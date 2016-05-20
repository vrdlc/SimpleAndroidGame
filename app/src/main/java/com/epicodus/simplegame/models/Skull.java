package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import com.epicodus.simplegame.R;


public class Skull {
    private float x, y, screenX, screenY, width, height, startX, startY;
    private RectF rect;
    Bitmap bitmap;
    Context context;
    private int frameCount, currentFrame, frameLength;
    private long lastFrameChangeTime;
    private Rect frameToDraw;

    public Skull(float screenX, float screenY, Context context){
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX/2;
        y = screenY/3+screenY/20;
        width = screenX/13;
        height = screenY/9;
        rect = new RectF();
        this.context = context;
        frameCount = 2;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 1000;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.death);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update(){
        rect.top = y;
        rect.bottom = y+height;
        rect.left = x;
        rect.right = x+width;
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
