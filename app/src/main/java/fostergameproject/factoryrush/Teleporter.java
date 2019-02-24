package fostergameproject.factoryrush;

public class Teleporter  extends GameObject{

    Vector2Point5D destination;

    //Teleporter, linked with another one by LevelManager
    //NOTE: this uses the same bitmap as the exit, but the teleport door
    //is much narrower
    Teleporter(float worldStartX, float worldStartY, char type){
        final float HEIGHT = 3.1f;
        final float WIDTH = 1.5f;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("door_open");
        setWorldLocation(worldStartX, worldStartY, -1);
        setHitbox();
        setPickup(true);
    }

    public void update(long fps, float gravity){}

    //this sets the target destination for the teleporter, called by LevelManager after initializing GameObjects
    //the location is passed from the levelManager, using the location of the linked teleporter
    public void setDestination(Vector2Point5D destination){
        this.destination = destination;
    }

}
