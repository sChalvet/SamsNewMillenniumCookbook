package com.gmail.samos6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

private static final int SPLASH_DISPLAY_TIME = 2000; /* 3 seconds */

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash_screen);
	
	    new Handler().postDelayed(new Runnable() {
	
	        public void run() {
	
	            Intent mainIntent = new Intent(SplashActivity.this, MainScreenActivity.class);
	            SplashActivity.this.startActivity(mainIntent);
	
	            SplashActivity.this.finish();
	            overridePendingTransition(0, R.anim.splashfadeout);
	        }
	    }, SPLASH_DISPLAY_TIME);
	}

}