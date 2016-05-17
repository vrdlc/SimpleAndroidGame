package com.epicodus.simplegame.models;

import android.graphics.Bitmap;
import android.graphics.RectF;

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

    RectF rect;
    Bitmap bitMap;

    public Seaweed(float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        x = screenX;
        y = screenY - screenY/10;
        width = screenX/20;
        height = screenY/10;
        rect = new RectF();
    }

    public float getX() {
        return x;
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
}
