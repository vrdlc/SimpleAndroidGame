package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;


public class Spear {
    private float x, y, screenX, screenY, width, height, spearSpeed, startX, endX;
    private Bitmap bitmap;
    private RectF rect;
    private RectF hitbox;
    public boolean isShot;
    public boolean isVisible;
    public Dolphin thrower;

    public Spear(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX / 13;
        this.height = screenY / 58;
        this.rect = new RectF();
        this.hitbox = new RectF();
        spearSpeed = screenX/3;
        isVisible = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spear);
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

    public RectF getHitbox() {
        return hitbox;
    }

    public void shoot(float startX, float startY) {
        this.startX = startX;
        x = startX;
        y = startY + 8*thrower.getHeight()/10;
        isShot = true;
        isVisible = true;
    }

    public void update(long fps, float scrollSpeed) {

        x = x - spearSpeed / fps;
        x = x - scrollSpeed / fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;

        hitbox.left = x + width/10;
        hitbox.right = x + width;
        hitbox.top = y + height/10;
        hitbox.bottom = y + 9*height/10;
    }
}
