package fostergameproject.factoryrush;

//Class for the hitboxes used by the game objects
public class RectHitbox {
    float top;
    float left;
    float bottom;
    float right;
    float height;

    //Takes another hitbox as input and returns true if
    //this hitbox and the one passed to the method intersect
    boolean intersects(RectHitbox rectHitbox){
        boolean hit = false;

        if(this.right > rectHitbox.left && this.left < rectHitbox.right){
            //intersect on x axis
            if(this.top < rectHitbox.bottom && this.bottom > rectHitbox.top){
                //intersecting on y axis
                hit = true;
            }
        }
        return hit;
    }

    public void setTop(float top){
        this.top = top;
    }

    public float getLeft(){
        return left;
    }

    public void setLeft(float left){
        this.left = left;
    }

    public void setBottom(float bottom){
        this.bottom = bottom;
    }

    public void setRight(float right){
        this.right = right;
    }

    public float getHeight(){
        return height;
    }

    public void setHeight(float height){
        this.height = height;
    }
}
