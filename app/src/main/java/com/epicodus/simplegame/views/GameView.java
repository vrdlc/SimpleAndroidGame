package com.epicodus.simplegame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.epicodus.simplegame.models.Bubble;
import com.epicodus.simplegame.models.Dolphin;
import com.epicodus.simplegame.models.Harpoon;
import com.epicodus.simplegame.models.Player;
import com.epicodus.simplegame.models.Seaweed;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Guest on 5/16/16.
 */
public class GameView extends SurfaceView implements Runnable {
    public static final int GAME_START = 0;
    public static final int GAME_PLAYING = 1;
    public static final int GAME_UPGRADING = 2;
    public static final int GAME_OVER = 3;
    public static final String TAG = GameView.class.getSimpleName();
    Thread gameThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playing;
    Canvas canvas;
    Paint paint;
    long fps;
    private long timeThisFrame;
    boolean isMoving = false;
    boolean isShooting = false;
    float swimSpeedPerSecond = 150;
    float playerXPosition = 10;
    float playerYPosition = 400;
    float scrollSpeed = 40;
    float screenX;
    float screenY;
    float pointerX;
    float pointerY;
    float circleXPosition;
    int gameState;
    float circleYPosition;
    float circleDefaultX;
    float circleDefaultY;
    float deltaX;
    float deltaY;
    float distance;
    float theta;
    float joystickRadius;
    int joystickPointerId;
    Player player;
    ArrayList<Harpoon> harpoons = new ArrayList<>();
    ArrayList<Dolphin> dolphins = new ArrayList<>();
    ArrayList<Seaweed> seaweeds = new ArrayList<>();
    Bubble bubble;
    Random randomNumberGenerator;
    Context mContext;
    int score;

    public GameView(Context context, float x, float y) {
        super(context);
        mContext = context;
        gameState = GAME_START;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        circleDefaultX = (float) (0.15*screenX);
        circleDefaultY = (float) (0.78*screenY);
        pointerX = circleDefaultX;
        pointerY = circleDefaultY;
        joystickRadius = (float) .1*screenY;
    }

    public void prepareLevel(Context context) {
        dolphins.clear();
        harpoons.clear();
        player = new Player(context, screenX, screenY);
        for (int i=0; i < 4; i++){
            harpoons.add(new Harpoon(context, screenX, screenY));
        }

        for (int i=0; i < 10; i++) {
            seaweeds.add(new Seaweed(context, screenX, screenY));
        }

        joystickPointerId = -1;

        randomNumberGenerator = new Random();

        for (int i = 0; i < 4; i++) {
            dolphins.add(new Dolphin(context, screenX, screenY));
        }

        bubble = new Bubble(screenX, screenY, context);
        score = 0;
    }

