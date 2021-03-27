package com.inkneko.nekorecord;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.inkneko.nekorecord.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeScreenActivity extends AppCompatActivity {
    private Handler mHandler;

    private long delay = 1500;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome_screen);

        Timer timer = new Timer();
        timer.schedule(task, delay);
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            Intent in = new Intent().setClass(WelcomeScreenActivity.this,
                    MainActivity.class).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            finish();

        }
    };
}
