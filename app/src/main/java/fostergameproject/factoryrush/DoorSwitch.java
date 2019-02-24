package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;

//Class for the switch that unlocks the level exit
public class DoorSwitch extends GameObject{

    boolean unlocked;
    Bitmap switchOn;

    DoorSwitch(Context context, int pixelsPerMetre, float worldStartX, float worldStartY, char type){
        final float HEIGHT = 3.1f;
        final float WIDTH = 1;

        unlocked = false;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("switch_off");
        setWorldLocation(worldStartX, worldStartY, 0);
        setHitbox();
        //prepare the bitmap for when the switch has been activated
        switchOn = prepareBitmap(context, "switch_on", pixelsPerMetre);
    }

    public void update(long fps, float gravity){}

    public boolean isUnlocked(){
        return unlocked;
    }

    //Called by the hit detection code in the FactoryView update method when the player collides with this object
    public void unlock(){
        unlocked = true;
    }
}