    @Override
    public void run() {
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

        if(gameState == GAME_PLAYING) {
            playerXPosition = playerXPosition + (swimSpeedPerSecond / fps);
            deltaX = pointerX-circleDefaultX;
            deltaY = pointerY-circleDefaultY;
            distance = (float) Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            theta = (float) Math.atan2(deltaY, deltaX);

            if(distance <= joystickRadius) {
                circleXPosition = pointerX;
                circleYPosition = pointerY;
            } else {
                circleXPosition = (float)(circleDefaultX + (joystickRadius)*Math.cos(theta));
                circleYPosition = (float)(circleDefaultY + (joystickRadius)*Math.sin(theta));
            }

            int randomDolphinNumber = randomNumberGenerator.nextInt(250);
            if (randomDolphinNumber == 249) {
                for(int i = 0; i < dolphins.size(); i++) {
                    if(!dolphins.get(i).isVisible) {
                        float randomY = randomNumberGenerator.nextFloat()*(screenY-(screenY/10));
                        dolphins.get(i).generate(randomY);
                        dolphins.get(i).isDead = false;
                        Log.d("dolphin dead? ", dolphins.get(i).isDead+"");
                        break;
                    }
                }
            }

            int randomSeaweedNumber = randomNumberGenerator.nextInt(350);
            if (randomSeaweedNumber == 349) {
                for(int i=0; i < seaweeds.size(); i++) {
                    if(!seaweeds.get(i).isVisible) {
                        seaweeds.get(i).generate();
                        Log.d("generated", "seaweed");
                        break;
                    }
                }
            }

            for (int i=0; i<seaweeds.size(); i++) {
                if(seaweeds.get(i).isVisible) {
                    seaweeds.get(i).update(scrollSpeed, fps);
                    seaweeds.get(i).getCurrentFrame();
                }
            }

            player.update(fps, circleXPosition, circleYPosition, circleDefaultX, circleDefaultY, scrollSpeed);

            player.getCurrentFrame();

            for(int i = 0; i < harpoons.size(); i++){
                if(harpoons.get(i).isVisible){
                    harpoons.get(i).update(fps, scrollSpeed);
                    if (RectF.intersects(harpoons.get(i).getRect(), player.getRect())) {
                        if (!harpoons.get(i).isShot) {
                            harpoons.get(i).deadDolphin = null;
                            harpoons.get(i).isVisible = false;
                            harpoons.get(i).isAngled = false;
                        }
                    }
                    for (int j=0; j<dolphins.size(); j++) {
                        if(dolphins.get(j).isVisible) {
                            if(RectF.intersects(dolphins.get(j).getRect(), harpoons.get(i).getRect())) {
                                if(!harpoons.get(i).isAHit) {
                                    if(!dolphins.get(j).isDead) {
                                        harpoons.get(i).isShot = false;
                                        dolphins.get(j).isDead = true;
                                        harpoons.get(i).isAHit = true;
                                        dolphins.get(j).killHarpoon = harpoons.get(i);
                                        harpoons.get(i).deadDolphin = dolphins.get(j);
                                        score++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int randomNumber = randomNumberGenerator.nextInt(250);
            if (randomNumber == 249) {
                for(int i = 0; i < dolphins.size(); i++) {
                    if(!dolphins.get(i).isVisible) {
                        float randomY = randomNumberGenerator.nextFloat()*(screenY-(screenY/10));
                        dolphins.get(i).generate(randomY);
                        dolphins.get(i).isDead = false;
                        break;
                    }
                }
            }

            for (int i = 0; i < dolphins.size(); i++) {
                if(dolphins.get(i).isVisible()) {
                    dolphins.get(i).update(fps, scrollSpeed);
                    if(RectF.intersects(dolphins.get(i).getRect(), player.getRect())) {
                        if(!dolphins.get(i).isDead) {
                            gameState = GAME_OVER;
                        } else {
                            dolphins.get(i).isVisible = false;
                            dolphins.get(i).killHarpoon = null;
                            dolphins.get(i).isDead = false;
                        }
                    }
                }
            }
            if(bubble.isVisible){
                bubble.update(scrollSpeed, fps);
                bubble.getCurrentFrame();
                if(RectF.intersects(bubble.getRect(), player.getRect())){
                    bubble.setVisible(false);
                    player.setOxygenLevel();
                }
            } else {
                if(randomNumberGenerator.nextInt(1000) == 999){
                    float randomY = randomNumberGenerator.nextFloat()*(screenY-(screenY/10));
                    bubble.generate(randomY);
                }
            }
        }

    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            if (gameState == GAME_START) {
                canvas.drawColor(Color.argb(255, 105, 255, 217));
                paint.setColor(Color.argb(255, 0, 29, 77));
                paint.setTextSize(100);
                canvas.drawText("DEEP FISH", screenX/2-230, screenY/2, paint);
                paint.setTextSize(60);
                canvas.drawText("touch screen to start", screenX/2-250, screenY/2+80, paint);
            } else if(gameState == GAME_PLAYING) {
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);
                canvas.drawText("Score: " + score, 20, 40, paint);
                canvas.drawCircle(circleDefaultX, circleDefaultY, joystickRadius, paint);
                paint.setColor(Color.argb(255, 37, 25, 255));
                canvas.drawCircle(circleXPosition, circleYPosition, (float) (.07*screenY), paint);
                canvas.drawBitmap(player.getBitmap(), player.getFrameToDraw(), player.getRect(), paint);

                for(int i = 0; i < harpoons.size(); i++) {
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

                if(bubble.isVisible){
                    canvas.drawBitmap(bubble.getBitmap(), bubble.getFrameToDraw(), bubble.getRect(), paint);
                }

                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i < dolphins.size(); i++) {
                    if (dolphins.get(i).isVisible) {
                        canvas.drawRect(dolphins.get(i).getRect(), paint);
                    }
                }

                for (int i=0; i<seaweeds.size(); i++) {
                    if(seaweeds.get(i).isVisible) {
                        canvas.drawBitmap(seaweeds.get(i).getBitMap(), seaweeds.get(i).getFrameToDraw(), seaweeds.get(i).getRect(), paint);
                    }
                }


            } else if(gameState == GAME_OVER) {
                canvas.drawColor(Color.argb(255, 105, 255, 217));
                paint.setColor(Color.argb(255, 0, 29, 77));
                paint.setTextSize(100);
                canvas.drawText("GAME OVER", screenX/2-230, screenY/2, paint);
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
                    } else if(gameState == GAME_PLAYING) {
                        int actionIndexDown = motionEvent.getActionIndex();
                        if(motionEvent.getX(actionIndexDown) < screenX/2) {
                            joystickPointerId = motionEvent.getPointerId(actionIndexDown);
                            isMoving = true;
                            player.setFrameLength(200);
                            pointerX = motionEvent.getX(actionIndexDown);
                            pointerY = motionEvent.getY(actionIndexDown);
                        } else {
                            isShooting = true;
                            for(int i = 0; i < harpoons.size(); i++){
                                if(!harpoons.get(i).isVisible) {
                                    harpoons.get(i).shoot(player.getX()+(player.getWidth()/3), player.getY()+player.getHeight()/2);
                                    Log.d("Is visible", harpoons.get(i).isAHit+"");
                                    break;
                                }
                            }
                        }
                    } else if(gameState == GAME_OVER) {
                        gameState = GAME_START;
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    int count = motionEvent.getPointerCount();
                    for(int i = 0; i < count; i++) {
                        if(motionEvent.getX(i) < screenX/2) {
                            pointerX = motionEvent.getX(i);
                            pointerY = motionEvent.getY(i);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:

                    int actionIndexUp = motionEvent.getActionIndex();
                    if(motionEvent.getX(actionIndexUp) < screenX/2) {
                        joystickPointerId = motionEvent.getPointerId(actionIndexUp);
                        isMoving = false;
                        player.setFrameLength(700);
                        pointerX = circleDefaultX;
                        pointerY = circleDefaultY;
                    } else {
                        isShooting = false;
                    }
                    break;
            }

        return true;


    }
}
