package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Guest on 5/16/16.
 */
public class Harpoon {
    private float x, y, screenX, screenY, width, height, harpoonSpeed, startX;
    private RectF rect;
    public boolean isShot;

    public Harpoon(Context context, float screenX, float screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX/15;
        this.height = screenY/15;
        this.rect = new RectF();
        harpoonSpeed = 500;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth(){
        return width;
    }

    public float getStartX(){
        return startX;
    }

    public RectF getRect(){
        return rect;
    }

    public void shoot(float startX, float startY){
        isShot = true;
        this.startX = startX;
        x = startX;
        y = startY;
    }

    public boolean isActive(){
        if(x < screenX){
            return true;
        } else {
            return false;
        }
    }

    public void update(long fps){
        x = x + harpoonSpeed/fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y+height;
    }
}
