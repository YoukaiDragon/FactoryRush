package fostergameproject.factoryrush;

//Class for the acid game object, a hazard that kills the player if they touch it
//This one is specifically for the surface texture
public class AcidSurface extends GameObject {
    AcidSurface(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 1.3f;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("acid1");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();

        //set the top of this hitbox a little lower
        setHitboxTop(0.4f);
    }

    //no need to update
    public void update(long fps, float gravity){}
}

