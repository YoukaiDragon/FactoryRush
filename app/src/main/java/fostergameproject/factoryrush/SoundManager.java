package fostergameproject.factoryrush;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import java.io.IOException;

//This class loads, stores, and plays the sound effects
public class SoundManager {
    private SoundPool soundPool;
    int shoot = -1;
    int melee = -1;
    int jump = -1;
    int player_hit = -1;
    int enemy_hit = -1;
    int ammo_pickup = -1;
    int shield_pickup = -1;
    int battery_dead = -1;
    int clink = -1;
    int level_complete = -1;
    int weapon_swap = -1;
    int gun_empty = -1;

    //called by FactoryView when the game is initializing to load the sounds that will be used by the game
    public void loadSound(Context context){
        //increase maxStreams as more sounds added
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        try{
            //create objects for the required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //load the sounds
            descriptor = assetManager.openFd("shoot.ogg");
            shoot = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("melee.ogg");
            melee = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("jump.ogg");
            jump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("player_hit.ogg");
            player_hit = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("enemy_hit.ogg");
            enemy_hit = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("ammo_pickup.ogg");
            ammo_pickup = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("shield_pickup.ogg");
            shield_pickup = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("battery_dead.ogg");
            battery_dead = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("clink.ogg");
            clink = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("level_complete.ogg");
            level_complete = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("weapon_swap.ogg");
            weapon_swap = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("gun_empty.ogg");
            gun_empty = soundPool.load(descriptor, 0);
        }catch(IOException e){
            Log.e("error", "failed to load sound files");
        }
    }

    //called whenever a sound needs to be played. Plays a sound based on the ID passed by whatever is calling this method
    public void playSound(String sound){
        switch(sound){
            case "shoot":
                soundPool.play(shoot,1,1,0,0,1);
                break;
            case "melee":
                soundPool.play(melee,1,1,0,0,1);
                break;
            case "jump":
                soundPool.play(jump,1,1,0,0,1);
                break;
            case "player_hit":
                soundPool.play(player_hit,1,1,0,0,1);
                break;
            case "enemy_hit":
                soundPool.play(enemy_hit,1,1,0,0,1);
                break;
            case "ammo_pickup":
                soundPool.play(ammo_pickup,1,1,0,0,1);
                break;
            case "shield_pickup":
                soundPool.play(shield_pickup,1,1,0,0,1);
                break;
            case "battery_dead":
                soundPool.play(battery_dead,1,1,0,0,1);
                break;
            case "clink":
                soundPool.play(clink,1,1,0,0,1);
                break;
            case "level_complete":
                soundPool.play(level_complete,1,1,0,0,1);
                break;
            case "weapon_swap":
                soundPool.play(weapon_swap,1,1,0,0,1);
                break;
            case "gun_empty":
                soundPool.play(gun_empty, 1, 1, 0, 0, 1);
        }
    }
}
