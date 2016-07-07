package com.jay.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jay.locationselect.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, WeatherActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1500);
	}

}
