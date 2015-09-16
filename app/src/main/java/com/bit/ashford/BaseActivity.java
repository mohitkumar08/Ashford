package com.bit.ashford;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BaseActivity extends ActionBarActivity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = ( ConnectivityManager ) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
