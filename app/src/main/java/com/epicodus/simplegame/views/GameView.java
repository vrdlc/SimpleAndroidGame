package com.epicodus.simplegame.views;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.epicodus.simplegame.R;
import com.epicodus.simplegame.activities.MainActivity;
import com.epicodus.simplegame.models.Boat;
import com.epicodus.simplegame.models.Bubble;
import com.epicodus.simplegame.models.Dolphin;
import com.epicodus.simplegame.models.Harpoon;
import com.epicodus.simplegame.models.Player;
import com.epicodus.simplegame.models.Pufferfish;
import com.epicodus.simplegame.models.Seaweed;

import com.epicodus.simplegame.models.Shark;
import com.epicodus.simplegame.models.Spear;
import com.epicodus.simplegame.models.Swordfish;

import com.epicodus.simplegame.models.Skull;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    public static final String TAG = GameView.class.getSimpleName();

    //Game State Constants
    public static final int GAME_START = 0;
    public static final int GAME_PLAYING = 1;
    public static final int GAME_UPGRADING = 2;
    public static final int GAME_OVER = 3;

    //Game Essentials
    Thread gameThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playing;
    boolean firstRun = true;
    Canvas canvas;
    Paint paint;
    long fps;
    float screenX;
    float screenY;
    int gameState;
    int gold;
    Context mContext;
    private MediaPlayer levelMusic;
    private MediaPlayer boatMusic;
    private MediaPlayer titleSound = new MediaPlayer();

