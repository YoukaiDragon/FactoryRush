package fostergameproject.factoryrush;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;

//Class for managing the levelData and the GameObjects
public class LevelManager {
    private String level;
    int mapWidth;
    int mapHeight;
    Player player;
    int playerIndex;
    Door door;
    int doorIndex;
    private boolean playing;
    float gravity = 0;
    LevelData levelData;
    ArrayList<GameObject> gameObjects;
    ArrayList<Teleporter> teleporters;
    Bitmap[] bitmapsArray;

    public LevelManager(Context context, int pixelsPerMetre,
                        int screenWidth, InputController ic, String level, float px, float py){

        this.level = level;

        //MAKE SURE TO ADD ALL LEVELS HERE
        //GAME WILL CRASH IF YOU TRY TO LOAD A LEVEL THAT IS NOT HERE
        switch(level){
            case "LevelTest":
                levelData = new LevelTest();
                break;
            case "LevelOne":
                levelData = new LevelOne();
                break;
            case "LevelTwo":
                levelData = new LevelTwo();
                break;
            case "LevelThree":
                levelData = new LevelThree();
                break;
            case "LevelFour":
                levelData = new LevelFour();
                break;
            case "LevelFive":
                levelData = new LevelFive();
                break;
        }

        //Array for all game objects
        gameObjects = new ArrayList<>();
        teleporters = new ArrayList<>();

        //hold 1 of every bitmap UPDATE AS MORE ARE ADDED
        bitmapsArray = new Bitmap[20];

        //load the data
        loadMapData(context, pixelsPerMetre, px, py);

        //start the game
        playing = true;
    }

    public boolean isPlaying(){
        return playing;
    }

    public Bitmap getBitmap(char blockType){
        int index;

        switch(blockType){
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'b':
                index = 3;
                break;
            case 'a':
                index = 4;
                break;
            case 'A':
                index = 5;
                break;
            case '4':
                index = 6;
                break;
            case '5':
                index = 7;
                break;
            case '6':
                index = 8;
                break;
            case '7':
                index = 9;
                break;
            case 'r':
                index = 10;
                break;
            case 'y':
                index = 11;
                break;
            case 'u':
                index = 12;
                break;
            case '8':
                index = 13;
                break;
            case 'D':
                index = 14;
                break;
            case 'f':
                index = 15;
                break;
            case 't':
                index = 16;
                break;
            case 'x':
                index = 17;
                break;
            case 'j':
                index = 18;
                break;
            case 'd':
                index = 19;
                break;
            default:
                index = 0;
                break;
        }
        return bitmapsArray[index];
    }

    public int getBitmapIndex(char blockType){
        int index;

        switch(blockType){
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'b':
                index = 3;
                break;
            case 'a':
                index = 4;
                break;
            case 'A':
                index = 5;
                break;
            case '4':
                index = 6;
                break;
            case '5':
                index = 7;
                break;
            case '6':
                index = 8;
                break;
            case '7':
                index = 9;
                break;
            case 'r':
                index = 10;
                break;
            case 'y':
                index = 11;
                break;
            case 'u':
                index = 12;
                break;
            case '8':
                index = 13;
                break;
            case 'D':
                index = 14;
                break;
            case 'f':
                index = 15;
                break;
            case 't':
                index = 16;
                break;
            case 'x':
                index = 17;
                break;
            case 'j':
                index = 18;
                break;
            case 'd':
                index = 19;
                break;
            default:
                index = 0;
                break;
        }

        return index;
    }

