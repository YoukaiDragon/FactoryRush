package fostergameproject.factoryrush;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

/*
Sprite Credits:
Jumping Robot: credit to www.amon.co
Big Robot: credit to Irina Mir (irmirx) -  opengameart.org
Flying Robot: credit to platforge project and artist Hannah Cohan – downloaded from opengameart.org
Player Character: credit to pzUH (gameart2D.com) –  downloaded from opengameart.org
Tileset from gameart2D.com
Battery and Shield Pickups: Credit to Fleurman - downloaded from opengameart.org
Lasers – downloaded from https://www.kisspng.com/png-blue-green-star-wars-color-laser-red-star-1234834/ from kisspng user Gvenegas
 */

public class FactoryActivity extends Activity{
    //Initialize view object
    private FactoryView factoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Get the screen resolution and set the view
        Display display = getWindowManager().getDefaultDisplay();
        Point resolution = new Point();
        display.getSize(resolution);
        factoryView = new FactoryView(this, resolution.x, resolution.y);

        //switch to the factoryView
        setContentView(factoryView);
    }

    //Pause the thread when the game is pause
    @Override
    protected void onPause(){
        super.onPause();
        factoryView.pause();
    }

    //Resume the thread if the game is resumed
    @Override
    protected void onResume(){
        super.onResume();
        factoryView.resume();
    }
}
