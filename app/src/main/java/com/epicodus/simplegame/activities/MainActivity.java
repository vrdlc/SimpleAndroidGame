/*
    todo:

    PRIORITY
    enemy design
    incorporate timer based difficulty
    credits
    title and sounds
    turn off god mode


    POLISH

    make points better
    treasure chests
    ideas for upgrades:
      better weapons
      weapon does more damage
    move speed is slow at first, increases as you move
    Make dolphins not overlap
    scroll faster as player touches right side of screen
*/

package com.epicodus.simplegame.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.epicodus.simplegame.R;
import com.epicodus.simplegame.views.GameView;

public class MainActivity extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        gameView = new GameView(this, size.x, size.y);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}
