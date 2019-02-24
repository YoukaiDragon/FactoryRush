package fostergameproject.factoryrush;

//Class for the acid game object, a hazard that kills the player if they touch it
public class Acid extends GameObject {
    Acid(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 1;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("acid2");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }

    //no need to update
    public void update(long fps, float gravity){}
}
