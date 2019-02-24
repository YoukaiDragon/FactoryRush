package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

//Main class handling operation of the game
public class FactoryView extends SurfaceView implements Runnable {
    //Set this to true to enable debug info and start on the test level
    private boolean debugging = false;

    private volatile boolean running;
    private Thread gameThread = null;

    //data for restarting level
    private String currentLevel; //for restarting level in case of death
    private float startX;
    private float startY;
    public boolean gameStart = true; //so game knows when to have the title screen up
    boolean gameOver = false; //boolean so the game knows when to go back to the title screen

    //start coordinates for the test level in debug mode
    float debugStartX = 17;
    float debugStartY = 22;

    //Drawing objects
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    Context context;

    //debug variables for the BigRobot
    int roboHP;
    int roboAnim = 0;
    int roboFrame = 0;
    boolean roboVisible = true;
    boolean roboActive = true;
    //debug variable for flying robot
    float robotWaypointX = 0;
    float robotWaypointY = 0;

    //for getting fps
    long startFrameTime;
    long timeThisFrame;
    long fps;

    //Engine classes
    private LevelManager lm;
    private Viewport vp;
    private InputController ic;
    SoundManager sm;

    //Bitmap for the background
    Bitmap background;
    float backgroundX1 = 0;
    float backgroundX2 = 0;

    FactoryView(Context context, int screenWidth, int screenHeight){
        super(context);
        this.context = context;

        //initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        //initialize the viewport
        vp = new Viewport(screenWidth, screenHeight);
        backgroundX2 = vp.getScreenWidth();

        //initialize the soundmanager and load sound effects
        sm = new SoundManager();
        sm.loadSound(context);

        //prepare the bitmap for the background
        int backgroundResID = context.getResources().getIdentifier("background", "drawable", context.getPackageName());
        background = BitmapFactory.decodeResource(context.getResources(), backgroundResID);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);

