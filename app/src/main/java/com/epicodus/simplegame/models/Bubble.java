package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import com.epicodus.simplegame.R;

/**
 * Created by Epicodus on 5/17/16.
 */
public class Bubble {
    private float x, y, screenX, screenY, width, height, startX, startY;
    private RectF rect;
    Bitmap bitmap;
    public boolean isVisible;
    Context context;
    private int frameCount, currentFrame, frameLength;
    private long lastFrameChangeTime;
    private Rect frameToDraw;

    public Bubble(float screenX, float screenY, Context context){
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX;
        y = screenY/2;
        width = screenX/22;
        height = screenY/20;
        rect = new RectF();
        this.context = context;
        frameCount = 2;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 1000;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void generate(float startY) {
        this.startY = startY;
        this.startX = screenX;
        x = startX;
        y = startY;
        isVisible = true;
    }

    public void update(float scrollSpeed, float fps){
        if (fps>0) {
            x = x-scrollSpeed/fps;
            rect.top = y;
            rect.bottom = y+height;
            rect.left = x;
            rect.right = x+width;

            if(rect.right < 0) {
                isVisible = false;
            }
        }
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
}
