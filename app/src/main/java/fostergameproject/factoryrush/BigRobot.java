package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

//A robot that patrols back and forth on its platform, takes multiple hits from the gun to destroy
//Immune to melee
public class BigRobot extends GameObject {

    final float MAX_X_VELOCITY = 5;

    ArrayList<Bitmap[]> animations;//2D array that stores all of the bitmaps used for the animations
    public int currentAnimation;//index of the current animation
    public int currentAnimBitmap;//index of the current bitmap of the current animation
    public int currentAnimFrame;//current number of frames on current bitmap

    public int hp = 3;//hits remaining until destroyed

    //index numbers for the animations
    final int MOVE = 0;
    final int DEAD = 1;

    //The outer limits of the patrol pth
    float leftBound;
    float rightBound;

    BigRobot(Context context, int pixelsPerMetre, float worldStartX, float worldStartY, float leftBound, float rightBound){
        final float HEIGHT = 3;
        final float WIDTH = 2;

        this.leftBound = leftBound;
        this.rightBound = rightBound;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType('b');
        setMoves(true);
        setWorldLocation(worldStartX, worldStartY, 0);
        setBitmapName("br_idle1");
        setHitbox();
        setFacing(LEFT);
        setxVelocity(-MAX_X_VELOCITY);

        currentAnimation = MOVE;
        currentAnimBitmap = 0;
        currentAnimFrame = 0;

        //set up animation bitmaps
        //name scheme for these is "br_(ANIM)(X), where ANIM is the name of the animation and X is the bitmap #
        //eg. 3rd bitmap in the move animation is br_move3
        animations = new ArrayList<>();
        Bitmap[] anim = new Bitmap[5];
        for(int i = 0; i < anim.length; i++){
            String animName = "br_move";
            anim[i] = this.prepareBitmap(context, animName + (i+1), pixelsPerMetre);
        }
        animations.add(anim);

        anim = new Bitmap[9];
        for(int i = 0; i < anim.length; i++){
            String animName = "br_dead";
            anim[i] = this.prepareBitmap(context, animName + (i+1), pixelsPerMetre);
        }
        animations.add(anim);

    }

    public void update(long fps, float gravity){
        if(currentAnimation != DEAD){
            if(currentAnimation == MOVE){
                //currently always true unless robot is dead
                //this if statement is here anyway to make things easier if
                //robot behavior is changed later
                currentAnimFrame++;
                if(currentAnimFrame == 5){
                    currentAnimFrame = 0;
                    currentAnimBitmap++;
                    if(currentAnimBitmap == animations.get(MOVE).length){
                        currentAnimBitmap = 0;
                    }
                }
                //check if the robot needs to turn around
                if (getFacing() == LEFT) {
                    if (getWorldLocation().x <= leftBound) {
                        setFacing(RIGHT);
                        setxVelocity(MAX_X_VELOCITY);
                    }
                } else {//facing right
                    if (getWorldLocation().x >= rightBound) {
                        setFacing(LEFT);
                        setxVelocity(-MAX_X_VELOCITY);
                    }
                }
                move(fps);
                setHitbox();
            }
        }else{//if robot is dead, play the death animation, then make the robot invisible
            if(hp == 0 && currentAnimBitmap >= 8 && currentAnimFrame >= 4){
                setVisible(false);
                setActive(false);
            }else{
                currentAnimFrame++;
                if(currentAnimFrame == 5){
                    currentAnimFrame = 0;
                    currentAnimBitmap++;
                }
            }
        }
    }

    //Called by the draw method in FactoryView to get the current bitmap to be drawn
    public Bitmap getBitmap(int currentAnimation, int currentAnimBitmap){
        Bitmap[] animation = animations.get(currentAnimation);
        if(currentAnimBitmap < animation.length) {
            return animation[currentAnimBitmap];
        }
        return animation[0];//to prevent crashing from loading a bitmap that doesn't exist
    }

    //Called by the bullet hit detection code in the FactoryView Update method to decrease the hp of the robot
    //and then set the death flag if the robot is out of hp
    public void damaged() {
        if (hp > 0){
            hp--;
        }
        if(hp == 0){
            currentAnimBitmap = 0;
            currentAnimFrame = 0;
            currentAnimation = DEAD;
        }
    }

    //This method has the robot back off a bit when it hits the player
    //to avoid pinching the player through a wall, parameter is the x position of the player
    //used to determine which direction the robot needs to move
    //Method is called by the FactoryView update method when the robot collides with the player
    public void damageTurn(float px){
        if(getWorldLocation().x < px && getFacing() == RIGHT){
            setFacing(LEFT);
            setxVelocity(-MAX_X_VELOCITY);
        }else if(getWorldLocation().x > px && getFacing() == LEFT){
            setFacing(RIGHT);
            setxVelocity(MAX_X_VELOCITY);
        }
    }
}
