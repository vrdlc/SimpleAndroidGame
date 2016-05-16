package com.epicodus.simplegame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.epicodus.simplegame.models.Player;

/**
 * Created by Guest on 5/16/16.
 */
public class GameView extends SurfaceView implements Runnable {
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
    float screenX;
    float screenY;
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
    Player player;

    public GameView(Context context, float x, float y) {
        super(context);
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

        player.update(fps, circleXPosition, circleYPosition, circleDefaultX, circleDefaultY);

    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(45);
            canvas.drawText("FPS: " + fps, 20, 40, paint);
            canvas.drawRect(player.getRect(), paint);
            canvas.drawCircle(circleDefaultX, circleDefaultY, joystickRadius, paint);
            paint.setColor(Color.argb(255, 37, 25, 255));
            canvas.drawCircle(circleXPosition, circleYPosition, (float) (.07*screenY), paint);

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
//        if (motionEvent.getX() < screenX / 2) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    int actionIndexDown = motionEvent.getActionIndex();
                    if(motionEvent.getX(actionIndexDown) < screenX/2) {
                        isMoving = true;
                        pointerX = motionEvent.getX(actionIndexDown);
                        pointerY = motionEvent.getY(actionIndexDown);
                    } else {
                        isShooting = true;
                        Log.d("ON ACTION DOWN", "DOES THIS WORK?" + isShooting);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int count = motionEvent.getPointerCount();
                    for(int i = 0; i < count; i++) {
                        if(motionEvent.getX(i) < screenX/2) {
                            pointerX = motionEvent.getX(i);
                            pointerY = motionEvent.getY(i);
                        } else {
                            pointerX = circleDefaultX;
                            pointerY = circleDefaultY;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    int actionIndexUp = motionEvent.getActionIndex();
                    if(motionEvent.getX(actionIndexUp) < screenX/2) {
                        isMoving = false;
                        pointerX = circleDefaultX;
                        pointerY = circleDefaultY;
                    } else {
                        isShooting = false;
                        Log.d("ON ACTION UP", "DOES THIS WORK?" + isShooting);
                    }
                    break;
            }

        return true;


    }
}
