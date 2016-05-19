package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import com.epicodus.simplegame.R;

/**
 * Created by Epicodus on 5/18/16.
 */
public class Boat {
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

    private RectF rect;
    private RectF hitbox;

    Bitmap bitMap;

    public Boat(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX-screenX/3;
        y = -screenY/7;
        width = screenX/4;
        height = screenY/4;
        rect = new RectF();
        hitbox = new RectF();
        this.context = context;
        frameCount = 2;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 1000;
        bitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boatlife);
        bitMap = Bitmap.createScaledBitmap(bitMap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;

        hitbox.top = y;
        hitbox.bottom = y + height-height/20;
        hitbox.left = x + width/10;
        hitbox.right = x + width - width/10;
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

    public Bitmap getBitmap() {
        return bitMap;
    }

    public RectF getHitbox() {
        return hitbox;
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
}
