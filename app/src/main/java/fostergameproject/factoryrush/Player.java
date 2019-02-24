package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class Player extends GameObject {
    final float MAX_X_VELOCITY = 10;
    boolean isPressingRight = false;
    boolean isPressingLeft = false;

    boolean restart = false; //tells FactoryView to restart the level

    boolean isMelee = true; //keeps track of which attack type is currently active
    boolean isDead = false;

    //jumping variables
    public boolean isFalling;
    public boolean isJumping;
    private long jumpTime;
    public boolean bonk = false;
    private long maxJumpTime = 900; //9/10ths of a second

    //sliding variables
    private long slideTime;
    private long maxSlideTime = 500; //1/2 a second
    final float SLIDE_VELOCITY = 12;

    //mercy invincibility variables
    final long INVINCIBILITY_TIME = 1500; //1.5 seconds mercy invincibility
    boolean invincible; //mercy invincibility flag
    private long invincibleStartTime;
    private long invincibleTime;

    //teleport variables
    private boolean canTele = true;
    private long teleTime;
    final long TELEPORT_DELAY = 2000; //teleport disables teleporting again for 2 seconds

    //animation variables
    ArrayList<Bitmap[]> playerAnimations;
    public int currentAnimation; //uses the final ints below to store the current animation
    public int currentAnimBitmap; //to store which bitmap of the current animation to draw
    public int currentAnimFrame; //to store how many frames the current bitmap has been used, for timing animations

    //Indices for the animation ArrayList
    final int IDLE = 0;
    final int RUN = 1;
    final int JUMP = 2;
    final int DEAD = 3;
    final int SHOOT = 4;
    final int SLIDE = 5;
    final int MELEE = 6;

    //stores the current shield type
    int shield = 0;
    //these are the same numbers as the equivalent shield pickup in level data for ease of remembering
    final int RED = 5;
    final int YELLOW = 6;
    final int BLUE = 7;


    Bitmap[] moveAnimation;
    Bitmap bulletBitmap;

    //Hitboxes
    RectHitbox hitboxFeet;
    RectHitbox hitboxLeft;
    RectHitbox hitboxRight;
    RectHitbox hitboxHead;

    public Gun gun;
    public int battery;

    //for time based drain of the battery
    private long batteryDrainTime; //the interval between instances of battery drain
    private long timeCurrFrame = 1;
    private long timeLastFrame = 1;

    Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre){
        //3/2 ratio for player
        final float HEIGHT = 3.2f;
        final float WIDTH = 2;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType('p');
        setxVelocity(0);
        setyVelocity(0);
        setFacing(RIGHT);
        isFalling = false;
        setMoves(true);
        setActive(true);
        setVisible(true);
        restart = false;
        invincible = false;

        hitboxFeet = new RectHitbox();
        hitboxLeft = new RectHitbox();
        hitboxRight = new RectHitbox();
        hitboxHead = new RectHitbox();

        gun = new Gun();
        battery = 100;
        batteryDrainTime = 4000; //drain occurs every 4 seconds

        currentAnimation = IDLE;
        currentAnimBitmap = 0;
        currentAnimFrame = 0;

        //prepare the animation bitmaps
        playerAnimations = new ArrayList<>();
        Bitmap[] anim = new Bitmap[10];
        for(int i = 0; i < anim.length; i++){
            String animName = "idle";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[8];
        for(int i = 0; i < anim.length; i++){
            String animName = "run";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[10];
        for(int i = 0; i < anim.length; i++){
            String animName = "jump";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[10];
        for(int i = 0; i < anim.length; i++){
            String animName = "dead";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[4];
        for(int i = 0; i < anim.length; i++){
            String animName = "shoot";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[10];
        for(int i = 0; i < anim.length; i++){
            String animName = "slide";
            anim[i] = this.prepareBitmap(context, animName + (i + 1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        anim = new Bitmap[8];
        for(int i = 0; i < anim.length; i++){
            String animName = "melee";
            anim[i] = this.prepareBitmap(context, animName + (i+1), pixelsPerMetre);
        }
        playerAnimations.add(anim);

        //create bullet bitmap, bullet bitmap stored here so draw method in FactoryView hase easy access
        int bulletID = context.getResources().getIdentifier("bullet", "drawable", context.getPackageName());
        bulletBitmap = BitmapFactory.decodeResource(context.getResources(), bulletID);
        bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, (int) (0.4 * pixelsPerMetre), (int) (0.3 * pixelsPerMetre), false);

        moveAnimation = new Bitmap[2];
        moveAnimation[0] = this.prepareBitmap(context, "idle1", pixelsPerMetre);


        //later we will have arrays of bitmaps for the animations, for now just an idle image for testing
        setBitmapName("idle1");

        setWorldLocation(worldStartX, worldStartY, 1);
    }

    public void update(long fps, float gravity){

        if(currentAnimation != DEAD) {
            //check teleport delay
            if(System.currentTimeMillis() - teleTime >= TELEPORT_DELAY){
                canTele = true;
            }
            //check battery
            if(battery <= 0){
                isDead = true;
                setxVelocity(0);
                return;
            } else { //only do if not dead
                //update mercy invincibility time
                if(invincible){
                    invincibleTime = System.currentTimeMillis() - invincibleStartTime;
                    if(invincibleTime >= INVINCIBILITY_TIME){
                        invincible = false;
                    }
                }
                //reset animation after an attack
                if((currentAnimation == SHOOT || currentAnimation == MELEE) &&
                        currentAnimBitmap == playerAnimations.get(currentAnimation).length - 1 &&
                        currentAnimFrame >= 2){
                    currentAnimBitmap = 0;
                    currentAnimFrame = 0;
                    currentAnimation = IDLE;
                }

                //set movement
                //preserve momentum through a jump
                if(currentAnimation != MELEE && currentAnimation != SHOOT && currentAnimation != SLIDE) {
                    if (isPressingRight) {
                        this.setxVelocity(MAX_X_VELOCITY);
                        if (currentAnimation != RUN) {//only set animation if was previously on a different one
                            currentAnimation = RUN;
                            currentAnimFrame = 0;
                            currentAnimBitmap = 0;
                        }
                    } else if (isPressingLeft) {
                        this.setxVelocity(-MAX_X_VELOCITY);
                        if (currentAnimation != RUN) {
                            currentAnimation = RUN;
                            currentAnimFrame = 0;
                            currentAnimBitmap = 0;
                        }
                    } else { //not moving, therefore idle
                        setxVelocity(0);
                        if (currentAnimation != IDLE) {
                            currentAnimation = IDLE;
                            currentAnimBitmap = 0;
                        }
                    }

                    //set facing direction
                    if (this.getxVelocity() > 0) {
                        setFacing(RIGHT);
                    } else if (this.getxVelocity() < 0) {
                        setFacing(LEFT);
                    }
                }else if(currentAnimation != SLIDE) {//is attacking
                    setxVelocity(0);
                }

                //Falling pose
                if (isFalling) {
                    currentAnimation = JUMP;
                    currentAnimFrame = 6;
                }

                //jumping and gravity
                if (isJumping) {
                    if (currentAnimation != JUMP && currentAnimation != DEAD) {
                        currentAnimation = JUMP;
                        currentAnimBitmap = 0;
                        currentAnimFrame = 0;
                    }
                    long timeJumping = System.currentTimeMillis() - jumpTime;
                    if (timeJumping < 70) {//animation frame based on how long player has been jumping
                        currentAnimBitmap = 0;
                    } else if (timeJumping < 100) {
                        currentAnimBitmap = 1;
                    } else if (timeJumping < 200) {
                        currentAnimBitmap = 2;
                    } else if (timeJumping < 300) {
                        currentAnimBitmap = 3;
                    } else if (timeJumping < 400) {
                        currentAnimBitmap = 4;
                    } else if (timeJumping < 500) {
                        currentAnimBitmap = 5;
                    } else if (timeJumping < 600) {
                        currentAnimBitmap = 6;
                    } else if (timeJumping < 700) {
                        currentAnimBitmap = 7;
                    } else if (timeJumping < 800) {
                        currentAnimBitmap = 8;
                    } else {
                        currentAnimBitmap = 9;
                    }
                    if (bonk) {//if the player bonks the ceiling while jumping, end upward movement
                        bonk = false;
                        setyVelocity(-gravity);
                    }else if (timeJumping < maxJumpTime) {
                        if (timeJumping < maxJumpTime / 2) {
                            this.setyVelocity(-gravity);
                        } else if (timeJumping >= maxJumpTime / 2) {
                            this.setyVelocity(gravity);
                        }
                    } else {
                        isJumping = false;
                    }
                } else {
                    this.setyVelocity(gravity);

                    //comment next line out to enable moonjumps
                    isFalling = true;
                }

                if(currentAnimation == SLIDE){
                    long timeSliding = System.currentTimeMillis() - slideTime;
                    if(timeSliding >= maxSlideTime){
                        currentAnimBitmap = 0;
                        currentAnimFrame = 0;
                        currentAnimation = IDLE;
                    }else{
                        currentAnimFrame++;
                        if(currentAnimFrame >= 4){
                            currentAnimBitmap++;
                            if(currentAnimBitmap > 9){
                                currentAnimBitmap = 0;
                            }
                        }
                    }
                }

                //handle non jumping / sliding animations
                if (currentAnimation != JUMP && currentAnimation != SLIDE) {
                    currentAnimFrame++;
                    if(currentAnimation == SHOOT){
                        if(currentAnimFrame == 4){
                            currentAnimFrame = 0;
                            currentAnimBitmap++;
                        }
                    }else if(currentAnimation == MELEE) {
                        if(currentAnimFrame == 3){
                            currentAnimFrame = 0;
                            currentAnimBitmap++;
                            if (currentAnimBitmap == playerAnimations.get(currentAnimation).length) {
                                currentAnimBitmap = 0;
                            }
                        }
                    }else if (currentAnimFrame == 5) {
                        currentAnimFrame = 0;
                        currentAnimBitmap++;
                        if (currentAnimBitmap == playerAnimations.get(currentAnimation).length) {
                            currentAnimBitmap = 0;
                        }
                    }
                }
                //update the gun
                gun.update(fps, gravity);
                //move the player
                this.move(fps);

                //update the hitbox locations
                Vector2Point5D location = getWorldLocation();
                float lx = location.x;
                float ly = location.y;

                hitboxFeet.top = ly + getHeight() * 0.95f;
                hitboxFeet.left = lx + getWidth() * 0.3f;
                hitboxFeet.bottom = ly + getHeight();
                hitboxFeet.right = lx + getWidth() * 0.7f;

                hitboxHead.top = ly + getHeight() *  0.05f;
                hitboxHead.left = lx + getWidth() * 0.4f;
                hitboxHead.bottom = ly + getHeight() * 0.2f;
                hitboxHead.right = lx + getWidth() * 0.6f;

                hitboxLeft.top = ly + getHeight() * 0.3f;
                hitboxLeft.left = lx + getWidth() * 0.2f;
                hitboxLeft.bottom = ly + getHeight() * 0.7f;
                hitboxLeft.right = lx + getWidth() * 0.3f;

                hitboxRight.top = ly + getHeight() * 0.3f;
                hitboxRight.left = lx + getWidth() * 0.7f;
                hitboxRight.bottom = ly + getHeight() * 0.7f;
                hitboxRight.right = lx + getWidth() * 0.8f;

                //drain the battery
                drainBattery();
            }
        } else{ //already dead, play animation and restart the level
            if(currentAnimBitmap < 9){
                currentAnimFrame++;
                if(currentAnimFrame >= 5){
                    currentAnimFrame = 0;
                    currentAnimBitmap++;
                }
            } else{
                //add restart level code here
                currentAnimBitmap = 9;
                restart = true;
            }
        }
    }

    //tests for collisions between the player and the hitbox passed in
    //function is called by main update method in FactoryView for each gameObject that is on screen
    //parameters are the hitbox to be tested and the type of the object that owns the hitbox, and if the object is a pickup
    //called by the FactoryView update method each frame for each active and visible GameObject
    //returns number representing where the collision is, or that there is no collision
    public int checkCollisions(RectHitbox hitbox, char type, boolean isPickup){
        int collided = 0; //no collision, 1 = left or right, 2 = feet, 3 = head

        //Test against each of the 4 player hitboxes (left, right, feet, head)
        if(this.hitboxLeft.intersects(hitbox)){
            if(type != 'd' && type != 'D') {
                if(!isPickup) {
                    this.setWorldLocationX(hitbox.right);
                    this.setPressingLeft(false);
                }
            }
            collided = 1;
        }

        if(this.hitboxRight.intersects(hitbox)){
            if(type != 'd' && type != 'D') {
                if(!isPickup) {
                    this.setWorldLocationX(hitbox.left - getWidth());
                    this.setPressingRight(false);
                }
            }
            collided = 1;
        }

        if(type != 'u' && type != 'y' && type != 'r') {//
            if (this.hitboxFeet.intersects(hitbox)) {
                if (type != 'd' && type != 'D') {
                    this.setWorldLocationY(hitbox.top - getHeight());
                }
                collided = 2;
                bonk = false;
                isFalling = false;
            }
        }

        if(currentAnimation != SLIDE) {//don't check for head collisions while sliding
            if (this.hitboxHead.intersects(hitbox)) {
                if (type != 'd' && type != 'D') {
                    this.setWorldLocationY(hitbox.bottom);
                    bonk = true;
                }
                collided = 3;
            }
        }
        return collided;
    }

    //These methods called by the input controller when the left/right movement button is pressed or released
    public void setPressingRight(boolean pressingRight){
        this.isPressingRight = pressingRight;
    }
    public void setPressingLeft(boolean pressingLeft){
        this.isPressingLeft = pressingLeft;
    }

    public int getCurrentAnimation(){
        return currentAnimation;
    }

    public int getCurrentAnimBitmap(){
        return currentAnimBitmap;
    }

    //gets the current bitmap to be drawn
    public Bitmap getBitmap(int currentAnimation, int currentAnimBitmap){
        Bitmap[] animation = playerAnimations.get(currentAnimation);
        return animation[currentAnimBitmap];
    }

    //start the jump animation, called by input controller when the jump button is pressed
    public void startJump(SoundManager sm){
        if(!isFalling){
            if(!isJumping){
                isJumping = true;
                jumpTime = System.currentTimeMillis();
                sm.playSound("jump");
            }
        }
    }

    //start the slide animation. called by input controller when the slide button is pressed
    public void startSlide(){
        if(!isFalling){
            if(currentAnimation == IDLE || currentAnimation == RUN){
                currentAnimBitmap = 0;
                currentAnimFrame = 0;
                currentAnimation = SLIDE;
                slideTime = System.currentTimeMillis();
                setxVelocity(SLIDE_VELOCITY * getFacing());
            }
        }
    }

    //called when the player collides with a teleporter
    //moves the player to the teleporter linked with the one they collided with if they are grounded
    //and have not teleported in the past 2 seconds
    public void teleport(Vector2Point5D destination){
        if(canTele && (currentAnimation == IDLE || currentAnimation == RUN)) {
            setWorldLocationX(destination.x);
            setWorldLocationY(destination.y);
            teleTime = System.currentTimeMillis();
            canTele = false;
        }
    }

    public boolean canTeleport(){
        return canTele;
    }

    //called by inputcontroller when the attack button is pressed. Starts either a melee or gun attack
    //based the the isMelee attribute
    public void attack(SoundManager sm){
        if(currentAnimation != SLIDE) {
            if (!isMelee) {
                //try to fire the gun
                if (gun.shoot(sm, this.getWorldLocation().x, this.getWorldLocation().y, getFacing(), getHeight())) {
                    if (currentAnimation != SHOOT) {
                        currentAnimation = SHOOT;
                        currentAnimBitmap = 0;
                        currentAnimFrame = 0;
                    }
                }
            } else {
                //make a melee attack
                if (currentAnimation != MELEE && currentAnimation != JUMP) {
                    battery -= 5;
                    currentAnimation = MELEE;
                    currentAnimBitmap = 0;
                    currentAnimFrame = 0;
                    sm.playSound("melee");
                }
            }
        }
    }

    //called by input controller when the weapon swap button is pressed, changes the isMelee attribute,
    //changing which attack is started by the attack funtion
    public void switchMode(SoundManager sm){
        //no point switching to the gun if there's no ammo, so stay in melee mode if there is no ammo
        if(isMelee && gun.getAmmoRemaining() > 0){
            isMelee = false;
        }else{
            isMelee = true;
        }
        sm.playSound("weapon_swap");
    }

    //handles the draining of the battery due to time
    public void drainBattery(){
        timeLastFrame = timeCurrFrame;
        timeCurrFrame = System.currentTimeMillis();
        if(timeLastFrame != 1){
            batteryDrainTime -= (timeCurrFrame - timeLastFrame);
        }
        if(batteryDrainTime <= 0){
            batteryDrainTime = 4000;
            battery -= 5;
        }
    }

    //drains the battery when damaged
    public void damaged(SoundManager sm){

        if(!invincible) {
            battery -= 5;
            invincible = true;
            invincibleStartTime = System.currentTimeMillis();
            sm.playSound("player_hit");
        }
    }

    public int getShield(){
        return shield;
    }

    //called when the player picks up a shield powerup,
    // changes the shield attribute based on the color of the powerup collected
    public void setShield(int color, SoundManager sm){

        shield = color;
        sm.playSound("shield_pickup");
    }

    //called when the player collects a battery pickup, refills the battery
    public void fillBattery(){
        battery = 100;
    }

    //called when the player collides with an acid tile, or by update when the battery hits 0
    //kills the player
    public void killed(SoundManager sm){
        if(currentAnimation != DEAD) {
            sm.playSound("battery_dead");
            currentAnimation = DEAD;
            currentAnimFrame = 0;
            currentAnimBitmap = 1;
        }
    }

    //called by the FactoryView update method to let it know when the player has finished their death animation
    //so it can restart the level
    public boolean restartLevel(){
        return restart;
    }
}
