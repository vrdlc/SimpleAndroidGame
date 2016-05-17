package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Guest on 5/16/16.
 */
public class Harpoon {
    private float x, y, screenX, screenY, width, height, harpoonSpeed, startX, endX;
    private RectF rect;
    public boolean isShot;
    public boolean isVisible;
    public boolean isAngled;
    public boolean isAHit;
    public final int FALL_SPEED = 250;
    public Dolphin deadDolphin;


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
        this.startX = startX;
        endX = (float) (startX+screenX*0.75);
        x = startX;
        y = startY;
        isShot = true;
        isAHit = false;
        isVisible = true;
    }

    public boolean isActive(){
        if(x < screenX){
            return true;
        } else {
            return false;
        }
    }

    public void update(long fps, float scrollSpeed){

        if(!isAHit) {

            if (isShot) {

                if(x < endX) {

                    x = x + harpoonSpeed/fps;
                } else {
                    isAngled = true;
                }
                if(isAngled) {
                    if(y+height < screenY-20) {
                        x = x + harpoonSpeed/fps;
                        y = y + harpoonSpeed/fps;
                    } else {
                        isShot = false;
                    }
                }
            }
        } else {
            if (y < screenY-height && deadDolphin.getY()+deadDolphin.getHeight() < screenY) {
                y = y + FALL_SPEED/fps;
            }
        }

        x = x-scrollSpeed/fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y+height;
    }
}
