package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;

/**
 * Created by Guest on 5/17/16.
 */
public class Seaweed {
    float x;
    float y;
    float screenX;
    float screenY;
    float width;
    float height;
    Context context;
    private int frameCount;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLength;
    private Rect frameToDraw;
    public boolean isVisible;

    RectF rect;
    Bitmap bitMap;

    public Seaweed(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX;
        y = (float) (screenY - screenY/3);
        width = screenX/12;
        height = screenY/3;
        rect = new RectF();
        this.context = context;
        frameCount = 4;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 1000;
        bitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.seaweed);
        bitMap = Bitmap.createScaledBitmap(bitMap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitMap() {
        return bitMap;
    }

    public void update(float scrollSpeed, float fps) {
        if (fps>0) {
            x = x-scrollSpeed/fps;
            rect.top = y;
            rect.bottom = y+height;
            rect.left = x;
            rect.right = x+width;
        }
    }

    public void generate() {
        isVisible = true;
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
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

    public void resetX() {
        x = screenX;
    }
}
