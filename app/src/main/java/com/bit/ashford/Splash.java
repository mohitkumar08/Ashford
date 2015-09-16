package com.bit.ashford;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;


public class Splash extends Activity {

	final int timeSlot = 3000;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Crashlytics crashlyticsKit = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
				.build();
		Fabric.with(this, crashlyticsKit);

		try {
			final Thread welcomeThread = new Thread() {

				int wait = 0;

				@Override
				public void run() {
					try {
						while ( wait < timeSlot ) {
							sleep(500);
							wait += 500;
						}

					} catch ( Exception e ) {

					} finally {
						Intent intent = new Intent(Splash.this,MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);

						finish();

					}
				}
			};
			welcomeThread.start();


		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}