    private void loadMapData(Context context, int pixelsPerMetre, float px, float py) {
        char c;

        //keep track of the index
        int currentIndex = -1;
        //boundary variables for robots
        int leftBound;
        int rightBound;

        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();

        for (int i = 0; i < levelData.tiles.size(); i++) {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) {
                c = levelData.tiles.get(i).charAt(j);

                //ignore empty spaces
                if(c != '.'){
                    currentIndex++;
                    switch(c){
                        case '1':
                            //Add a platform to the gameobjects
                            gameObjects.add(new Platform(j,i,c));
                            break;
                        case 'a':
                            //Add an acid tile to gameobjects
                            gameObjects.add(new Acid(j,i,c));
                            break;
                        case 'A':
                            //add an acid surface tile to gameObjects
                            gameObjects.add(new AcidSurface(j,i,c));
                            break;
                        case 'd':
                            //add a door to the gameobjects
                            gameObjects.add(new Door(context, pixelsPerMetre, j,i,c));
                            //get index of door and reference to door
                            doorIndex = currentIndex;
                            door = (Door) gameObjects.get(doorIndex);
                            break;
                        case 'p':
                            //add a player to the gameobjects
                            gameObjects.add(new Player(context, px, py, pixelsPerMetre));

                            //get index of player and reference to player
                            playerIndex = currentIndex;
                            player = (Player) gameObjects.get(playerIndex);
                            break;
                        case 'b':
                            //add a big robot to the gameobjects
                            //find the bounds of the patrol route
                            leftBound = j;
                            rightBound = j+1;
                            //advance bounds until next tile is a wall or has no floor and there are still tiles to iterate through
                            while(levelData.tiles.get(i+2).charAt(leftBound-1) != '1'
                                    && levelData.tiles.get(i+3).charAt(leftBound-1) == '1' && leftBound > 1){
                                leftBound--;
                            }
                            while(levelData.tiles.get(i+2).charAt(rightBound+1) != '1'
                                    && levelData.tiles.get(i+3).charAt(rightBound+1) == '1'
                                    && rightBound + 2 < levelData.tiles.get(i+2).length()
                                    && rightBound + 2 < levelData.tiles.get(i+3).length()){
                                rightBound++;
                            }
                            gameObjects.add(new BigRobot(context, pixelsPerMetre, j, i, leftBound, rightBound));
                            break;
                        case '4':
                            //add a battery pickup to the gameobjects
                            gameObjects.add(new BatteryPickup(j,i,c));
                            break;
                        //add a shield pickup to the gameobjects
                        case '5':
                            gameObjects.add(new Shield(j,i,c));
                            break;
                        case '6':
                            gameObjects.add(new Shield(j,i,c));
                            break;
                        case '7':
                            gameObjects.add(new Shield(j,i,c));
                            break;
                        //add lasers
                        case 'r':
                        case 'y':
                        case 'u':
                            gameObjects.add(new Laser(j,i,c));
                            break;
                        case '8':
                            gameObjects.add(new AmmoPickup(j,i,c));
                            break;
                        case 'D':
                            gameObjects.add(new DoorSwitch(context, pixelsPerMetre, j,i,c));
                            break;
                        case 'f':
                            gameObjects.add(new FlyingRobot(context, pixelsPerMetre, j,i,c, px, py));
                            break;
                        case 't':
                            gameObjects.add(new Teleporter(j,i,c));
                            teleporters.add((Teleporter) gameObjects.get(currentIndex));
                            break;
                        case 'x':
                            gameObjects.add(new Box(j,i,c));
                            break;
                        case 'j':
                            leftBound = j;
                            rightBound = j;
                            //advance bounds until next tile is a wall or has no floor
                            while(levelData.tiles.get(i+1).charAt(leftBound-1) != '1'
                                    && levelData.tiles.get(i+2).charAt(leftBound-1) == '1'){
                                leftBound--;
                            }
                            while(levelData.tiles.get(i+1).charAt(rightBound+1) != '1'
                                    && levelData.tiles.get(i+2).charAt(rightBound+1) == '1'){
                                rightBound++;
                            }
                            gameObjects.add(new JumpingRobot(context, pixelsPerMetre, j,i,c, leftBound, rightBound));
                            break;
                    }

                    //Prepare bitmap if needed
                    if(bitmapsArray[getBitmapIndex(c)] == null){
                        bitmapsArray[getBitmapIndex(c)] =
                                gameObjects.get(currentIndex).prepareBitmap(context,
                                        gameObjects.get(currentIndex).getBitmapName(), pixelsPerMetre);
                    }
                }
            }
        }

        //Set Teleporter destinations
        if(teleporters.size() % 2 == 0){
            /*if there is an unpaired teleporter, remove it to avoid errors below
            the reason we remove a teleporter only if there is an even amount in the list
            is because the first one is a dummy teleporter only there to make sure the correct bitmap
            is used*/
            teleporters.remove(teleporters.size()-1);
        }
        for(int i = 1; i < teleporters.size(); i++){//skip the dummy teleporter
            if(i%2 == 0){
                teleporters.get(i).setDestination(teleporters.get(i-1).getWorldLocation());
            }else{
                teleporters.get(i).setDestination(teleporters.get(i+1).getWorldLocation());
            }
        }
    }

    public void switchPlayingStatus(){
        playing = !playing;
        if(playing){
            player.setMoves(true);
            gravity = 6;
        }else{
            player.setMoves(false);
            gravity = 0;
        }
    }
}
