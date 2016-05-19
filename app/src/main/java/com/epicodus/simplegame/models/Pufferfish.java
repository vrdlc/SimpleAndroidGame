package com.epicodus.simplegame.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.epicodus.simplegame.R;

import java.util.Random;

/**
 * Created by Guest on 5/17/16.
 */
public class Pufferfish {
    private float x, y, screenX, screenY, width, height, pufferfishSpeed, startX, startY;
    private RectF rect;
    private RectF hitbox;
    public boolean isVisible;
    public boolean isDead;
    public boolean spikeThrown;
    public final int FALL_SPEED = 250;
    public Harpoon killHarpoon;
    private int frameCount;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLength;
    private Rect frameToDraw;
    private Random randomNumberGenerator;

    private Bitmap bitmap;


    public Pufferfish(Context context, float screenX, float screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX/10;
        this.height = 3*screenY/8;
        this.rect = new RectF();
        this.pufferfishSpeed = screenY/12;
        this.hitbox = new RectF();
        frameCount = 2;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLength = 700;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pufferfish);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width*frameCount, (int) height, false);
        frameToDraw = new Rect(0, 0, (int) width, (int) height);
        randomNumberGenerator = new Random();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RectF getHitbox() {
        return hitbox;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean takeAim(float playerY, float playerHeight) {

        int randomNumber = -1;

        if(!spikeThrown) {
            if((playerY + playerHeight > y && playerY + playerHeight < y + height) || (playerY > y && playerY < y + height)) {
                randomNumber = randomNumberGenerator.nextInt(150);
            } else {
                randomNumber = randomNumberGenerator.nextInt(350);
            }
            if(randomNumber == 0) {
                spikeThrown = true;
                return true;
            }
        }

        return false;

    }

    public void generate(float startY) {
        this.startY = startY;
        this.startX = screenX;
        x = startX;
        y = startY;

        if (y < screenY/5) {
            y = screenY/5;
        }
        if (y + height > screenY) {
            y = screenY - height;
        }

        isVisible = true;
        isDead = false;
        killHarpoon = null;
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

    public void update(long fps, float scrollSpeed) {
        if(!isDead) {
            if(fps > 0) {
                x = x - pufferfishSpeed/fps;
            }
        } else {
            if (y < screenY-height) {
                y = y + FALL_SPEED/fps;
            }
        }
        x = x - scrollSpeed/fps;
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y+height;

        if(rect.right < 0) {
            isVisible = false;
            killHarpoon = null;
            isDead = false;
        }

        hitbox.left = x + width/20;
        hitbox.right = x + width;
        hitbox.top = y + height/8;
        hitbox.bottom = y + height;
    }



    public float getHeight() {
        return height;
    }
}
