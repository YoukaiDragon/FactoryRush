package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;

//a flying robot that will chase after the player within a certain distance of its spawn point.
//Has a bit of a delay to changing direction, to make it easier to deal with
public class FlyingRobot extends GameObject {

    final float MAX_X_VELOCITY = 8;
    final float MAX_Y_VELOCITY = 6;
    //Max distance from the start location the robot will go to
    final float MAX_CHASE_X = 25;
    final float MAX_CHASE_Y = 12;
    //current target location
    float waypointX = 0;
    float waypointY = 0;
    //current player location, used to update waypoint
    private float playerX = 0;
    private float playerY = 0;
    //starting location
    private float homeX;
    private float homeY;
    public Bitmap bitmap;

    final long WAYPOINT_UPDATE_TIME = 2000; //2 seconds
    private long lastWaypointUpdate = 0;

    FlyingRobot(Context context, int pixelsPerMetre, float worldStartX, float worldStartY, char type, float pX, float pY){
        final float HEIGHT = 2;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setMoves(true);
        setWorldLocation(worldStartX, worldStartY, 0);
        homeX = worldStartX;
        homeY = worldStartY;
        setBitmapName("flying_robot1");
        setHitbox();
        setFacing(LEFT);
        waypointX = worldStartX;
        waypointY = worldStartY;

        playerX = pX;
        playerY = pY;

        bitmap = prepareBitmap(context, "flying_robot1", pixelsPerMetre);
    }

    public void update(long fps, float gravity){
        if(System.currentTimeMillis() - lastWaypointUpdate >= WAYPOINT_UPDATE_TIME){ //update the waypoint periodically
            waypointX = playerX;
            waypointY = playerY;
            lastWaypointUpdate = System.currentTimeMillis();
        }

        //move towards the waypoint
        if((homeX - waypointX <= MAX_CHASE_X && homeX - waypointX >= -MAX_CHASE_X)){
            if((homeY - waypointY <= MAX_CHASE_Y && homeY - waypointY >= -MAX_CHASE_Y)){
                //player is close enough, chase them
                //nested loop is to prevent vibrations caused by never reaching the exact value of the waypoint
                //set x velocity
                if(waypointX < getWorldLocation().x){
                    if(getWorldLocation().x - waypointX > 0.8f) {
                        setxVelocity(-MAX_X_VELOCITY);
                        setFacing(LEFT);
                    }
                }else if(waypointX > getWorldLocation().x){
                    if(waypointX - getWorldLocation().x > 0.8f) {
                        setxVelocity(MAX_X_VELOCITY);
                        setFacing(RIGHT);
                    }
                }else{
                    setxVelocity(0);
                }
                //set y velocity
                if(waypointY < getWorldLocation().y){
                    if(getWorldLocation().y - waypointY > 0.8f) {
                        setyVelocity(-MAX_Y_VELOCITY);
                    }
                }else if(waypointY > getWorldLocation().y){
                    if(waypointY - getWorldLocation().y > 0.8f) {
                        setyVelocity(MAX_Y_VELOCITY);
                    }
                }else{
                    setyVelocity(0);
                }
            }
        }else{//move back to the start location
            if(homeX < getWorldLocation().x){
                if(getWorldLocation().x - homeX > 0.8f) {
                    setxVelocity(-MAX_X_VELOCITY);
                    setFacing(LEFT);
                }
            }else if(homeX > getWorldLocation().x){
                if(homeX - getWorldLocation().x > 0.8f) {
                    setxVelocity(MAX_X_VELOCITY);
                    setFacing(RIGHT);
                }
            }else{
                setxVelocity(0);
            }
            if(homeY < getWorldLocation().y){
                if(getWorldLocation().y - homeY > 0.8f) {
                    setyVelocity(-MAX_Y_VELOCITY);
                }
            }else if(homeY > getWorldLocation().y){
                if(homeY - getWorldLocation().y > 0.8f) {
                    setyVelocity(MAX_Y_VELOCITY);
                }
            }else{
                setyVelocity(0);
            }
        }

        //move and update the hitbox
        move(fps);
        setHitbox();
    }

    //called by the main upate method to pass the player's location to this robot
    public void updatePlayerLocation(float px,float py){
        playerX = px;
        playerY = py;
    }

    //called when the robot collides with the player's melee or gun attacks
    public void dead(){
        setVisible(false);
        setActive(false);
    }

    //This method has the robot back off a bit when it hits the player
    //to avoid pinching the player through a wall, parameter is the x position of the player
    //used to determine which direction the robot needs to move
    public void damageTurn(float px){
        if(px < getWorldLocation().x){
            waypointX = getWorldLocation().x + 3;
        }else{
            waypointX = getWorldLocation().x - 3;
        }
    }
}
