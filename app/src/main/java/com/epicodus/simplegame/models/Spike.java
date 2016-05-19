package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.epicodus.simplegame.R;

/**
 * Created by Guest on 5/19/16.
 */
public class Spike {
    private float x, y, screenX, screenY, width, height, spikeSpeed, startX, endX;
    private Bitmap bitmap;
    private RectF rect;
    public boolean isShot;
    public boolean isVisible;
    public final int FALL_SPEED = 250;
    public Pufferfish thrower;

    public Spike(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX / 13;
        this.height = screenY / 58;
        this.rect = new RectF();
        spikeSpeed = 500;
        isVisible = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getStartX() {
        return startX;
    }

    public RectF getRect() {
        return rect;
    }

    public void shoot(float startX, float startY) {
        this.startX = startX;
        x = startX;
        y = startY + 8*thrower.getHeight()/10;
        isShot = true;
        isVisible = true;

    }

    public void update(long fps, float scrollSpeed) {

        x = x - spikeSpeed / fps;
        x = x - scrollSpeed / fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
    }
}
