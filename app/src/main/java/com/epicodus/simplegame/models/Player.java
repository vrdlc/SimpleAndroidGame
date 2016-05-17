package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;

/**
 * Created by Guest on 5/16/16.
 */
public class Player {
    private int oxygenLevel;
    private int frameCount;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLength;
    private Rect frameToDraw;

    private float x;
    private float y;

    private float width;
    private float height;

    public float screenX;
    public float screenY;

    private RectF rect;
    private Bitmap bitmap;

    float xVel;
    float yVel;

    public Player(Context context, float screenX, float screenY) {
        oxygenLevel = 10;
        x = (float) (screenX*0.8);
        y = screenY/5;
        width = screenX/5;
        height = screenY/9;
        xVel = 0;
        yVel = 0;
        rect = new RectF();
        this.screenX = screenX;
        this.screenY = screenY;
        frameCount = 3;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 200;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scuba);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public RectF getRect() {
        return rect;
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLength) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame > frameCount-1) {
                currentFrame = 0;
            }
        }
        frameToDraw.left = currentFrame * (int) width;
        frameToDraw.right = frameToDraw.left + (int) width;
    }

    public void update(long fps, float circleXPosition, float circleYPosition, float circleDefaultX, float circleDefaultY, float scrollSpeed) {
        xVel = (circleXPosition - circleDefaultX) * 10;
        yVel = (circleYPosition - circleDefaultY) * 10;
        if(fps > 0) {
            x = x + xVel/fps;
            y = y + yVel/fps;
            x = x-scrollSpeed/fps;
        }

        if (y < 0) {
            y = 0;
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenX) {
            x = screenX - width;
        }
        if (y + height > screenY) {
            y = screenY - height;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }
}
