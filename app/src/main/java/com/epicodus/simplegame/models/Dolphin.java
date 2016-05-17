package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.RectF;

/**
 * Created by Guest on 5/17/16.
 */
public class Dolphin {
    private float x, y, screenX, screenY, width, height, dolphinSpeed, startX, startY;
    private RectF rect;
    public boolean isVisible;


    public Dolphin(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX/7;
        this.height = screenY/7;
        this.rect = new RectF();
        this.dolphinSpeed = screenY/10;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void generate(float startY) {
        this.startY = startY;
        this.startX = screenX;
        x = startX;
        y = startY;
        isVisible = true;
    }

    public void update(long fps, float scrollSpeed) {
        if(fps > 0) {
            x = x - scrollSpeed/fps;
            x = x - dolphinSpeed/fps;
        }
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y+height;
        if(rect.right < 0) {
            isVisible = false;
        }
    }
}
