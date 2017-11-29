package co.edu.unal.bookswapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logoApp;
    private TextView nameApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logoApp = (ImageView) findViewById(R.id.logoApp);
        nameApp = (TextView) findViewById(R.id.nameApp);
        Animation splashScreenTransition = AnimationUtils.loadAnimation(this, R.anim.splash_screen_transition);
        logoApp.startAnimation(splashScreenTransition);
        nameApp.startAnimation(splashScreenTransition);
        final Intent intent = new Intent(this, LoginActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e ) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
