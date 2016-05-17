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
    Seaweed seaweed;
    Player player;
    ArrayList<Harpoon> harpoons = new ArrayList<>();
    ArrayList<Dolphin> dolphins = new ArrayList<>();
    Random randomNumberGenerator;

    public GameView(Context context, float x, float y) {
        super(context);
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
        player = new Player(context, screenX, screenY);
        for (int i=0; i < 3; i++){
            harpoons.add(new Harpoon(context, screenX, screenY));
        }
        joystickPointerId = -1;
        seaweed = new Seaweed(screenX, screenY, context);
        randomNumberGenerator = new Random();
        for (int i = 0; i < 4; i++) {
            dolphins.add(new Dolphin(context, screenX, screenY));
        }
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

        seaweed.update(scrollSpeed, fps);
        seaweed.getCurrentFrame();

        player.update(fps, circleXPosition, circleYPosition, circleDefaultX, circleDefaultY, scrollSpeed);

        player.getCurrentFrame();


        for(int i = 0; i < harpoons.size(); i++){
            if(harpoons.get(i).isVisible){
                harpoons.get(i).update(fps, scrollSpeed);
                if (RectF.intersects(harpoons.get(i).getRect(), player.getRect())) {
                    if (!harpoons.get(i).isShot) {
                        harpoons.get(i).isVisible = false;
                        harpoons.get(i).isAngled = false;

                    }
                }
            }
        }
        int randomNumber = randomNumberGenerator.nextInt(500);
        if (randomNumber == 499) {
            Log.d("random", ""+randomNumber);
            for(int i = 0; i < dolphins.size(); i++) {
                if(!dolphins.get(i).isVisible) {
                    float randomY = randomNumberGenerator.nextFloat()*(screenY-(screenY/10));
                    dolphins.get(i).generate(randomY);
                    Log.d("dolphin", "generated");
                    break;
                }
            }
        }
        for(int i = 0; i < dolphins.size(); i++) {
            if(dolphins.get(i).isVisible()) {
                dolphins.get(i).update(fps, scrollSpeed);
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
            } else {
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);
                canvas.drawText("FPS: " + fps, 20, 40, paint);
                canvas.drawCircle(circleDefaultX, circleDefaultY, joystickRadius, paint);
                paint.setColor(Color.argb(255, 37, 25, 255));
                canvas.drawCircle(circleXPosition, circleYPosition, (float) (.07*screenY), paint);

                canvas.drawBitmap(player.getBitmap(), player.getFrameToDraw(), player.getRect(), paint);

                for(int i = 0; i < harpoons.size(); i++){
                    if(harpoons.get(i).isVisible){
                        if(!harpoons.get(i).isAngled) {
                            canvas.drawRect(harpoons.get(i).getRect(), paint);
                        } else {
                            canvas.save();
                            canvas.rotate(45, harpoons.get(i).getX(), harpoons.get(i).getY());
                            canvas.drawRect(harpoons.get(i).getRect(), paint);
                            canvas.restore();
                        }

                    }
                }

                paint.setColor(Color.argb(255, 255, 0, 234));
                for (int i = 0; i < dolphins.size(); i++) {
                    if(dolphins.get(i).isVisible) {
                        canvas.drawRect(dolphins.get(i).getRect(), paint);
                    }
                }
                canvas.drawBitmap(seaweed.getBitMap(), seaweed.getFrameToDraw(), seaweed.getRect(), paint);
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
                        gameState = GAME_PLAYING;
                    } else {
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
                                if(!harpoons.get(i).isVisible){
                                    harpoons.get(i).shoot(player.getX(), player.getY());
                                    break;
                                }
                            }
                        }
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
