package fostergameproject.factoryrush;

public class Shield extends GameObject {

    //These match the numbers used for the pickups in LevelData for ease of remembering
    final char RED = '5';
    final char YELLOW = '6';
    final char BLUE = '7';

    Shield(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 0.8f;
        final float WIDTH = 0.8f;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setPickup(true);
        if(type == RED){
            setBitmapName("shield_red");
        }else if(type == YELLOW){
            setBitmapName("shield_yellow");
        }else if(type == BLUE){
            setBitmapName("shield_blue");
        }
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }

    public void update(long fps, float gravity){}
}
