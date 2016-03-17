package com.example.android.bustracker_acg.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;

/**
 * Created by giorgos on 3/11/2016.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private SecretTextView secretTextView;

    private long ms=0;
    private long splashTime=3000;

    private boolean splashActive = true;
    private boolean paused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        secretTextView = (SecretTextView)findViewById(R.id.secret_text_view);
        secretTextView.setDuration(3000);
        secretTextView.setIsVisible(false);
        secretTextView.toggle();

        Thread mythread = new Thread() {
            public void run() {
                try {
                    while (splashActive && ms < splashTime) {
                        if(!paused)
                            ms=ms+100;
                        sleep(100);
                    }
                } catch(Exception e) {}
                finally {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        mythread.start();
    }

}
