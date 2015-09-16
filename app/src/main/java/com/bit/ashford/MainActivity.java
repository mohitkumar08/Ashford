package com.bit.ashford;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.trncic.library.DottedProgressBar;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity {

	WebView myWebView;
	DottedProgressBar progressDotted;
	public static UrlCache urlCache_save = null;
	public static Boolean flag = false;
	public static Boolean second_page = false;
	public static Bundle bundle_instance;
	AlertDialog.Builder alertDialog;
	AlertDialog customBuilder;
	public static final String URL = "http://zh.ashford.com/";
	ActionBar actionBar;
	public static Boolean firsttimne = true;
	public static String UPDATE_URL = "http://zh.ashford.com/";


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		try {
			Fabric.with(this, new Crashlytics());
			setContentView(R.layout.activity_main);
			bundle_instance = savedInstanceState;
			myWebView = ( WebView ) findViewById(R.id.webView);
			myWebView.setWebViewClient(new MyWebViewClient(MainActivity.this));

			progressDotted = ( DottedProgressBar ) findViewById(R.id.progress);
			actionBar = getSupportActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);
			//actionBar.setHomeAsUpIndicator(R.drawable.ic_list_white_24dp);
			//	actionBar.setHomeAsUpIndicator(R.drawable.ic_list_white_36dp);
			setWebSetting();
			//
			cache_setting();
			if ( ! isNetworkAvailable() ) {
				showInternetSetting();
			} else {
				myWebView.setWebChromeClient(new WebChromeClient() {
					@Override
					public void onProgressChanged( WebView view, int progress ) {
						//		Log.e("progresss", String.valueOf(progress));
						if ( progress == 100 ) {
							//	Log.e("update url ", UPDATE_URL);
							hide_visible_view();

						}
						if ( progress >= 70 ) {
							if ( UPDATE_URL.equalsIgnoreCase(URL) ) {
								actionBar.setDisplayHomeAsUpEnabled(false);
							} else {
								actionBar.setDisplayHomeAsUpEnabled(true);
								actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
							}
						}
					}
				});
				myWebView.loadUrl(URL);
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		// Check if the key event was the Back button and if there's history
		try {
			if ( (keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack() ) {
				myWebView.goBack();
				return true;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return super.onKeyDown(keyCode, event);
	}

	//This is class handle all  internet surfing or say handle webview navigation  and page load

	private class MyWebViewClient extends WebViewClient {
		private UrlCache urlCache = null;

		public MyWebViewClient( Activity activity ) {

			this.urlCache = new UrlCache(activity);
			urlCache_save = new UrlCache(activity);
			this.urlCache.register(URL, "zh.ashford.com",
					"text/html", "UTF-8", 600 * UrlCache.ONE_SECOND);
			urlCache_save.register(URL, "zh.ashford.com",
					"text/html", "UTF-8", 600 * UrlCache.ONE_MINUTE);

		}

		@Override
		public boolean shouldOverrideUrlLoading( WebView view, String url ) {
			//	Log.e("shouldOverrideUrlLoading  ", url);
			if ( ! isNetworkAvailable() ) {
				showInternetSetting();
			} else {
				view.loadUrl(url);
				show_visible_view();
			}
			return true;
		}

		@Override
		public void onPageFinished( WebView view, String url ) {
			//	Log.e("onPageFinished  ", url);
			if ( ! isNetworkAvailable() ) {
				showInternetSetting();
			} else {
				if ( second_page ) {
					//actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_48dp);
					flag = true;
				}
				second_page = true;
				UPDATE_URL = url;
				firsttimne = false;
				view.clearCache(true);

				super.onPageFinished(view, url);

			}

		}

		@Override
		public void onPageStarted( WebView view, String url, Bitmap favicon ) {
			try {
				//		Log.e("onPageStarted  ", url);


				if ( ! isNetworkAvailable() ) {
					showInternetSetting();
				} else {
					show_visible_view();
					UPDATE_URL = url;
					super.onPageStarted(view, url, favicon);

					//	show_visible_view();
				}
				if ( ! (String.valueOf(urlCache_save).equals("null")) ) {
					//	flag = true;
				}
				/*if ( firsttimne ) {
					progressDotted.setVisibility(View.GONE);
					//hide_visible_view();
				}*/
			} catch ( Exception e ) {
				e.printStackTrace();
			}

		}

		//This method call when webview give error on load pa
		@Override
		public void onReceivedError( WebView view, int errorCode,
									 String description, String failingUrl ) {
				Log.e("onReceivedError  ", failingUrl);
			/*if ( errorCode == - 2 ) {
				urlCache_save.load(URL);
				hide_visible_view();
				if ( ! isNetworkAvailable() ) {
					showInternetSetting();
				}
			} else {*/
			myWebView.setVisibility(View.INVISIBLE);
			super.onReceivedError(view, errorCode, description, failingUrl);


//page could not be loaded
		}

		@Override
		public WebResourceResponse shouldInterceptRequest( WebView view, String url ) {
			try {
				//	Log.e("shouldInterceptRequest  ", url);
				if ( ! isNetworkAvailable() ) {
					myWebView.goBack();
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			return this.urlCache.load(url);
		}
	}

	/*@Override
// Detect when the back button is pressed
	public void onBackPressed() {
		if ( myWebView.canGoBack() ) {
			myWebView.goBack();
		}
		super.onBackPressed();
	}*/


	@Override
	public void onResume() {
		//	Log.e("on resume   ", "resume");

		if ( ! isNetworkAvailable() ) {
			showInternetSetting();
		}
		super.onResume();
	}

	protected void showInternetSetting() {
		try {
			myWebView.setBackgroundColor(Color.WHITE);
			alertDialog = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Dialog_Alert);
			alertDialog.setCancelable(false);
			alertDialog.setTitle("Internet Not Available.");
			alertDialog.setMessage("Ashford requires internet, please enable internet.");
			/*alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

				@Override
				public void onClick( final DialogInterface dialog, final int which ) {
					customBuilder.dismiss();
					dialog.dismiss();
					startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);

				}
			});*/
			alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				/**
				 * dialog
				 */
				@Override
				public void onClick( final DialogInterface dialog, final int which ) {
					customBuilder.dismiss();
					dialog.dismiss();
					finish();

				}
			});

			/*if ( flag ) {
				alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( final DialogInterface dialog, final int which ) {
						urlCache_save.load(URL);
						dialog.dismiss();

					}
				});
			}*/
			customBuilder = alertDialog.create();
			customBuilder.show();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
/*

	@SuppressLint ( "NewApi" )
	protected void showInternetSetting1() {
		 alertDialog = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Dialog_Alert);
		alertDialog.setCancelable(true);
		alertDialog.setTitle("Internet Not Available");
		alertDialog.setMessage("Ashford requires Internet services .Enable Internet services ");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			*/
/**
 * dialog
 *//*

			@Override
			public void onClick( final DialogInterface dialog, final int which ) {
				try {
					customBuilder.dismiss();
					final Intent intent = new Intent(Settings.ACTION_SETTINGS);
					startActivity(intent);

				} catch ( Exception e ) {
					e.printStackTrace();

				}
			}
		});
		alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
			*/
/**
 * dialog
 *//*

			@Override
			public void onClick( final DialogInterface dialog, final int which ) {
				try {
					customBuilder.dismiss();
					finish();
				} catch ( Exception e ) {
					e.printStackTrace();

				}
			}
		});
		*/
/*alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			*//*
*/

	/**
	 * dialog
	 *//*
*/
/*
			@Override
			public void onClick( final DialogInterface dialog, final int which ) {
				try {
					dialog.dismiss();
				} catch ( Exception e ) {
					e.printStackTrace();

				}
			}
		});*//*

		AlertDialog customBuilder = alertDialog.create();
		//customBuilder
		customBuilder.show();
		alertDialog.show();
	}

*/

	/*@Override
	protected void onStart() {
		if ( ! isNetworkAvailable() ) {
			showInternetSetting1();
		}
		Log.e("start   ", "start");
		super.onStart();
	}
*/
	@Override
	protected void onRestart() {
		//Log.e("restart   ", "restart");
		super.onRestart();
		if ( isNetworkAvailable() ) {

			try {
				if ( bundle_instance != null ) {
					MainActivity.this.onCreate(bundle_instance);
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}

		}
		cache_setting();

	}


	@Override
	protected void onPause() {
		//	Log.e("pause   ", "pause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		//	Log.e("stop   ", "stop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		//	Log.e("destroy   ", "destory");
		firsttimne = true;
		super.onDestroy();
		myWebView.clearCache(true);
		myWebView.clearHistory();
	}


	public void setWebSetting() {
		myWebView.getSettings().setSupportZoom(true);         //Zoom Control on web (You don't need this
		myWebView.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setSupportMultipleWindows(false);
		myWebView.getSettings().setDomStorageEnabled(true);
		myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);//tbd
		myWebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
		//myWebView.getSettings().setAppCacheMaxSize(1); // 5MB
		//myWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setAppCacheEnabled(true);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.getSettings().setUseWideViewPort(true);
		//myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // dbd
		//	myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

	}

	/*else { // loading offline
		Log.e("neetwork not available", "na hai internet");
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		progressDotted.setVisibility(View.GONE);
		progressDotted.stopProgress();
		imageview.setVisibility(View.GONE);
		myWebView.loadUrl(url);
	}
	*/
	public void show_visible_view() {
		progressDotted.startProgress();
		progressDotted.setVisibility(View.VISIBLE);

	}

	public void hide_visible_view() {
		progressDotted.stopProgress();
		progressDotted.setVisibility(View.GONE);


	}

	public void cache_setting() {
		if ( isNetworkAvailable() ) {
			myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // dbd
			//myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // dbd
		} else {
			myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // dbd


		}
	}

	@Override
	public void onSaveInstanceState( Bundle savedInstanceState ) {
		super.onSaveInstanceState(savedInstanceState);
		this.bundle_instance = savedInstanceState;
	}

	@Override
	public void onRestoreInstanceState( Bundle savedInstanceState ) {
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch ( item.getItemId() ) {
			case android.R.id.home:
				//	Toast.makeText(getApplicationContext(),"click me",Toast.LENGTH_LONG).show();
				if ( myWebView.canGoBack() ) {
					//		actionBar.setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_48dp);
					myWebView.goBack();

				} else {
					//		actionBar.setHomeAsUpIndicator(R.drawable.ic_list_white_36dp);
				}

				break;
			/*case R.id.goBack:
				if ( myWebView.canGoBack() ) {
					myWebView.goBack();
				}
				return true;


			case R.id.goForward:
				if ( myWebView.canGoForward() ) {
					myWebView.goForward();
				}
				return true;*/
			case R.id.exit:
				finish();
				return true;
			case R.id.refresh:
				if ( isNetworkAvailable() ) {
					myWebView.loadUrl(UPDATE_URL);
				}
				return true;


		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent intent ) {
		if ( requestCode == 0 ) {
			Intent intent1 = getIntent();
			finish();
			startActivity(intent1);/*
			WifiManager wifiManager = (WifiManager)
					getSystemService(Context.WIFI_SERVICE);
			if(!wifiManager.isWifiEnabled())
			{

			}*/
			//restart Application here
		}
	}
}