        //load the first level
        //if in debug mode, load the test level instead
        //if you want to start at a different level for testing, this is the code to change
        if(debugging) {
            loadLevel("LevelTest", debugStartX, debugStartY);
        }else{
            loadLevel("LevelOne", 8, 20);

            //Commented out loadLevel calls for the other levels to start on that level for playtesting
            //loadLevel("LevelTwo", 6, 18);
            //loadLevel("LevelThree", 9, 33);
            //loadLevel("LevelFour", 12, 27);
            //loadLevel("LevelFive",13,35);
        }
    }

    @Override
    public void run(){
        while(running){
            //get the time at the start of the frame
            startFrameTime = System.currentTimeMillis();

            //update game objects, then draw the screen
            update();
            draw();

            //get the time at the end of the frame and calculate fps
            //this is used to time animations and movement
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1){
                fps = 1000 / timeThisFrame;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        if(lm != null){
            //if at the start menu, begin the game, else send to the InputController
            if(gameStart){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameStart = false;
                    lm.switchPlayingStatus();
                }
            }else if(gameOver){
                //If game is over, go back to the menu and prepare the first level
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    gameOver = false;
                    gameStart = true;
                    loadLevel("LevelOne", 8, 20);
                }
            }else{
                ic.handleInput(motionEvent, lm, sm, vp);
            }
        }
        //invalidate();
        return true;
    }

    //updates the active game objects and detects collisions with the player, called every frame by run()
    //calls update on all game objects, as well as functions from game objects to get info or manipulate the state of game objects
    private void update(){
        //no need to to anything else if on the main menu
        if(gameStart || gameOver){
            return;
        }
        //if player battery has hit 0, kill the player
        if(lm.player.isDead){
            lm.player.killed(sm);
        }
        //if the player has died, restart the level
        if(lm.player.restartLevel()){
            loadLevel(currentLevel, startX, startY);
            return;
        }
        for(GameObject go : lm.gameObjects){

            //disable collisions for lasers when the player has the correct shield,
            if(go.getType() == 'u'){
                if(lm.player.getShield() == lm.player.BLUE){
                    go.setActive(false);
                }else{
                    go.setActive(true);
                }
            }else if(go.getType() == 'y'){
                if(lm.player.getShield() == lm.player.YELLOW){
                    go.setActive(false);
                }else{
                    go.setActive(true);
                }
            }else if(go.getType() == 'r'){
                if(lm.player.getShield() == lm.player.RED){
                    go.setActive(false);
                }else{
                    go.setActive(true);
                }

            }else if(go.getType() == 'b' && debugging){
                //debug info for big robot
                roboActive = go.isActive();
                roboVisible = go.isVisible();
            }
            //Disable collisions with the teleports when the player is not able to teleport
            //and when the player is not grounded
            if(go.getType() == 't'){
                if(lm.player.canTeleport() &&
                        (lm.player.currentAnimation == lm.player.IDLE || lm.player.currentAnimation == lm.player.RUN)){
                    go.setActive(true);
                }else{
                    go.setActive(false);
                }
                go.setVisible(true);
            }

            //only perform hit detection for active objects
            if (go.isActive()) {
                //update player location for enemies that use it
                if(go.getType() == 'f'){
                    FlyingRobot fRobot = (FlyingRobot) go;
                    fRobot.updatePlayerLocation(lm.player.getWorldLocation().x, lm.player.getWorldLocation().y);
                    if(debugging) {
                        robotWaypointX = fRobot.waypointX;
                        robotWaypointY = fRobot.waypointY;
                    }
                }else if(go.getType() == 'j'){
                    JumpingRobot jRobot = (JumpingRobot) go;
                    jRobot.updatePlayerLocation(lm.player.getWorldLocation().x);
                }
                //only check collisions with objects that are visible
                if(!vp.clipObjects(go.getWorldLocation().x, go.getWorldLocation().y, go.getWidth(), go.getHeight())){
                    go.setVisible(true);

                    //check collisions with player
                    int hit;
                    hit = lm.player.checkCollisions(go.getHitbox(), go.getType(), go.isPickup());

                    if(hit > 0){//actions based on collision with player
                        switch(go.getType()){//take action based on what the player collided with
                            case 'a': //acid tile
                                lm.player.killed(sm);
                                break;
                            case 'A': //acid surface tile
                                lm.player.killed(sm);
                                break;
                            case 'd': //level exit
                                if(lm.door.isUnlocked()){
                                    //load the next level based on the current level and whether the game is in debug mode
                                    if(debugging){
                                        sm.playSound("level_complete");
                                        loadLevel("LevelTest", debugStartX, debugStartY);
                                    }else{
                                        if(currentLevel == "LevelOne"){
                                            sm.playSound("level_complete");
                                            loadLevel("LevelTwo", 6, 18);
                                        }else if(currentLevel == "LevelTwo"){
                                            sm.playSound("level_complete");
                                            loadLevel("LevelThree", 9, 33);
                                        }else if(currentLevel == "LevelThree"){
                                            sm.playSound("level_complete");
                                            loadLevel("LevelFour", 12, 27);
                                        }else if(currentLevel == "LevelFour"){
                                            sm.playSound("level_complete");
                                            loadLevel("LevelFive",13,35);
                                        }else if(currentLevel == "LevelFive"){
                                            sm.playSound("level_complete");
                                            gameStart = false;
                                            gameOver = true;
                                            return;
                                        }
                                    }
                                }
                                break;
                            case '4': //battery pickup
                                lm.player.fillBattery();
                                go.setVisible(false);
                                go.setActive(false);
                                break;
                            case '5': //red shield
                                lm.player.setShield(5, sm);
                                go.setVisible(false);
                                go.setActive(false);
                                break;
                            case '6': //yellow shield
                                lm.player.setShield(6, sm);
                                go.setVisible(false);
                                go.setActive(false);
                                break;
                            case '7': //blue shield
                                lm.player.setShield(7, sm);
                                go.setVisible(false);
                                go.setActive(false);
                                break;
                            case 'r': //red laser
                            case 'y': //yellow laser
                            case 'u': //blue laser
                                lm.player.damaged(sm);
                                break;
                            case 'b': //big robot
                                lm.player.damaged(sm);
                                BigRobot br = (BigRobot) go;
                                br.damageTurn(lm.player.getWorldLocation().x);
                                break;
                            case '8': //ammo pickup
                                lm.player.gun.reloadAmmo(sm);
                                go.setVisible(false);
                                go.setActive(false);
                                break;
                            case 'D': //door switch
                                DoorSwitch doorSwitch = (DoorSwitch) go;
                                doorSwitch.unlock();
                                lm.door.unlock();
                                break;
                            case 'f': //flying robot
                                lm.player.damaged(sm);
                                FlyingRobot fR = (FlyingRobot) go;
                                fR.damageTurn(lm.player.getWorldLocation().x);
                                break;
                            case 'j': //jumping robot
                                lm.player.damaged(sm);
                                JumpingRobot jR = (JumpingRobot) go;
                                jR.damageTurn();
                                break;
                            case 't': //teleporter
                                Teleporter tele = (Teleporter) go;
                                lm.player.teleport(tele.destination);
                            default://probably a regular tile
                                if(hit == 1) {//left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                    lm.player.setPressingLeft(false);
                                }
                                if(hit == 2){ //feet
                                    lm.player.isFalling = false;
                                }if(hit == 3) { //head
                                    lm.player.isJumping = false;
                                    lm.player.setWorldLocationY(lm.player.getWorldLocation().y -  0.3f);
                                }
                                break;
                        }
                    }
                    //check melee attack collisions
                    if(lm.player.currentAnimation == lm.player.MELEE){
                        //make a hitbox next to the player in the direction they are facing
                        RectHitbox melee = new RectHitbox();
                        if(lm.player.getFacing() == lm.player.RIGHT){
                            melee.setLeft(lm.player.getWorldLocation().x + lm.player.getWidth());
                            melee.setTop(lm.player.getWorldLocation().y + lm.player.getHeight() * 0.2f);
                            melee.setRight(lm.player.getWorldLocation().x + lm.player.getWidth() + lm.player.getWidth() * 0.2f);
                            melee.setBottom(lm.player.getWorldLocation().y + lm.player.getHeight() * 0.8f);
                        }else{//facing left
                            melee.setLeft(lm.player.getWorldLocation().x - lm.player.getWidth() * 0.2f);
                            melee.setTop(lm.player.getWorldLocation().y + lm.player.getHeight() * 0.2f);
                            melee.setRight(lm.player.getWorldLocation().x);
                            melee.setBottom(lm.player.getWorldLocation().y + lm.player.getHeight() * 0.8f);
                        }
                        //handle collision based on GameObject type
                        if(go.getHitbox().intersects(melee)){
                            if(go.getType() == 'f'){
                                FlyingRobot fRobot = (FlyingRobot) go;
                                fRobot.dead();
                                sm.playSound("enemy_hit");
                            }else if(go.getType() == 'j'){
                                JumpingRobot jRobot = (JumpingRobot) go;
                                jRobot.dead();
                                sm.playSound("enemy_hit");
                            }else if(go.getType() == 'x'){
                                Box box = (Box) go;
                                box.pushed(lm.player.getFacing());
                            }else if(go.getType() == 'b'){//big robot immune to melee
                                sm.playSound("clink");
                            }else if(go.getType() == 'j'){
                                JumpingRobot jRobot = (JumpingRobot) go;
                                jRobot.dead();
                                sm.playSound("enemy_hit");
                            }
                        }
                    }

                    //check bullet collisions
                    for(int i = 0; i < lm.player.gun.getNumBullets(); i++){
                        //make a hitbox for the current bullet
                        RectHitbox r = new RectHitbox();
                        r.setLeft(lm.player.gun.getBulletX(i));
                        r.setTop(lm.player.gun.getBulletY(i));
                        r.setRight(lm.player.gun.getBulletX(i)+0.4f);
                        r.setBottom(lm.player.gun.getBulletY(i)+0.3f);
                        if(go.getHitbox().intersects(r)){
                            //collision detected, hide bullet and take action based on type of GameObject
                            if(go.getType() == '1') {
                                //bullet stops at platforms
                                lm.player.gun.hideBullet(i);
                                sm.playSound("clink");
                            }else if(go.getType() == 'b'){
                                BigRobot bigRobo = (BigRobot) go;
                                bigRobo.damaged();
                                lm.player.gun.hideBullet(i);
                                sm.playSound("enemy_hit");
                            }else if(go.getType() == 'D'){
                                DoorSwitch doorSwitch = (DoorSwitch) go;
                                doorSwitch.unlock();
                                lm.door.unlock();
                                lm.player.gun.hideBullet(i);
                            }else if(go.getType() == 'f'){
                                FlyingRobot fRobot = (FlyingRobot) go;
                                fRobot.dead();
                                lm.player.gun.hideBullet(i);
                                sm.playSound("enemy_hit");
                            }else if(go.getType() == 'j'){
                                JumpingRobot jRobot = (JumpingRobot) go;
                                jRobot.dead();
                                lm.player.gun.hideBullet(i);
                                sm.playSound("enemy_hit");
                            }else if(go.getType() == 'u' || go.getType() == 'y' || go.getType() == 'r'){
                                lm.player.gun.hideBullet(i);
                                sm.playSound("clink");
                            }
                        }
                        //make hitbox to allow jumping robot to know when to jump over a bullet
                        if(go.getType() == 'j'){
                            RectHitbox jumpBox = new RectHitbox();
                            if(lm.player.getWorldLocation().x < go.getWorldLocation().x){
                                //robot is to the right of the player
                                jumpBox.setLeft(go.getWorldLocation().x - 8f);
                                jumpBox.setRight(go.getWorldLocation().x - 1f);
                            }else{
                                //robot is to the left of the player
                                jumpBox.setLeft(go.getWorldLocation().x + 1f);
                                jumpBox.setRight(go.getWorldLocation().x + 8f);
                            }
                            jumpBox.setTop(go.getHitbox().top);
                            jumpBox.setBottom(go.getHitbox().bottom);

                            if(jumpBox.intersects(r)){
                                JumpingRobot jRobot = (JumpingRobot) go;
                                jRobot.startJump();
                            }
                        }
                    }
                    if(lm.isPlaying()){
                        //run un-clipped updates
                        go.update(fps, lm.gravity);

                        //scroll the background
                        float backgroundScroll = 0.01f * lm.player.getxVelocity();
                        backgroundX1 -= backgroundScroll;
                        backgroundX2 -= backgroundScroll;
                        if(backgroundX1 > vp.getScreenWidth()){
                            backgroundX1 -= 2*vp.getScreenWidth();
                        }else if(backgroundX2 > vp.getScreenWidth()){
                            backgroundX2 -= 2*vp.getScreenWidth();
                        }else if(backgroundX1 < -vp.getScreenWidth()){
                            backgroundX1 += 2*vp.getScreenWidth();
                        }else if(backgroundX2 < -vp.getScreenWidth()){
                            backgroundX2 += 2*vp.getScreenWidth();
                        }
                    }
                } else{
                    go.setVisible(false);
                }
            }
        }

        if(lm.isPlaying()){
            vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                    lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
        }
    }

    //Method called every frame to draw objects to the screen, called every frame by run()
    private void draw(){
        if(surfaceHolder.getSurface().isValid()){
            //lock the area of memory
            canvas = surfaceHolder.lockCanvas();

            //Clear the last frame
            paint.setColor(Color.argb(255,0,0,255));
            canvas.drawColor(Color.argb(255,0,0,255));
            //draw the background
            canvas.drawBitmap(background, backgroundX1, 0, paint);
            canvas.drawBitmap(background, backgroundX2, 0, paint);


            //if on the game over screen, draw it
            if(gameOver){
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawText("GAME OVER", vp.getScreenWidth()/2, vp.getScreenHeight() / 4, paint);
                canvas.drawText("Thanks for playing", vp.getScreenWidth()/2, vp.getScreenHeight()/2, paint);
                paint.setTextSize(30);
                canvas.drawText("Touch Screen to return to title", vp.getScreenWidth()/2, vp.getScreenHeight()*0.75f, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                return;
            }else if(gameStart){
                //if on the start menu, draw it
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawText("FACTORY RUSH", vp.getScreenWidth()/2, vp.getScreenHeight()/2, paint);
                paint.setTextSize(30);
                canvas.drawText("Touch Screen to Begin", vp.getScreenWidth()/2, vp.getScreenHeight()*0.75f, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                return;
            }

            //Prepare the Rect object used to help draw the bitmaps
            Rect toScreen2D = new Rect();

            //draw all game objects, starting at the lowest layer
            for(int layer = -1; layer <= 1; layer++){
                for(GameObject go : lm.gameObjects){
                    if(go.isVisible() && go.getWorldLocation().z == layer){//only draw if object is visible and on current layer
                        //convert the world location to a screen position
                        toScreen2D.set(vp.worldToScreen(go.getWorldLocation().x,
                                go.getWorldLocation().y, go.getWidth(), go.getHeight()));
                        //For objects with multiple possible bitmaps, find and draw the current bitmap
                        if(go.getType() == 'p'){
                            Bitmap anim = lm.player.getBitmap(lm.player.currentAnimation, lm.player.getCurrentAnimBitmap());
                            if(go.getFacing() == go.RIGHT){
                                canvas.drawBitmap(anim, toScreen2D.left, toScreen2D.top, paint);
                            }else{//flip the bitmap if needed
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Bitmap b = Bitmap.createBitmap(anim, 0, 0, anim.getWidth(), anim.getHeight(), flipper, true);
                                canvas.drawBitmap(b,toScreen2D.left, toScreen2D.top, paint);
                            }
                        }else if(go.getType() == 'b') {
                            BigRobot bigRobo = (BigRobot) go;
                            //get debug info
                            roboHP = bigRobo.hp;
                            roboAnim = bigRobo.currentAnimBitmap;
                            roboFrame = bigRobo.currentAnimFrame;
                            //draw the robot
                            Bitmap anim = bigRobo.getBitmap(bigRobo.currentAnimation, bigRobo.currentAnimBitmap);
                            if(bigRobo.getFacing() == bigRobo.LEFT){//default facing for robot is left
                                canvas.drawBitmap(anim, toScreen2D.left, toScreen2D.top, paint);
                            }else{
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Bitmap b = Bitmap.createBitmap(anim, 0, 0, anim.getWidth(), anim.getHeight(), flipper, true);
                                canvas.drawBitmap(b, toScreen2D.left, toScreen2D.top, paint);
                            }
                        }else if(go.getType() == 'd') {
                            if(lm.door.isUnlocked()){
                                canvas.drawBitmap(lm.door.doorOpen, toScreen2D.left, toScreen2D.top, paint);
                            }else{
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2D.left,
                                        toScreen2D.top, paint);
                            }
                        }else if(go.getType() == 'D'){
                            DoorSwitch doorSwitch = (DoorSwitch) go;
                            if(doorSwitch.isUnlocked()){
                                canvas.drawBitmap(doorSwitch.switchOn, toScreen2D.left, toScreen2D.top, paint);
                            }else{
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2D.left,
                                        toScreen2D.top, paint);
                            }
                        }else if(go.getType() == 'f') {
                            FlyingRobot fRobot = (FlyingRobot) go;
                            if(fRobot.getFacing() == fRobot.LEFT){
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2D.left, toScreen2D.top, paint);
                            }else{
                                Bitmap fR = fRobot.bitmap;
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Bitmap b = Bitmap.createBitmap(fR, 0, 0, fR.getWidth(), fR.getHeight(), flipper, true);
                                canvas.drawBitmap(b, toScreen2D.left, toScreen2D.top, paint);
                            }
                        }else if(go.getType() == 'j'){
                            JumpingRobot jRobot = (JumpingRobot) go;
                            Bitmap jR = jRobot.getBitmap(jRobot.currentAnimation, jRobot.currentAnimBitmap);
                            if(jRobot.getFacing() == jRobot.RIGHT){
                                canvas.drawBitmap(jR, toScreen2D.left, toScreen2D.top, paint);
                            }else{
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Bitmap b = Bitmap.createBitmap(jR, 0, 0, jR.getWidth(), jR.getHeight(), flipper, true);
                                canvas.drawBitmap(b, toScreen2D.left, toScreen2D.top, paint);
                            }
                        }else{//for everything with a single bitmap
                            canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2D.left,
                                    toScreen2D.top, paint);
                        }
                    }
                }
            }

            //draw the bullets
            for(int i = 0; i < lm.player.gun.getNumBullets(); i++){
                toScreen2D.set(vp.worldToScreen(lm.player.gun.getBulletX(i), lm.player.gun.getBulletY(i), 0.4f, 0.3f));
                canvas.drawBitmap(lm.player.bulletBitmap, toScreen2D.left, toScreen2D.top, paint);
            }

            //draw debugging text
            if(debugging){
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawText("fps:" + fps, 10, 60, paint);
                canvas.drawText("num objects:" + lm.gameObjects.size(), 10, 80, paint);
                canvas.drawText("num clipped:" + vp.getNumClipped(), 10, 100, paint);
                canvas.drawText("playerX:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, 10, 120, paint);
                canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y, 10, 140, paint);
                canvas.drawText("battery:" + lm.player.battery, 10, 160, paint);
                canvas.drawText("player state:" + lm.player.currentAnimation, 10, 180, paint);
                canvas.drawText("player shield:" + lm.player.shield, 10, 200, paint);
                canvas.drawText("door unlocked:" + lm.door.isUnlocked(), 10, 220, paint);
                canvas.drawText("isMelee:" + lm.player.isMelee, 10, 240, paint);
                //canvas.drawText("Flying Robot Waypoint X:" + robotWaypointX,10, 260, paint);
                //canvas.drawText("Flying Robot Waypoint Y:" + robotWaypointY, 10, 280, paint);
                //canvas.drawText("Big Robot HP:" + roboHP, 10, 300, paint);
                //canvas.drawText("Big Robot active:" + roboActive, 10, 320, paint);
                //canvas.drawText("Big Robot Visible:" + roboVisible, 10, 340, paint);
                //canvas.drawText("Big Robot anim:" + roboAnim, 10, 360, paint);
                //canvas.drawText("Big Robot frame:" + roboFrame, 10, 380, paint);

                //reset the number of clipped objects each frame
                vp.resetNumClipped();
            }else{
                //draw the HUD text
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawText("Battery:" + lm.player.battery, 10, 40, paint);
                if(lm.player.isMelee){
                    canvas.drawText("Weapon: Melee", 10, 60, paint);
                }else{
                    canvas.drawText("Weapon: Gun", 10, 60, paint);
                }
                canvas.drawText("Ammo:" + lm.player.gun.getAmmoRemaining(), 10, 80, paint);
                if(lm.player.getShield() == lm.player.BLUE){
                    canvas.drawText("Shield: Blue", 10, 100, paint);
                }else if(lm.player.getShield() == lm.player.RED){
                    canvas.drawText("Shield: Red", 10, 100, paint);
                }else if(lm.player.getShield() == lm.player.YELLOW){
                    canvas.drawText("Shield: Orange", 10, 100, paint);
                }else{
                    canvas.drawText("Shield: None", 10, 100, paint);
                }
            }

            //draw the control buttons
            ArrayList<Rect> buttonsToDraw = ic.getButtons();
            int buttonNum = 0;
            for(Rect rect : buttonsToDraw){
                paint.setColor(Color.argb(80,255,255,255));
                RectF rf = new RectF(rect.left, rect.top, rect.right, rect.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
                paint.setColor(Color.argb(255,255,255,255));
                paint.setTextAlign(Paint.Align.CENTER);
                float textX = rect.left + ((rect.right - rect.left) / 2);
                float textY = rect.top + ((rect.bottom - rect.top) / 2);
                //add text to button
                switch(buttonNum) {
                    case 0:
                        canvas.drawText("<--", textX, textY, paint);
                        break;
                    case 1:
                        canvas.drawText("-->", textX, textY, paint);
                        break;
                    case 2:
                        canvas.drawText("slide", textX, textY, paint);
                        break;
                    case 3:
                        canvas.drawText("swap", textX, textY, paint);
                        break;
                    case 4:
                        canvas.drawText("atk", textX, textY, paint);
                        break;
                    case 5:
                        canvas.drawText("jump", textX, textY, paint);
                        break;
                    case 6:
                        canvas.drawText("pause", textX, textY, paint);
                        break;
                }
                buttonNum++;
            }

            //draw pause screen
            if(!this.lm.isPlaying()){
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255,255,255,255));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2, vp.getScreenHeight() / 2, paint);
            }

            //unlock and draw
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    //Set up a levelManager, InputController, and the viewPort for a level
    //input variables are the name of the LevelData class to be loaded, and the x and y
    //coordinates the player will spawn at
    //called by Factory view when loading the game, when the player dies, and when the player completes a level
    public void loadLevel(String level, float px, float py){
        lm = null;
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());
        lm = new LevelManager(context, vp.getPixelsPerMetreX(), vp.getScreenWidth(), ic, level, px, py);

        vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
        currentLevel = level;
        startX = px;
        startY = py;
        if(lm.isPlaying()){
            lm.switchPlayingStatus();
        }
    }

    public void pause(){
        running = false;

        try{
            gameThread.join();
        }catch(InterruptedException e){
            Log.e("error","failed to pause thread");
        }
    }

    public void resume(){
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
