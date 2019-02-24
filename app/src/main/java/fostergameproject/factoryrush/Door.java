package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;

//This is the class for the level exit
public class Door extends GameObject {
    public boolean unlocked; //boolean for if the exit is open
    public Bitmap doorOpen; //bitmap for the door in its open state


    Door(Context context, int pixelsPerMetre, float worldStartX, float worldStartY, char type){
        final float HEIGHT = 4.1f;
        final float WIDTH = 2.5f;

        unlocked = false;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("door_locked");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
        doorOpen = prepareBitmap(context, "door_open", pixelsPerMetre);
    }

    public boolean isUnlocked(){
        return unlocked;
    }

    //Called by the FactoryView update method if the door switch has been activated
    public void unlock(){
        unlocked = true;
    }

    //no need to update
    public void update(long fps, float gravity){}
}
