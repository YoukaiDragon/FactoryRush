package fostergameproject.factoryrush;

import android.content.Context;

import java.util.concurrent.CopyOnWriteArrayList;

//Class that manages the bullets for the player
public class Gun extends GameObject {

    private int maxAmmo = 6;
    private int ammoRemaining;
    private int numBullets;
    private int nextBullet;
    private int rateOfFire = 1; //shots per second
    private long lastShotTime;

    private CopyOnWriteArrayList<Bullet> bullets;

    Context context;
    int pixelsPerMetre;

    int speed = 25;

    Gun(){
        bullets = new CopyOnWriteArrayList<>();
        lastShotTime = -1;
        nextBullet = -1;
        ammoRemaining = maxAmmo;
        this.context = context;
        this.pixelsPerMetre = pixelsPerMetre;
    }

    //update each of the bullets
    public void update(long fps, float gravity){
        for(Bullet bullet : bullets){
            bullet.update(fps, gravity);
        }
    }

    public int getNumBullets(){
        return numBullets;
    }

    public int getAmmoRemaining(){
        return ammoRemaining;
    }

    public float getBulletX(int bulletIndex){
        if(bullets != null && bulletIndex < numBullets){
            return bullets.get(bulletIndex).getX();
        }
        return -1f;
    }

    public float getBulletY(int bulletIndex){
        if(bullets != null && bulletIndex < numBullets){
            return bullets.get(bulletIndex).getY();
        }
        return -1f;
    }

    //Called by FactoryView update method when a bullet hits something, calls hidebullet method of the bullet object,
    //which moves the bullet off screen and sets its velocity to 0
    public void hideBullet(int bulletIndex){
        bullets.get(bulletIndex).hideBullet();
    }

    public int getDirection(int bulletIndex){
        return bullets.get(bulletIndex).getDirection();
    }

    //called by FactoryView update method when the player collects an ammo pickup
    public void reloadAmmo(SoundManager sm){
        ammoRemaining = maxAmmo;
        sm.playSound("ammo_pickup");
    }

    //called by Player when the player fires the gun
    //input is the soundmanger for playing the appropriate sound effect, and
    //the player's location, height, and facing direction
    //output is a boolean saying if a shot was fired
    public boolean shoot(SoundManager sm, float ownerX, float ownerY, int ownerFacing, float ownerHeight){
        boolean shotFired = false;
        if(ammoRemaining == 0){
            sm.playSound("gun_empty");
            return shotFired;
        }

        if(System.currentTimeMillis() - lastShotTime > 1000 / rateOfFire){
            nextBullet++;

            if(numBullets >= maxAmmo){
                numBullets = maxAmmo;
            }
            if(nextBullet == maxAmmo){
                nextBullet = 0;
            }
            lastShotTime = System.currentTimeMillis();
            bullets.add(nextBullet, new Bullet(ownerX, (ownerY + ownerHeight / 3), speed, ownerFacing));
            shotFired = true;
            numBullets++;
            ammoRemaining--;
            sm.playSound("shoot");
        }
        return shotFired;
    }
}
