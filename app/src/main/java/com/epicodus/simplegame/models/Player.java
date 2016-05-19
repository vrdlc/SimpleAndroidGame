package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;

/**
 * Created by Guest on 5/16/16.
 */
public class Player {
    private int oxygenLevel;
    private int oxygenInterval;
    private int frameCount;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLength;
    private long lowerOxygen;
    private Rect frameToDraw;

    private float x;
    private float y;

    private float width;
    private float height;
    private float playerSpeedModifier;

    public float screenX;
    public float screenY;

    private RectF rect;
    private Bitmap bitmap;

    float xVel;
    float yVel;

    int speedUpgradeLevel;

    public Player(Context context, float screenX, float screenY, int speedUpgradeLevel, int oxygenUpgradeLevel, int lungsUpgrade) {

        oxygenLevel = 2+oxygenUpgradeLevel;
        x = (float) (screenX*0.2);
        y = screenY/5;
        width = screenX/5;
        height = screenY/9;
        xVel = 0;
        yVel = 0;
        rect = new RectF();
        this.screenX = screenX;
        this.screenY = screenY;
        frameCount = 3;
        currentFrame = 0;
        lowerOxygen = System.currentTimeMillis();
        lastFrameChangeTime = 0;
        frameLength = 700;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scuba);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
        this.speedUpgradeLevel = speedUpgradeLevel;
        playerSpeedModifier = (float) (0.3 + 0.1*speedUpgradeLevel);
        oxygenInterval = 5000 + 1000*lungsUpgrade;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void isPlayerDead(boolean isDead){
        if(isDead){
            x = screenX/2-screenX/6;
            y = screenY/2;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public void setFrameLength(int length) {
        frameLength = length;
    }

    public RectF getRect() {
        return rect;
    }

    public int getOxygenLevel(){
        return oxygenLevel;
    }

    public void setOxygenLevel(){
        oxygenLevel++;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getPlayerSpeedModifier() {
        return playerSpeedModifier;
    }

    public void setPlayerSpeedModifier(float playerSpeedModifier) {
        this.playerSpeedModifier = playerSpeedModifier;
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

    public void update(long fps, float circleXPosition, float circleYPosition, float circleDefaultX, float circleDefaultY, float scrollSpeed) {
        xVel = playerSpeedModifier * (circleXPosition - circleDefaultX) * 10;
        yVel = playerSpeedModifier * (circleYPosition - circleDefaultY) * 10;
        if(fps > 0) {
            x = x + xVel/fps;
            y = y + yVel/fps;
            x = x-scrollSpeed/fps;
        } else {
            x = screenX/2-screenX/6;
            y = screenY/2;

        }

        if (y + height > screenY) {
            y = screenY - height;
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenX) {
            x = screenX - width;
        }
        if (y < screenY/20) {
            y = screenY/20;
        }

        long oxygenTimer = System.currentTimeMillis();
        if(oxygenTimer > oxygenInterval + lowerOxygen){
            lowerOxygen = oxygenTimer;
            oxygenLevel--;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width ;
    }
}



/*TODO:
    Collision detection -- player-enemy/harpoon-enemy/
    Points/Fish
    Surfacing
    fix bug where harpoon flashes at last location
 */
