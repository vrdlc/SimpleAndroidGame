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
    Bitmap player;
    boolean isMoving = false;
    float swimSpeedPerSecond = 150;
    float playerXPosition = 10;
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

    public GameView(Context context, float x, float y) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        circleDefaultX = (float) (0.875*screenX);
        circleDefaultY = (float) (0.75*screenY);
        pointerX = circleDefaultX;
        pointerY = circleDefaultY;
        joystickRadius = (float) .1*screenY;
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
        if(isMoving) {
            playerXPosition = playerXPosition + (swimSpeedPerSecond / fps);
        }

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


    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(45);
            canvas.drawText("FPS: " + fps, 20, 40, paint);
            canvas.drawRect(playerXPosition, 200, playerXPosition + 100, 300, paint);
            canvas.drawCircle((float) (0.875*screenX), (float) (0.75*screenY), joystickRadius, paint);
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
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isMoving = true;
                pointerX = motionEvent.getX();
                pointerY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                pointerX = motionEvent.getX();
                pointerY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                pointerX = circleDefaultX;
                pointerY = circleDefaultY;
                break;
        }
        return true;
    }

}
