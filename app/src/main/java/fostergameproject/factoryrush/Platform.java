package fostergameproject.factoryrush;

public class Platform extends GameObject {

    Platform(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 1;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("tile");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
        setHitboxTop(0.25f);
    }

    //We don't need to update a tile, so this is empty
    public void update(long fps, float gravity){}
}
