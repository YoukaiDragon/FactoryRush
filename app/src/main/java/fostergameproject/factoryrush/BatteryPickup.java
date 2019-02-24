package fostergameproject.factoryrush;

//Class for the battery pickup, an item that refills the player's battery when collected
public class BatteryPickup extends GameObject {
    BatteryPickup(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 0.8f;
        final float WIDTH = 0.8f;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setPickup(true);
        setBitmapName("battery_pickup");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
    }

    public void update(long fps, float gravity){}
}
