package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

//This is an enemy that will chase the player when they are within
//it's territory. it will also jump when the player shoots at it with the gun
public class JumpingRobot extends GameObject{

    final float MAX_X_VELOCITY = 12;
    final long MAX_JUMP_TIME = 600;
    private long jumpTime;
    private float startY;
    private float px;

    ArrayList<Bitmap[]> animations;//2D array that stores all of the bitmaps used for the animations
    public int currentAnimation;//index of the current animation
    public int currentAnimBitmap;//index of the current bitmap of the current animation
    public int currentAnimFrame;//current number of frames on current bitmap
    final int IDLE = 0;
    final int MOVE = 1;
    final int JUMP = 2;

    private boolean jumping;
    final long MERCY_TIME = 1000;
    private long mercyTime = 0;
    private float leftBound;
    private float rightBound;

    JumpingRobot(Context context, int pixelsPerMetre, float worldStartX, float worldStartY, char type, float leftBound, float rightBound){
        final float HEIGHT = 2;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setWorldLocation(worldStartX, worldStartY, 0);
        setMoves(true);
        setFacing(RIGHT);
        setBitmapName("jump_robot");
        setHitbox();
        jumping = false;

        currentAnimation = IDLE;
        currentAnimBitmap = 0;
        currentAnimFrame = 0;

        startY = worldStartY;

        //Prepare the animation bitmaps
        animations = new ArrayList<>();

        Bitmap[] anim = new Bitmap[1];
        anim[0] = this.prepareBitmap(context, "jump_robot", pixelsPerMetre);
        animations.add(anim);

        anim = new Bitmap[4];
        anim[0] = this.prepareBitmap(context, "jump_robot_walk1", pixelsPerMetre);
        anim[1] = this.prepareBitmap(context,"jump_robot_walk2", pixelsPerMetre);
        anim[2]= this.prepareBitmap(context, "jump_robot_walk3", pixelsPerMetre);
        anim[3] = this.prepareBitmap(context,"jump_robot_walk2", pixelsPerMetre);
        animations.add(anim);

        anim = new Bitmap[3];
        anim[0] = this.prepareBitmap(context,"jump_robot_jump1", pixelsPerMetre);
        anim[1] = this.prepareBitmap(context,"jump_robot_jump2", pixelsPerMetre);
        anim[2] = this.prepareBitmap(context,"jump_robot_jump3", pixelsPerMetre);
        animations.add(anim);


        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public void update(long fps, float gravity){
        //handle the jump if needed
        if(jumping){
            currentAnimation = JUMP;
            long timeJumping = System.currentTimeMillis() - jumpTime;
            if(timeJumping <= MAX_JUMP_TIME / 2){
                setyVelocity(-gravity * 2f);
                if(timeJumping < 100){
                    currentAnimBitmap = 0;
                }else if(timeJumping < 200){
                    currentAnimBitmap = 1;
                }else{
                    currentAnimBitmap = 2;
                }
            }else if(timeJumping <= MAX_JUMP_TIME){
                setyVelocity(gravity * 1.6f);
            }else{
                setWorldLocationY(startY);
                setyVelocity(0);
                jumping = false;
                currentAnimBitmap = 0;
                currentAnimFrame = 0;
                currentAnimation = IDLE;
            }
        }

        //move the robot if the player is close enough
        if(getWorldLocation().x > px){//player is left of the robot
            if(System.currentTimeMillis() - mercyTime <= MERCY_TIME){
                setxVelocity(MAX_X_VELOCITY);
                setFacing(RIGHT);
            }else if(px > leftBound){
                setxVelocity(-MAX_X_VELOCITY);
                setFacing(LEFT);
            }else{
                setxVelocity(0);
                currentAnimBitmap = 0;
                currentAnimFrame = 0;
                currentAnimation = IDLE;
            }
        }else if(getWorldLocation().x < px){//player is right of the robot
            if(System.currentTimeMillis() - mercyTime <= MERCY_TIME){
                setxVelocity(-MAX_X_VELOCITY);
                setFacing(LEFT);
            }else if(px < rightBound){
                setxVelocity(MAX_X_VELOCITY);
                setFacing(RIGHT);
            }else{
                setxVelocity(0);
                currentAnimBitmap = 0;
                currentAnimFrame = 0;
                currentAnimation = IDLE;
            }
        }
        //stay in the boundaries
        if(getWorldLocation().x < leftBound){
            setxVelocity(MAX_X_VELOCITY);
            setFacing(RIGHT);
        }else if(getWorldLocation().x > rightBound){
            setxVelocity(-MAX_X_VELOCITY);
            setFacing(LEFT);
        }

        //handle movement animation
        if(getxVelocity() != 0 && currentAnimation == IDLE){
            currentAnimBitmap = 0;
            currentAnimFrame = 0;
            currentAnimation = MOVE;
        }
        if(currentAnimation == MOVE){
            currentAnimFrame++;
            if(currentAnimFrame >= 5){
                currentAnimFrame = 0;
                currentAnimBitmap++;
                if(currentAnimBitmap >= animations.get(MOVE).length){
                    currentAnimBitmap = 0;
                }
            }
        }

        move(fps);
        setHitbox();
    }

    //called when the robot is hit by a gun or melee attack from the player
    public void dead(){
        setVisible(false);
        setActive(false);
    }

    //called by update when a bullet collides with a hitbox placed between the robot and the player
    //sets the robot to start jumping to avoid the bullet being shot at it
    public void startJump(){
        if(!jumping){
            jumping = true;
            jumpTime = System.currentTimeMillis();
            currentAnimBitmap = 0;
            currentAnimFrame = 0;
            currentAnimation = JUMP;
        }
    }

    //called by update so this robot knows where the player is
    public void updatePlayerLocation(float px){
        this.px = px;
    }

    //this method sets the flag that has the robot back off temporarily when it hits the player
    //so as to avoid having the robot "pinch" the player between itself and something else
    public void damageTurn(){
        mercyTime = System.currentTimeMillis();
    }

    //called by the draw method so it knows which bitmap it needs to draw for this robot
    public Bitmap getBitmap(int currentAnimation, int currentAnimBitmap){
        Bitmap[] animation = animations.get(currentAnimation);
        if(currentAnimBitmap < animation.length) {
            return animation[currentAnimBitmap];
        }
        return animation[0];//to prevent crashing from loading a bitmap that doesn't exist
    }
}
