package fostergameproject.factoryrush;

import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;

public class InputController {

    Rect left;
    Rect right;
    Rect slide;
    Rect weaponSwap;
    Rect attack;
    Rect jump;
    Rect pause;

    int screenWidth;

    InputController(int screenWidth, int screenHeight){
        //Button size is defined as fraction of screen size
        int buttonWidth = screenWidth / 8;
        int buttonHeight = screenHeight / 7;
        int buttonPadding = screenWidth / 80;
        this.screenWidth = screenWidth;

        //define the size and location of the buttons
        left = new Rect(buttonPadding,
                screenHeight - buttonHeight - buttonPadding - buttonPadding - buttonHeight,
                buttonWidth,
                screenHeight - buttonHeight - buttonPadding - buttonPadding);
        right = new Rect(buttonPadding + buttonWidth,
                screenHeight - buttonHeight - buttonPadding - buttonPadding - buttonHeight,
                buttonWidth + buttonPadding + buttonWidth,
                screenHeight - buttonHeight - buttonPadding - buttonPadding);
        slide = new Rect(buttonPadding + (buttonWidth / 2),
                screenHeight - buttonHeight - buttonPadding,
                buttonPadding + (buttonWidth / 2) + buttonWidth,
                screenHeight - buttonPadding);
        weaponSwap = new Rect(buttonPadding + (buttonWidth / 2),
                screenHeight - buttonHeight - buttonPadding - buttonPadding - buttonHeight - buttonPadding - buttonHeight,
                buttonPadding + (buttonWidth / 2) + buttonWidth,
                screenHeight - buttonHeight - buttonPadding - buttonPadding - buttonHeight - buttonPadding);
        attack = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding - buttonHeight,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding);
        jump = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding);
        pause = new Rect(screenWidth - buttonPadding - buttonWidth,
                buttonPadding,
                screenWidth - buttonPadding,
                buttonPadding + buttonHeight);
    }

    public ArrayList getButtons(){
        ArrayList<Rect> buttonList = new ArrayList<>();
        buttonList.add(left);
        buttonList.add(right);
        buttonList.add(slide);
        buttonList.add(weaponSwap);
        buttonList.add(attack);
        buttonList.add(jump);
        buttonList.add(pause);
        return buttonList;
    }

    //Called whenever the FactoryView class detects a MotionEvent and the game is not on the main menu or game over screens
    //determines the type and location of the MotionEvent, and then takes appropriate action
    //if a button is pressed or released
    public void handleInput(MotionEvent motionEvent, LevelManager lm, SoundManager sound, Viewport vp){
        int pointerCount = motionEvent.getPointerCount();
        for(int i = 0; i < pointerCount; i++){
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    if(left.contains(x,y)){
                        lm.player.setPressingRight(false);
                        lm.player.setPressingLeft(true);
                    }else if(right.contains(x,y)){
                        lm.player.setPressingRight(true);
                        lm.player.setPressingLeft(false);
                    }else if(slide.contains(x,y)){
                        //add slide code here
                        lm.player.startSlide();
                    }else if(weaponSwap.contains(x,y)){
                        //add weapon swap code here
                        lm.player.isMelee = !lm.player.isMelee;
                    }else if(attack.contains(x,y)){
                        lm.player.attack(sound);
                    }else if(jump.contains(x,y)){
                        lm.player.startJump(sound);
                    }else if(pause.contains(x,y)){
                        lm.switchPlayingStatus();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(left.contains(x,y)){
                        lm.player.setPressingLeft(false);
                    }else if(right.contains(x,y)){
                        lm.player.setPressingRight(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(left.contains(x,y)){
                        lm.player.setPressingLeft(true);
                        lm.player.setPressingRight(false);
                    }else if(right.contains(x,y)){
                        lm.player.setPressingLeft(false);
                        lm.player.setPressingRight(true);
                    }else if(x < screenWidth / 2){
                        lm.player.setPressingLeft(false);
                        lm.player.setPressingRight(false);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(left.contains(x,y)){
                        lm.player.setPressingRight(false);
                        lm.player.setPressingLeft(true);
                    }else if(right.contains(x,y)){
                        lm.player.setPressingRight(true);
                        lm.player.setPressingLeft(false);
                    }else if(slide.contains(x,y)){
                        //add slide code here
                        lm.player.startSlide();
                    }else if(weaponSwap.contains(x,y)){
                        //add weapon swap code here
                        lm.player.isMelee = !lm.player.isMelee;
                    }else if(attack.contains(x,y)){
                        lm.player.attack(sound);
                    }else if(jump.contains(x,y)){
                        lm.player.startJump(sound);
                    }else if(pause.contains(x,y)){
                        lm.switchPlayingStatus();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(left.contains(x,y)){
                        lm.player.setPressingLeft(false);
                    }else if(right.contains(x,y)){
                        lm.player.setPressingRight(false);
                    }
                    break;
            }
        }
    }
}
