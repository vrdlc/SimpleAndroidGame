package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;

/**
 * Created by Guest on 5/16/16.
 */
public class Harpoon {
    private float x, y, screenX, screenY, width, height, harpoonSpeed, startX, endX;
    private Bitmap bitmap;
    private RectF rect;
    public boolean isShot;
    public boolean isVisible;
    public boolean isAngled;

    public Harpoon(Context context, float screenX, float screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX/15;
        this.height = screenY/15;
        this.rect = new RectF();
        harpoonSpeed = 500;
        isVisible = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.harpoon);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);
        endX = (float) (startX+screenX*0.75);
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
        isVisible = true;
        this.startX = startX;
        endX = (float) (startX+screenX*0.75);
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

    public void update(long fps, float scrollSpeed){

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
        x = x-scrollSpeed/fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y+height;
    }
}
