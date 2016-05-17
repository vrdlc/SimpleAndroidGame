/*
    todo:
    randomly generate seaweed, bubbles, etc.
    add start screen and game over screen
    include points
    surface for air
    treasure chests
*/

package com.epicodus.simplegame.activities;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
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
