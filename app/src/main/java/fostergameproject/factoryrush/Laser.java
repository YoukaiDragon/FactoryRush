package fostergameproject.factoryrush;

public class Laser extends GameObject{

    //A laser, comes in three colors
    //Lasers will block and hurt the player unless they possess the shield powerup that matches
    //the laser
    Laser(float worldStartX, float worldStartY, char type){
        final float WIDTH = 0.6f;

        setHeight(1.1f);
        setWidth(WIDTH);
        setType(type);
        if(type == 'r'){
            setBitmapName("red_laser");
        }else if(type == 'u'){
            setBitmapName("blue_laser");
        }else{
            setBitmapName("orange_laser");
        }
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }


    public void update(long fps, float gravity){
    }
}
