package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class GameObject {

    private Vector2Point5D worldLocation;
    private float width;
    private float height;

    private boolean active = true;
    private boolean visible = true;
    private char type;
    private String bitmapName;
    private float xVelocity;
    private float yVelocity;
    final int LEFT = -1;
    final int RIGHT = 1;
    private int facing;
    private boolean moves = false;
    private RectHitbox rectHitbox = new RectHitbox();
    private boolean pickup = false;

    public abstract void update(long fps, float gravity);

    public String getBitmapName(){
        return bitmapName;
    }

    public void setBitmapName(String bitmapName){
        this.bitmapName = bitmapName;
    }

    public float getWidth(){
        return width;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public float getHeight(){
        return height;
    }

    public void setHeight(float height){
        this.height = height;
    }

    //Prepares the primary bitmap for the gameObject stored by the levelManager
    public Bitmap prepareBitmap(Context context, String bitmapName, int pixelsPerMetre){
        //Make a resource id
        int resID = context.getResources().getIdentifier(bitmapName, "drawable", context.getPackageName());

        //create the bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        //scale the bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * pixelsPerMetre), (int) (height * pixelsPerMetre), false);

        return bitmap;
    }

    public Vector2Point5D getWorldLocation(){
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, float z){
        this.worldLocation = new Vector2Point5D();
        this.worldLocation.x = x;
        this.worldLocation.y = y;
        this.worldLocation.z = z;
    }

    public void setWorldLocationX(float x){
        this.worldLocation.x = x;
    }

    public void setWorldLocationY(float y){
        this.worldLocation.y = y;
    }

    public boolean isActive(){
        return active;
    }

    public boolean isVisible(){
        return visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public char getType(){
        return type;
    }

    public void setType(char type){
        this.type = type;
    }

    //Moves the object based on it's current velocity
    //called during update
    void move(long fps){
        if(xVelocity != 0){
            this.worldLocation.x += xVelocity / fps;
        }
        if(yVelocity != 0){
            this.worldLocation.y += yVelocity / fps;
        }
    }

    public int getFacing(){
        return facing;
    }

    public void setFacing(int facing){
        this.facing = facing;
    }

    public float getxVelocity(){
        return xVelocity;
    }

    public void setxVelocity(float xVelocity){
        if(moves){
            this.xVelocity = xVelocity;
        }
    }

    public float getyVelocity(){
        return yVelocity;
    }

    public void setyVelocity(float yVelocity){
        if(moves){
            this.yVelocity = yVelocity;
        }
    }

    public boolean isMoves(){
        return moves;
    }

    public void setMoves(boolean moves){
        this.moves = moves;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    //updates the hitbox to the objects current location
    //called by update after calling move
    public void setHitbox(){
        rectHitbox.setTop(worldLocation.y);
        rectHitbox.setLeft(worldLocation.x);
        rectHitbox.setBottom(worldLocation.y + height);
        rectHitbox.setRight(worldLocation.x + width);
    }

    //
    public void setHitboxTop(float adjust){
        rectHitbox.setTop(worldLocation.y + height * adjust);
    }

    public RectHitbox getHitbox(){
        return rectHitbox;
    }

    public boolean isPickup(){
        return pickup;
    }
    public void setPickup(boolean pickup){
        this.pickup = pickup;
    }
}
