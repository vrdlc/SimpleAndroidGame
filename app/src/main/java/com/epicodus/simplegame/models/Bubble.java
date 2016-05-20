package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import com.epicodus.simplegame.R;


public class Bubble {
    private float x, y, screenX, screenY, width, height, startX, startY;
    private RectF rect;
    Bitmap bitmap;
    public boolean isVisible;
    Context context;
    private int frameCount, currentFrame, frameLength;
    private long lastFrameChangeTime;
    private Rect frameToDraw;
    private RectF hitbox;

    public Bubble(float screenX, float screenY, Context context){
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX;
        y = screenY/2;
        width = screenX/18;
        height = screenY/12;
        rect = new RectF();
        this.context = context;
        hitbox = new RectF();
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

    public void setX(float x) {
        this.x = x;
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
    public RectF getHitbox() { return hitbox; }

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

        if (y < screenY/5) {
            y = screenY/5;
        }
        if (y + height > screenY) {
            y = screenY - height;
        }
    }

    public void update(float scrollSpeed, float fps){
        if (fps>0) {
            x = x-scrollSpeed/fps;
            rect.top = y;
            rect.bottom = y+height;
            rect.left = x;
            rect.right = x+width;

            hitbox.top = y + height/5;
            hitbox.bottom = y + height - height/5;
            hitbox.left = x + width/5;
            hitbox.right = x + width-width/5;

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
