package com.inkneko.nekorecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.inkneko.nekorecord.data.RecordRepository;

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

        SharedPreferences sp = getSharedPreferences("nekoRecordStatistic", MODE_PRIVATE);

        if (sp.getBoolean("isFirstRun", true)){
            //TODO: 初始化类别数据
            Toast.makeText(this, "第一次运行", Toast.LENGTH_SHORT).show();
            RecordRepository repository = new RecordRepository(getApplication());
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    repository.initializeLocalStroage();
                }
            });
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

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
