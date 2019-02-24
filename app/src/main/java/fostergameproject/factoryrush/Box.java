package fostergameproject.factoryrush;


//class for a box that can be pushed
public class Box extends GameObject {
    boolean accelerate = false; //boolean for if the box is accelerating
    float worldStartX; //starting location used because box can currently be pushed only a limited distance from initial location
    final float MAX_X_VELOCITY = 3f;
    final int MAX_VELOCITY_FRAMES = 3; //number of frames the box can spend at top speed before starting to decelerate
    int maxVelocityFrames = 0; //number of frames the box has spent at top speed

    Box(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 2;
        final float WIDTH = 2;

        this.worldStartX = worldStartX;
        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("box");
        setMoves(true);
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }

    //Code to move the box when it is pushed by the player
    public void update(long fps, float gravity){
        if(accelerate){
            if(getxVelocity() > 0) {
                if (getxVelocity() < MAX_X_VELOCITY) {
                    setxVelocity(getxVelocity() + 0.2f);
                } else {
                    maxVelocityFrames++;
                    if (maxVelocityFrames >= MAX_VELOCITY_FRAMES) {
                        accelerate = false;
                        maxVelocityFrames = 0;
                    }
                }
            }else if(getxVelocity() < 0){
                if (getxVelocity() > -MAX_X_VELOCITY) {
                    setxVelocity(getxVelocity() - 0.2f);
                } else {
                    maxVelocityFrames++;
                    if (maxVelocityFrames >= MAX_VELOCITY_FRAMES) {
                        accelerate = false;
                        maxVelocityFrames = 0;
                    }
                }
            }
        }else if(getxVelocity() > 0){
            setxVelocity(getxVelocity() - 0.2f);
            if(getxVelocity() <= 0){
                setxVelocity(0);
            }
        }else if(getxVelocity() < 0){
            setxVelocity(getxVelocity() + 0.2f);
            if(getxVelocity() >=0){
                setxVelocity(0);
            }
        }

        if(getWorldLocation().x - worldStartX > 5 && getxVelocity() > 0){
            setxVelocity(0);
        }else if(worldStartX - getWorldLocation().x > 5 && getxVelocity() < 0){
            setxVelocity(0);
        }

        move(fps);
        setHitbox();
    }

    //Method to start movement of the box
    //Called by melee attack hit detection code in FactoryView update method
    public void pushed(int direction){
        accelerate = true;
        setxVelocity(3 * direction);
    }
}
