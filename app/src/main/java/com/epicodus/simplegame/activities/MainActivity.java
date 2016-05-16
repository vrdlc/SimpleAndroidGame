package com.epicodus.simplegame.activities;

import android.app.Activity;
import android.os.Bundle;

import com.epicodus.simplegame.R;
import com.epicodus.simplegame.views.GameView;

public class MainActivity extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);

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
