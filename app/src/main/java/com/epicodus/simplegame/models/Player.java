package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Guest on 5/16/16.
 */
public class Player {

    private float x;
    private float y;

    private float width;
    private float height;

    public float screenX;
    public float screenY;

    private RectF rect;

    float xVel;
    float yVel;

    public Player(Context context, float screenX, float screenY) {
        x = (float) (screenX*0.8);
        y = screenY/5;
        width = screenX/12;
        height = screenY/4;
        xVel = 0;
        yVel = 0;
        rect = new RectF();
        this.screenX = screenX;
        this.screenY = screenY;
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

    public void update(long fps, float circleXPosition, float circleYPosition, float circleDefaultX, float circleDefaultY) {
        xVel = (circleXPosition - circleDefaultX) * 10;
        yVel = (circleYPosition - circleDefaultY) * 10;
        if(fps > 0) {
            x = x + xVel/fps;
            y = y + yVel/fps;
        }

        if (y < 0) {
            y = 0;
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenX) {
            x = screenX - width;
        }
        if (y + height > screenY) {
            y = screenY - height;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }
}