//    private SoundPool soundPool;
//    private int titleID;

    long gameStartTime;

    //Joystick variables
    float pointerX;
    float pointerY;
    float circleXPosition;
    float circleYPosition;
    float circleDefaultX;
    float circleDefaultY;
    float deltaX;
    float deltaY;
    float distance;
    float theta;
    float joystickRadius;

    //Models
    Player player;
    ArrayList<Harpoon> harpoons = new ArrayList<>();
    ArrayList<Dolphin> dolphins = new ArrayList<>();
    ArrayList<Seaweed> seaweeds = new ArrayList<>();
    ArrayList<Shark> sharks = new ArrayList();
    ArrayList<Swordfish> swordfishes = new ArrayList();
    ArrayList<Spear> spears = new ArrayList();
    ArrayList<Pufferfish> pufferfishes = new ArrayList();
    Bubble bubble;
    Skull skull;
    Boat boat;

    //Bitmaps/animation variables
    Bitmap harpoonKey;
    Bitmap fullBubbleMeter;
    Bitmap emptyBubbleMeter;
    boolean isMoving = false;
    long bubbleBlinkInterval;
    long lastBubbleBlink;
    boolean bubbleBlinkEmpty;

    //Upgrade button positions
    float upgradeX;
    float upgradeHarpoonY;
    float upgradeOxygenY;
    float upgradeSpeedY;
    float upgradeLungsY;
    float upgradeButtonRadius;
    float doneUpgradingX;
    float doneUpgradingY;
    float doneUpgradingWidth;
    float doneUpgradingHeight;

    //Upgrade counters
    int harpoonUpgradeLevel;
    int oxygenUpgradeLevel;
    int speedUpgradeLevel;
    int lungsUpgradeLevel;

    //Upgrade Points Arrays
    Integer[] harpoonUpgradeCosts  = {1, 4, 8, 13, 19, 28, 43, 67, 101, 155, -1};
    Integer[] lungsUpgradeCosts  = {1, 4, 8, 13, 19, 28, 43, 67, 101, 155, -1};
    Integer[] oxygenUpgradeCosts  = {1, 4, 8, 13, 19, 28, 43, 67, 101, 155, -1};
    Integer[] speedUpgradeCosts  = {1, 4, 8, 13, 19, 28, 43, 67, 101, 155, -1};

    //Model Counts
    int maxVisibleSwordfish;
    int maxVisibleSharks;
    int maxVisibleDolphins;
    int maxVisiblePufferfish;


    //Other
    float scrollSpeed;
    Random randomNumberGenerator;
    int harpoonCount;

    public GameView(Context context, float x, float y) {
        super(context);
        mContext = context;
        gameState = GAME_START;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        bubbleBlinkInterval = 750;

        //Setup Joystick
        circleDefaultX = (float) (0.15*screenX);
        circleDefaultY = (float) (0.78*screenY);
        joystickRadius = (float) .1*screenY;

        //Setup Upgrade Buttons
        upgradeX = 37*screenX/55;
        upgradeHarpoonY = 14*screenY/60;
        upgradeOxygenY = 23*screenY/60;
        upgradeSpeedY = 32*screenY/60;
        upgradeLungsY = 41*screenY/60;
        upgradeButtonRadius = 5*screenY/120;

        doneUpgradingX = 16*screenX/20;
        doneUpgradingY = 17*screenY/20;
        doneUpgradingHeight = 2*screenY/20;
        doneUpgradingWidth = 3*screenX/20;

        //Initialize upgrade values
        harpoonUpgradeLevel = 0;
        oxygenUpgradeLevel = 0;
        speedUpgradeLevel = 0;
        lungsUpgradeLevel = 0;

        //Initialize camera movement and spawn zone
        scrollSpeed = screenX/20;

//        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

//        try {
//            AssetManager assetManager = context.getAssets();
//            AssetFileDescriptor descriptor;
//
//            descriptor = assetManager.openFd("title1.wav");
//            titleID = soundPool.load(descriptor, 0);
//
//        } catch (IOException e) {
//            Log.e("error", "failed to load sound file");
//        }


    }

    public void prepareLevel(Context context) {

        //clear variables
        dolphins.clear();
        harpoons.clear();
        seaweeds.clear();
        sharks.clear();
        swordfishes.clear();
        pufferfishes.clear();
        spears.clear();


        bubbleBlinkEmpty = false;
        lastBubbleBlink = 0;

        harpoonCount = 0;

        if(gameState == GAME_START) {
            harpoonUpgradeLevel = 0;
            oxygenUpgradeLevel = 0;
            speedUpgradeLevel = 0;
            lungsUpgradeLevel = 0;
        }

        //Instantiate music (add more music here, but create new MediaPlayer up at top)
        levelMusic = MediaPlayer.create(mContext, R.raw.two_finger_johnny);
        boatMusic = MediaPlayer.create(mContext, R.raw.bit_quest);
        titleSound = MediaPlayer.create(mContext, R.raw.title);
        Log.v("sound", ""+levelMusic);

        //Start level music
        levelMusic.start();


        //Initialize Models
        player = new Player(context, screenX, screenY, speedUpgradeLevel, oxygenUpgradeLevel, lungsUpgradeLevel);

        for (int i=0; i < 10; i++) {
            seaweeds.add(new Seaweed(context, screenX, screenY));
            seaweeds.get(i).isVisible = false;
            seaweeds.get(i).resetX();
        }


        for (int i=0; i < 1+harpoonUpgradeLevel; i++){
            harpoons.add(new Harpoon(context, screenX, screenY, player));
            harpoonCount++;
        }

        for (int i = 0; i < 10; i++) {
            dolphins.add(new Dolphin(context, screenX, screenY));
        }
        for (int i = 0; i < 10; i++) {
            spears.add(new Spear(context, screenX, screenY));
        }

        for (int i = 0; i < 10; i++) {
            sharks.add(new Shark(context, screenX, screenY));
        }
        for (int i = 0; i < 10; i++) {
            swordfishes.add(new Swordfish(context, screenX, screenY));
        }
        for (int i = 0; i < 10; i++) {
            pufferfishes.add(new Pufferfish(context, screenX, screenY));
        }

        //Initialize model counts
        maxVisibleSwordfish = 2;
        maxVisibleSharks = 0;
        maxVisibleDolphins = 0;
        maxVisiblePufferfish = 0;


        skull = new Skull(screenX, screenY, context);
        bubble = new Bubble(screenX, screenY, context);
        boat = new Boat(context, screenX, screenY);



        //Setup game variables
        randomNumberGenerator = new Random();
        gold = 0;
        pointerX = circleDefaultX;
        pointerY = circleDefaultY;
        gameStartTime = System.currentTimeMillis();

        //Setup Bitmaps
        harpoonKey = BitmapFactory.decodeResource(getResources(), R.drawable.harpoonkey);
        emptyBubbleMeter = BitmapFactory.decodeResource(getResources(), R.drawable.bubblemeter);
        emptyBubbleMeter = Bitmap.createScaledBitmap(emptyBubbleMeter, (int) screenX/40, (int) screenY/30, false);
        fullBubbleMeter = BitmapFactory.decodeResource(getResources(), R.drawable.fillbubblemeter);
        fullBubbleMeter = Bitmap.createScaledBitmap(fullBubbleMeter, (int) screenX/40, (int) screenY/30, false);

        //Generate a Dolphin
        swordfishes.get(0).generate(screenY/2);

    }

    @Override
    public void run() {
        long timeThisFrame;
        while(playing) {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis()-startFrameTime;
            if(timeThisFrame > 0) {
                fps = 1000/timeThisFrame;
            }
        }
    }

    public void update() {

        if (gameState == GAME_PLAYING) {

            //Update enemy arrays based on time
            long gameTime = System.currentTimeMillis() - gameStartTime;

            if (gameTime > 60000) {
                maxVisibleDolphins = 2;
            } else if (gameTime > 35000) {
                maxVisibleSharks = 2;
            } else if (gameTime > 20000) {
                maxVisibleSwordfish = 3;
                maxVisibleSharks = 1;
            } else if (gameTime > 10000) {
                maxVisibleSwordfish = 4;
            } else if (gameTime > 5000) {
                maxVisibleSwordfish = 3;
            }

            //Default pointer position if player dies

            //Update boat
            boat.getCurrentFrame();

            //Calculate joystick position
            deltaX = pointerX - circleDefaultX;
            deltaY = pointerY - circleDefaultY;
            distance = (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
            theta = (float) Math.atan2(deltaY, deltaX);

            if (distance <= joystickRadius) {
                circleXPosition = pointerX;
                circleYPosition = pointerY;
            } else {
                circleXPosition = (float) (circleDefaultX + (joystickRadius) * Math.cos(theta));
                circleYPosition = (float) (circleDefaultY + (joystickRadius) * Math.sin(theta));
            }

            //Generate Dolphins
            int randomDolphinNumber = randomNumberGenerator.nextInt(50);
            if (randomDolphinNumber == 0) {
                int dolphinCount = 0;
                for (int i = 0; i < dolphins.size(); i++) {
                    if (!dolphins.get(i).isVisible) {
                        if (dolphinCount >= maxVisibleDolphins) {
                            break;
                        }
                        float randomY = randomNumberGenerator.nextFloat() * (screenY - (screenY / 10));
                        dolphins.get(i).generate(randomY);
                        dolphins.get(i).isDead = false;
                        break;
                    } else {
                        dolphinCount++;
                    }
                }
            }

            //Generate Sharks
            int randomSharkNumber = randomNumberGenerator.nextInt(50);
            if (randomSharkNumber == 0) {
                int sharkCount = 0;
                for (int i = 0; i < sharks.size(); i++) {
                    if (!sharks.get(i).isVisible) {
                        if (sharkCount >= maxVisibleSharks) {
                            break;
                        }
                        float randomY = randomNumberGenerator.nextFloat() * (screenY - (screenY / 10));
                        sharks.get(i).generate(randomY);
                        sharks.get(i).isDead = false;
                        break;
                    } else {
                        sharkCount++;
                    }
                }
            }

            //Generate Swordfishes
            int randomSwordfishNumber = randomNumberGenerator.nextInt(50);
            if (randomSwordfishNumber == 0) {
                int swordfishCount = 0;
                for (int i = 0; i < swordfishes.size(); i++) {
                    if (!swordfishes.get(i).isVisible) {
                        if (swordfishCount >= maxVisibleSwordfish) {
                            break;
                        }
                        float randomY = randomNumberGenerator.nextFloat() * (screenY - (screenY / 10));
                        swordfishes.get(i).generate(randomY);
                        swordfishes.get(i).isDead = false;
                        break;
                    } else {
                        swordfishCount++;
                    }
                }
            }

            //Generate Pufferfishes
            int randomPufferfishNumber = randomNumberGenerator.nextInt(50);
            if (randomPufferfishNumber == 0) {
                int pufferfishCount = 0;
                for (int i = 0; i < pufferfishes.size(); i++) {
                    if (!pufferfishes.get(i).isVisible) {
                        if (pufferfishCount >= maxVisiblePufferfish) {
                            break;
                        }
                        float randomY = randomNumberGenerator.nextFloat() * screenY - (screenY / 10);
                        pufferfishes.get(i).generate(randomY);
                        pufferfishes.get(i).isDead = false;
                        break;
                    } else {
                        pufferfishCount++;
                    }
                }
            }

            //Generate Seaweed
            int randomSeaweedNumber = randomNumberGenerator.nextInt(350);
            if (randomSeaweedNumber == 349) {
                for (int i = 0; i < seaweeds.size(); i++) {
                    if (!seaweeds.get(i).isVisible) {
                        seaweeds.get(i).generate();
                        break;
                    }
                }
            }

            //Update player position
            player.update(fps, circleXPosition, circleYPosition, circleDefaultX, circleDefaultY, scrollSpeed);
            player.getCurrentFrame();

            //Check player oxygen level
            if (player.getOxygenLevel() == 0) {
                gameState = GAME_OVER;
                levelMusic.pause();
                levelMusic.reset();
            }

            //Update harpoons
            for (int i = 0; i < harpoons.size(); i++) {
                if (harpoons.get(i).isVisible) {
                    harpoons.get(i).update(fps, scrollSpeed);

                    //Check for collision between player and harpoon
                    if (RectF.intersects(harpoons.get(i).getRect(), player.getRect())) {
                        if (!harpoons.get(i).isShot) {
                            harpoonCount++;
                            harpoons.get(i).deadDolphin = null;
                            harpoons.get(i).deadShark = null;
                            harpoons.get(i).deadSwordfish = null;
                            harpoons.get(i).isVisible = false;
                            harpoons.get(i).isAngled = false;
                        }
                    }

                    //Check for collision between harpoons and dolphins
                    for (int j = 0; j < dolphins.size(); j++) {
                        if (dolphins.get(j).isVisible) {
                            if (RectF.intersects(dolphins.get(j).getHitbox(), harpoons.get(i).getHitbox())) {
                                if (!harpoons.get(i).isAHit) {
                                    if (!dolphins.get(j).isDead) {
                                        harpoons.get(i).isShot = false;
                                        dolphins.get(j).isDead = true;
                                        harpoons.get(i).isAHit = true;
                                        dolphins.get(j).killHarpoon = harpoons.get(i);
                                        harpoons.get(i).deadDolphin = dolphins.get(j);
                                    }
                                }
                            }
                        }
                    }

                    //Check for collision between harpoons and sharks
                    for (int j = 0; j < sharks.size(); j++) {
                        if (sharks.get(j).isVisible) {
                            if (RectF.intersects(sharks.get(j).getHitbox(), harpoons.get(i).getHitbox())) {
                                if (!harpoons.get(i).isAHit) {
                                    if (!sharks.get(j).isDead) {
                                        sharks.get(j).life--;
                                        harpoons.get(i).isShot = false;

                                        if (sharks.get(j).life == 0) {
                                            sharks.get(j).isDead = true;
                                        }
                                        harpoons.get(i).isAHit = true;
                                        sharks.get(j).killHarpoon = harpoons.get(i);
                                        harpoons.get(i).deadShark = sharks.get(j);
                                    }
                                }
                            }
                        }
                    }

                    //Check for collision between harpoons and swordfishes
                    for (int j = 0; j < swordfishes.size(); j++) {
                        if (swordfishes.get(j).isVisible) {
                            if (RectF.intersects(swordfishes.get(j).getHitbox(), harpoons.get(i).getHitbox())) {
                                if (!harpoons.get(i).isAHit) {
                                    if (!swordfishes.get(j).isDead) {
                                        harpoons.get(i).isShot = false;
                                        swordfishes.get(j).isDead = true;
                                        harpoons.get(i).isAHit = true;
                                        swordfishes.get(j).killHarpoon = harpoons.get(i);
                                        harpoons.get(i).deadSwordfish = swordfishes.get(j);
                                    }
                                }
                            }
                        }
                    }
                    //Check for collision between harpoons and pufferfishes
                    for (int j = 0; j < pufferfishes.size(); j++) {
                        if (pufferfishes.get(j).isVisible) {
                            if (RectF.intersects(pufferfishes.get(j).getHitbox(), harpoons.get(i).getRect())) {
                                if (!harpoons.get(i).isAHit) {
                                    if (!pufferfishes.get(j).isDead) {
                                        harpoons.get(i).isShot = false;
                                        pufferfishes.get(j).isDead = true;
                                        harpoons.get(i).isAHit = true;
                                        pufferfishes.get(j).killHarpoon = harpoons.get(i);
                                        harpoons.get(i).deadPufferfish = pufferfishes.get(j);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Update dolphins
            for (int i = 0; i < dolphins.size(); i++) {
                if (dolphins.get(i).isVisible()) {
                    dolphins.get(i).update(fps, scrollSpeed);
                    dolphins.get(i).getCurrentFrame();

                    //Check for collision between player and dolphins
                    if (RectF.intersects(dolphins.get(i).getHitbox(), player.getHitbox())) {
                        if (!dolphins.get(i).isDead) {
                            gameState = GAME_OVER;
                            levelMusic.pause();
                            levelMusic.reset();
                        } else {
                            dolphins.get(i).isVisible = false;
                            dolphins.get(i).killHarpoon = null;
                            dolphins.get(i).isDead = false;
                            gold += 4;
                        }
                    }

                    //Generate spear if possible
                    if (dolphins.get(i).takeAim(player.getY(), player.getHeight())) {
                        for (int j = 0; j < spears.size(); j++) {
                            if (!spears.get(j).isVisible) {
                                spears.get(j).thrower = dolphins.get(i);
                                spears.get(j).shoot(dolphins.get(i).getX(), dolphins.get(i).getY());
                                spears.get(j).isVisible = true;
                                break;
                            }
                        }
                    }
                }
            }

            //Update spears
            for (int i = 0; i < spears.size(); i++) {
                if (spears.get(i).isVisible) {
                    spears.get(i).update(fps, scrollSpeed);

                    //Check for collision between spear and player
                    if (RectF.intersects(spears.get(i).getRect(), player.getRect())) {
                        gameState = GAME_OVER;
                        levelMusic.pause();
                        levelMusic.reset();
                    }
                    if (spears.get(i).getX() < -spears.get(i).getWidth()) {
                        spears.get(i).isVisible = false;
                        spears.get(i).thrower.spearThrown = false;
                    }
                }
            }

            //Update sharks
            for (int i = 0; i < sharks.size(); i++) {
                if (sharks.get(i).isVisible()) {
                    sharks.get(i).update(fps, scrollSpeed);
                    sharks.get(i).getCurrentFrame();

                    //Check for collision between player and sharks
                    if (RectF.intersects(sharks.get(i).getHitbox(), player.getHitbox())) {
                        if (!sharks.get(i).isDead) {
                            gameState = GAME_OVER;
                            levelMusic.pause();
                            levelMusic.reset();
                        } else {
                            sharks.get(i).isVisible = false;
                            sharks.get(i).killHarpoon = null;
                            sharks.get(i).isDead = false;
                            sharks.get(i).life = 2;
                            gold += 2;
                        }
                    }
                }
            }
            //Update swordfishes
            for (int i = 0; i < swordfishes.size(); i++) {
                if (swordfishes.get(i).isVisible()) {
                    swordfishes.get(i).update(fps, scrollSpeed);
                    swordfishes.get(i).getCurrentFrame();

                    //Check for collision between player and swordfishes
                    if (RectF.intersects(swordfishes.get(i).getHitbox(), player.getHitbox())) {
                        if (!swordfishes.get(i).isDead) {
                            gameState = GAME_OVER;
                            levelMusic.pause();
                            levelMusic.reset();
                        } else {
                            swordfishes.get(i).isVisible = false;
                            swordfishes.get(i).killHarpoon = null;
                            swordfishes.get(i).isDead = false;
                            gold++;
                        }
                    }
                }
            }

            //Update Pufferfishes
            for (int i = 0; i < pufferfishes.size(); i++) {
                if (pufferfishes.get(i).isVisible()) {
                    pufferfishes.get(i).update(fps, scrollSpeed);
                    pufferfishes.get(i).getCurrentFrame();

                    //Check for collision between player and pufferfishes
                    if (RectF.intersects(pufferfishes.get(i).getHitbox(), player.getRect())) {
                        if (!pufferfishes.get(i).isDead) {
                            gameState = GAME_OVER;
                            levelMusic.pause();
                            levelMusic.reset();
                        } else {
                            pufferfishes.get(i).isVisible = false;
                            pufferfishes.get(i).killHarpoon = null;
                            pufferfishes.get(i).isDead = false;
                            gold += 8;
                        }
                    }
                }
            }


            //Update seaweeds
            for (int i = 0; i < seaweeds.size(); i++) {
                if (seaweeds.get(i).isVisible) {
                    seaweeds.get(i).update(scrollSpeed, fps);
                    seaweeds.get(i).getCurrentFrame();
                }
            }

            //Update bubble
            if (bubble.isVisible) {
                bubble.update(scrollSpeed, fps);
                bubble.getCurrentFrame();

                //Check for collision between player and bubble
                if (RectF.intersects(bubble.getRect(), player.getRect())) {
                    bubble.setVisible(false);
                    if (player.getOxygenLevel() < 2 + oxygenUpgradeLevel) {
                        player.setOxygenLevel();
                    }
                }
            } else {
                if (randomNumberGenerator.nextInt(1000) == 999) {
                    float randomY = randomNumberGenerator.nextFloat() * (screenY - (screenY / 10));
                    bubble.generate(randomY);
                }
            }

            //Check for collision between player and boat
            if (RectF.intersects(player.getHitbox(), boat.getHitbox())) {
                firstRun = false;
                gameState = GAME_UPGRADING;
                //Change to boat music (boat music lives in Touch Event
                levelMusic.pause();
                boatMusic.start();
            }
        }
    }


    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            //Draw Start Screen
            if (gameState == GAME_START) {
                if(firstRun) {
                    if(titleSound != null){
                        try{
                            Log.v("i'm here", ""+titleSound);
                            titleSound.start();
                        } catch(IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                }
                canvas.drawColor(Color.argb(255, 105, 255, 217));
                paint.setColor(Color.argb(255, 0, 29, 77));
                paint.setTextSize(100);
                canvas.drawText("DEEP FISH", screenX / 2 - 230, screenY / 2, paint);
                paint.setTextSize(60);
                canvas.drawText("touch screen to start", screenX / 2 - 250, screenY / 2 + 80, paint);

                //Draw Game
            } else if(gameState == GAME_PLAYING) {
                canvas.drawColor(Color.argb(255, 44, 94, 171));
                paint.setColor(Color.argb(255, 121, 192, 233));
                canvas.drawRect(0, 0, screenX, screenY/20, paint);
                paint.setColor(Color.argb(255, 250, 234, 182));
                canvas.drawRect(0, screenY, screenX, screenY-screenY/30, paint);

                //Draw Player
                canvas.drawBitmap(player.getBitmap(), player.getFrameToDraw(), player.getRect(), paint);

                //Draw Score
                paint.setColor(Color.argb(255, 163, 215, 228));
                paint.setTextSize(38);
                canvas.drawText("Gold: " + gold, 20, 40, paint);

                //Draw Boat
                canvas.drawBitmap(boat.getBitmap(), boat.getFrameToDraw(), boat.getRect(), paint);

                //Draw Oxygen Meter
                int bubbleMeterPosition = (int) screenX/50;
                int bubbleMeterSpacing = (int) screenX/50;

                if (player.getOxygenLevel() == 1) {
                    if (lastBubbleBlink == 0) {
                        lastBubbleBlink = System.currentTimeMillis();
                    }
                    if(System.currentTimeMillis() - lastBubbleBlink > bubbleBlinkInterval) {
                        if(bubbleBlinkEmpty) {
                            bubbleBlinkEmpty = false;
                        } else {
                            bubbleBlinkEmpty = true;
                        }
                        lastBubbleBlink = System.currentTimeMillis();
                    }
                }


                for(int i = 0; i < player.getOxygenLevel(); i++){
                    if (player.getOxygenLevel() == 1) {
                        if (bubbleBlinkEmpty) {
                            canvas.drawBitmap(emptyBubbleMeter, bubbleMeterPosition, screenY / 15, paint);
                            bubbleMeterPosition += bubbleMeterSpacing;
                        } else {
                            canvas.drawBitmap(fullBubbleMeter, bubbleMeterPosition, screenY / 15, paint);
                            bubbleMeterPosition += bubbleMeterSpacing;
                        }
                    } else {
                        canvas.drawBitmap(fullBubbleMeter, bubbleMeterPosition, screenY / 15, paint);
                        bubbleMeterPosition += bubbleMeterSpacing;
                    }
                }

                for(int i = 0; i < ((oxygenUpgradeLevel+2)-player.getOxygenLevel()); i++) {
                    canvas.drawBitmap(emptyBubbleMeter, bubbleMeterPosition, screenY/15, paint);
                    bubbleMeterPosition += bubbleMeterSpacing;
                }

                //Draw harpoon key
                canvas.drawBitmap(harpoonKey, screenX/35, screenY/8, paint);
                canvas.drawText("x"+harpoonCount, screenX/10, screenY/7, paint);

                //Draw Joystick
                paint.setColor(Color.WHITE);
                paint.setAlpha(90);
                canvas.drawCircle(circleDefaultX, circleDefaultY, joystickRadius, paint);
                canvas.drawCircle(circleXPosition, circleYPosition, (float) (.07 * screenY), paint);
                paint.setAlpha(255);

                if(firstRun){
                    canvas.drawText("Board the boat to end", screenX-screenX/3, screenY/6, paint);
                    canvas.drawText("the round and buy upgrades", screenX-screenX/3, screenY/5, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    RectF left = new RectF(screenX/80, screenY/80, screenX/2-screenX/100, screenY-screenY/80);
                    canvas.drawRoundRect(left,200, 200, paint);
                    RectF right = new RectF(screenX/2+screenX/100, screenY/80, screenX-screenX/80, screenY-screenY/80);
                    canvas.drawRoundRect(right,200, 200, paint);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawText("Control the joystick", screenX/7, screenY/2, paint);
                    canvas.drawText("on the left side", screenX/7, screenY/2+screenY/20, paint);
                    canvas.drawText("Fire harpoons by", screenX-screenX/3, screenY/2, paint);
                    canvas.drawText("clicking on the right side", screenX-screenX/3, screenY/2+screenY/20, paint);
                    canvas.drawText("Don't run out of oxygen!", screenX/10, screenY/10, paint);
                    playing = false;
                }

                //Draw Harpoons
                for (int i = 0; i < harpoons.size(); i++) {
                    if (harpoons.get(i).isVisible) {
                        if (!harpoons.get(i).isAngled) {
                            canvas.drawBitmap(harpoons.get(i).getBitmap(), harpoons.get(i).getX(), harpoons.get(i).getY(), paint);
                        } else {
                            canvas.save();
                            canvas.rotate(45, harpoons.get(i).getX(), harpoons.get(i).getY());
                            canvas.drawBitmap(harpoons.get(i).getBitmap(), harpoons.get(i).getX(), harpoons.get(i).getY(), paint);
                            canvas.restore();
                        }
                    }
                }

                //Draw Dolphins
                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i < dolphins.size(); i++) {
                    if (dolphins.get(i).isVisible) {
                        canvas.drawBitmap(dolphins.get(i).getBitmap(), dolphins.get(i).getFrameToDraw(), dolphins.get(i).getRect(), paint);
                    }
                }

                //Draw Spears
                for(int i = 0; i < spears.size(); i++) {
                    if(spears.get(i).isVisible) {
                        canvas.drawBitmap(spears.get(i).getBitmap(), spears.get(i).getX(), spears.get(i).getY(), paint);
                    }
                }

                //Draw Sharks
                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i <sharks.size(); i++) {
                    if (sharks.get(i).isVisible) {
                        canvas.drawBitmap(sharks.get(i).getBitmap(), sharks.get(i).getFrameToDraw(), sharks.get(i).getRect(), paint);
                    }
                }

                //Draw Swordfishes
                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i <swordfishes.size(); i++) {
                    if (swordfishes.get(i).isVisible) {
                        canvas.drawBitmap(swordfishes.get(i).getBitmap(), swordfishes.get(i).getFrameToDraw(), swordfishes.get(i).getRect(), paint);
                    }
                }

                //Draw Pufferfishes
                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i <pufferfishes.size(); i++) {
                    if (pufferfishes.get(i).isVisible) {
                        canvas.drawBitmap(pufferfishes.get(i).getBitmap(), pufferfishes.get(i).getFrameToDraw(), pufferfishes.get(i).getRect(), paint);
                    }
                }

                //Draw Seaweed
                for (int i = 0; i < seaweeds.size(); i++) {
                    if (seaweeds.get(i).isVisible) {
                        canvas.drawBitmap(seaweeds.get(i).getBitMap(), seaweeds.get(i).getFrameToDraw(), seaweeds.get(i).getRect(), paint);
                    }
                }

                //Draw Bubble
                if (bubble.isVisible) {
                    canvas.drawBitmap(bubble.getBitmap(), bubble.getFrameToDraw(), bubble.getRect(), paint);
                }

                //Draw upgrade screen
            } else if(gameState == GAME_UPGRADING) {



                canvas.drawColor(Color.argb(255, 46, 191, 188));
                paint.setColor(Color.argb(255, 191, 46, 49));
                paint.setTextSize(screenY/10);
                canvas.drawText("Upgrades", screenX/2-screenY/5, 2*screenY/20, paint);

                //Draw Upgrade Titles
                paint.setTextSize(screenY/14);
                canvas.drawText("Harpoons", screenX/25, (5*screenY)/20, paint);
                canvas.drawText("Oxygen Tank", screenX/25, 8*screenY/20, paint);
                canvas.drawText("Swim Speed", screenX/25, 11*screenY/20, paint);
                canvas.drawText("Lung Capacity", screenX/25, 14*screenY/20, paint);

                //Draw filled upgrade boxes
                for(int i = 0; i < harpoonUpgradeLevel; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (4*screenY)/20, 19*screenX/55+(i*screenY/19), (16*screenY)/60, paint);
                }
                for(int i = 0; i < oxygenUpgradeLevel; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (7*screenY)/20, 19*screenX/55+(i*screenY/19), (25*screenY)/60, paint);
                }
                for(int i = 0; i < speedUpgradeLevel; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (10*screenY)/20, 19*screenX/55+(i*screenY/19), (34*screenY)/60, paint);
                }
                for(int i = 0; i < lungsUpgradeLevel; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (13*screenY)/20, 19*screenX/55+(i*screenY/19), (43*screenY)/60, paint);
                }

                if(harpoonUpgradeCosts[harpoonUpgradeLevel] != -1) {

                }

                if(harpoonUpgradeLevel<10) {
                    canvas.drawText("Cost: " + harpoonUpgradeCosts[harpoonUpgradeLevel], 42*screenX/55, 5*screenY/20, paint);
                }
                if(oxygenUpgradeLevel<10) {
                    canvas.drawText("Cost: " + oxygenUpgradeCosts[oxygenUpgradeLevel], 42 * screenX / 55, 8 * screenY / 20, paint);
                }
                if(speedUpgradeLevel<10) {
                    canvas.drawText("Cost: " + speedUpgradeCosts[speedUpgradeLevel], 42 * screenX / 55, 11 * screenY / 20, paint);
                }
                if(lungsUpgradeLevel<10) {
                    canvas.drawText("Cost: " + lungsUpgradeCosts[lungsUpgradeLevel], 42 * screenX / 55, 14 * screenY / 20, paint);
                }

                //Draw Upgrade Buttons

                paint.setColor(Color.argb(255, 114, 46, 191));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.argb(255, 0, 0, 0));
                Path path = new Path();
                if(harpoonUpgradeLevel < 10 && gold >= harpoonUpgradeCosts[harpoonUpgradeLevel]) {
                    canvas.drawCircle(37*screenX/55, 14*screenY/60, 5*screenY/120, paint);
                    path.moveTo(37*screenX/55, 15*screenY/60);
                    path.lineTo(37*screenX/55, 13*screenY/60);
                    path.moveTo(75*screenX/110, 14*screenY/60);
                    path.lineTo(73*screenX/110, 14*screenY/60);
                }
                if(oxygenUpgradeLevel < 10 && gold >= oxygenUpgradeCosts[oxygenUpgradeLevel]) {
                    canvas.drawCircle(37*screenX/55, 23*screenY/60, 5*screenY/120, paint);
                    path.moveTo(37*screenX/55, 24*screenY/60);
                    path.lineTo(37*screenX/55, 22*screenY/60);
                    path.moveTo(75*screenX/110, 23*screenY/60);
                    path.lineTo(73*screenX/110, 23*screenY/60);
                }
                if(speedUpgradeLevel < 10 && gold >= speedUpgradeCosts[speedUpgradeLevel]) {
                    canvas.drawCircle(37*screenX/55, 32*screenY/60, 5*screenY/120, paint);
                    path.moveTo(37*screenX/55, 33*screenY/60);
                    path.lineTo(37*screenX/55, 31*screenY/60);
                    path.moveTo(75*screenX/110, 32*screenY/60);
                    path.lineTo(73*screenX/110, 32*screenY/60);
                }
                if(lungsUpgradeLevel < 10 && gold >= lungsUpgradeCosts[lungsUpgradeLevel]) {
                    canvas.drawCircle(37*screenX/55, 41*screenY/60, 5*screenY/120, paint);
                    path.moveTo(37*screenX/55, 42*screenY/60);
                    path.lineTo(37*screenX/55, 40*screenY/60);
                    path.moveTo(75*screenX/110, 41*screenY/60);
                    path.lineTo(73*screenX/110, 41*screenY/60);
                }
                canvas.drawPath(path, paint);

                //Draw Upgrade Boxes
                for(int i = harpoonUpgradeLevel; i < 10; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (4*screenY)/20, 19*screenX/55+(i*screenY/19), (16*screenY)/60, paint);
                }
                for(int i = oxygenUpgradeLevel; i < 10; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (7*screenY)/20, 19*screenX/55+(i*screenY/19), (25*screenY)/60, paint);
                }
                for(int i = speedUpgradeLevel; i < 10; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (10*screenY)/20, 19*screenX/55+(i*screenY/19), (34*screenY)/60, paint);
                }
                for(int i = lungsUpgradeLevel; i < 10; i++) {
                    canvas.drawRect(18*screenX/55+(i*screenY/19), (13*screenY)/20, 19*screenX/55+(i*screenY/19), (43*screenY)/60, paint);
                }

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 126, 194, 48));

                canvas.drawRect(16*screenX/20, 17*screenY/20, 19*screenX/20, 19*screenY/20, paint);
                paint.setTextSize(40);
                paint.setColor(Color.argb(255, 0,0,0));
                canvas.drawText("FISH", 269*screenX/320, 147*screenY/160, paint);
                canvas.drawText("Gold: "+ gold, 240*screenX/320, screenY/10, paint);

                //Draw game over screen
            } else if(gameState == GAME_OVER) {
                firstRun = false;
                canvas.drawColor(Color.argb(255, 105, 255, 217));
                paint.setColor(Color.argb(255, 0, 29, 77));
                paint.setTextSize(100);

                isMoving = false;
                canvas.drawText("GAME OVER", screenX/2-screenX/6, screenY/3, paint);
                levelMusic.pause();
                levelMusic.reset();
                canvas.drawBitmap(player.getBitmap(), player.getFrameToDraw(), player.getRect(), paint);
                player.isPlayerDead(true);
                player.getCurrentFrame();
                player.update(fps, circleXPosition, circleYPosition, circleDefaultX, circleDefaultY, scrollSpeed);
                skull.update();
                skull.getCurrentFrame();
                canvas.drawBitmap(skull.getBitmap(), skull.getFrameToDraw(), skull.getRect(), paint);

            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error: ", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (gameState == GAME_START) {
                        prepareLevel(mContext);
                        gameState = GAME_PLAYING;
                    } else if (gameState == GAME_PLAYING) {
                        if (firstRun) {
                            prepareLevel(mContext);
                            resume();
                            playing = true;
                            firstRun = false;
                        } else {
                            int actionIndexDown = motionEvent.getActionIndex();
                            if (motionEvent.getX(actionIndexDown) < screenX / 2) {

                                //Move joystick if touching left side of screen
                                isMoving = true;
                                player.setFrameLength(200);
                                pointerX = motionEvent.getX(actionIndexDown);
                                pointerY = motionEvent.getY(actionIndexDown);
                            } else {

                                //fire harpoon if touching right side of screen
                                for (int i = 0; i < harpoons.size(); i++) {
                                    if (!harpoons.get(i).isVisible) {
                                        harpoons.get(i).shoot(player.getX() + (player.getWidth() / 3), player.getY() + player.getHeight() / 2);
                                        Log.d("Is visible", harpoons.get(i).isAHit + "");
                                        harpoonCount--;
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (gameState == GAME_UPGRADING) {
                        if (motionEvent.getX() > (upgradeX - upgradeButtonRadius) && motionEvent.getX() < (upgradeX + upgradeButtonRadius)) {
                            if (motionEvent.getY() > (upgradeHarpoonY - upgradeButtonRadius) && motionEvent.getY() < (upgradeHarpoonY + upgradeButtonRadius)) {
                                if (harpoonUpgradeLevel < 10) {
                                    if (gold >= harpoonUpgradeCosts[harpoonUpgradeLevel]) {
                                        gold -= harpoonUpgradeCosts[harpoonUpgradeLevel];
                                        harpoonUpgradeLevel++;
                                        Log.d("UPGRADELEVEL", "" + harpoonUpgradeLevel);
                                    }
                                }
                            } else if (motionEvent.getY() > (upgradeOxygenY - upgradeButtonRadius) && motionEvent.getY() < (upgradeOxygenY + upgradeButtonRadius)) {
                                if (oxygenUpgradeLevel < 10) {
                                    if (gold >= oxygenUpgradeCosts[oxygenUpgradeLevel]) {
                                        gold -= oxygenUpgradeCosts[oxygenUpgradeLevel];
                                        oxygenUpgradeLevel++;
                                        Log.d("UPGRADELEVEL", "" + oxygenUpgradeLevel);
                                    }
                                }
                            } else if (motionEvent.getY() > (upgradeSpeedY - upgradeButtonRadius) && motionEvent.getY() < (upgradeSpeedY + upgradeButtonRadius)) {
                                if (speedUpgradeLevel < 10) {
                                    if (gold >= speedUpgradeCosts[speedUpgradeLevel]) {
                                        gold -= speedUpgradeCosts[speedUpgradeLevel];
                                        speedUpgradeLevel++;
                                        Log.d("UPGRADELEVEL", "" + speedUpgradeLevel);
                                    }
                                }
                            } else if (motionEvent.getY() > (upgradeLungsY - upgradeButtonRadius) && motionEvent.getY() < (upgradeLungsY + upgradeButtonRadius)) {
                                if (lungsUpgradeLevel < 10) {
                                    if (gold >= lungsUpgradeCosts[lungsUpgradeLevel]) {
                                        gold -= lungsUpgradeCosts[lungsUpgradeLevel];
                                        lungsUpgradeLevel++;
                                        Log.d("UPGRADELEVEL", "" + lungsUpgradeLevel);
                                    }

                                }
                            }
                        } else if (motionEvent.getX() > doneUpgradingX && motionEvent.getX() < doneUpgradingX + doneUpgradingWidth) {
                            if (motionEvent.getY() > doneUpgradingY && motionEvent.getY() < doneUpgradingY + doneUpgradingHeight) {
                                prepareLevel(mContext);
                                gameState = GAME_PLAYING;
                                //Boat Music stops and Level Music resumes
                                boatMusic.pause();
                                levelMusic.start();
                            }
                        }

                    } else if (gameState == GAME_OVER) {
                        gameState = GAME_START;
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    int count = motionEvent.getPointerCount();
                    for (int i = 0; i < count; i++) {
                        if (motionEvent.getX(i) < screenX / 2) {
                            pointerX = motionEvent.getX(i);
                            pointerY = motionEvent.getY(i);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (gameState == GAME_PLAYING) {
                        int actionIndexUp = motionEvent.getActionIndex();
                        if (motionEvent.getX(actionIndexUp) < screenX / 2) {
                            isMoving = false;
                            player.setFrameLength(700);
                            pointerX = circleDefaultX;
                            pointerY = circleDefaultY;
                        }
                    }
                    break;
            }

        return true;

    }
}
