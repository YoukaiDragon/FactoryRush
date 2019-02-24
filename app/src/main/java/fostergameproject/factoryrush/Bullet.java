package fostergameproject.factoryrush;


//Class for the bullets fired by the player
public class Bullet {
    private float x;
    private float y;
    private float xVelocity;
    private int direction;

    Bullet(float x, float y, float xVelocity, int direction){
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.xVelocity = xVelocity * direction;

    }

    public int getDirection(){
        return direction;
    }

    //move the bullet based on it's velocity
    public void update(long fps, float gravity){
        x += xVelocity / fps;
    }

    //Makes a bullet vanish until needed by moving it off screen
    public void hideBullet(){
        this.x = -100;
        this.xVelocity = 0;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

}
