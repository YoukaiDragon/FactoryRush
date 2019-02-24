package fostergameproject.factoryrush;

//Class for the ammo pickup, an item that refills the player's ammo when collected
public class AmmoPickup extends GameObject {
    AmmoPickup(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 0.8f;
        final float WIDTH = 0.8f;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setPickup(true);
        setBitmapName("ammo_pickup");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }

    public void update(long fps, float gravity){}
}
