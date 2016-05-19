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
public class Harpoon {
    private float x, y, screenX, screenY, width, height, harpoonSpeed, startX, endX;
    private Bitmap bitmap;
    private RectF rect;
    private RectF hitbox;
    public boolean isShot;
    public boolean isVisible;
    public boolean isAngled;
    public boolean isAHit;
    public final int FALL_SPEED = 250;
    public Dolphin deadDolphin;
    public Shark deadShark;
    public Swordfish deadSwordfish;
    public Pufferfish deadPufferfish;
    public Player player;


    public Harpoon(Context context, float screenX, float screenY, Player player) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.width = screenX / 13;
        this.height = screenY / 58;
        this.rect = new RectF();
        this.player = player;
        this.hitbox = new RectF();
        harpoonSpeed = (int) Math.floor(500 + (20*player.getSpeedUpgradeLevel()));
        isVisible = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.harpoon);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);
        endX = (float) (startX + screenX * 0.75);
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
        endX = (float) (startX + screenX * 0.75);
        x = startX;
        y = startY;
        isShot = true;
        isAHit = false;
        isVisible = true;
        deadDolphin = null;
        deadShark = null;
        deadSwordfish = null;
        deadPufferfish = null;
    }

    public boolean isActive() {
        if (x < screenX) {
            return true;
        } else {
            return false;
        }
    }

    public void update(long fps, float scrollSpeed) {

        if (!isAHit) {

            if (isShot) {

                if (x < endX) {

                    x = x + harpoonSpeed / fps;
                } else {
                    isAngled = true;
                }
                if (isAngled) {
                    if (y + height < screenY - 20) {
                        x = x + harpoonSpeed / fps;
                        y = y + harpoonSpeed / fps;
                    } else {
                        isShot = false;
                    }
                }
            }
        } else {
            if (deadDolphin != null) {
                if (y < screenY - height && deadDolphin.getY() + deadDolphin.getHeight() < screenY) {
                    y = y + FALL_SPEED / fps;
                }
            } else if (deadShark != null) {
                if (deadShark.getLife() > 0) {
                    x = x - deadShark.getSharkSpeed() / fps;
                } else {
                    if (y < screenY - height && deadShark.getY() + deadShark.getHeight() < screenY) {
                        y = y + FALL_SPEED / fps;
                    }
                }

            } else if (deadSwordfish != null) {
                if (y < screenY - height && deadSwordfish.getY() + deadSwordfish.getHeight() < screenY) {
                    y = y + FALL_SPEED / fps;
                }

            } else {
                if (y < screenY - height && deadPufferfish.getY() + deadPufferfish.getHeight() < screenY) {
                    y = y + FALL_SPEED / fps;
                }
            }
        }
            x = x - scrollSpeed / fps;
            rect.left = x;
            rect.right = x + width;
            rect.top = y;
            rect.bottom = y + height;

            hitbox.left = x;
            hitbox.right = x + width - width/8;
            hitbox.top = y;
            hitbox.bottom = y + height;
    }
}
