package com.pinmyballs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.pinmyballs.metier.Tournoi;

public class PageInfoTournoi extends AppCompatActivity {

	ProgressBar progressBar;
	WebView web;
	ActionBar mActionbar;


	public class myWebClient extends WebViewClient
	{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.startsWith("http://")) {
				view.getContext().startActivity(
						new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				return true;
			}else if (url.startsWith("mailto:") || url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}
	}

	Tournoi tournoi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// On récupère le tournoi concerné
		Intent i = getIntent();
		tournoi = (Tournoi) i.getSerializableExtra(PageListeResultatTournois.INTENT_TOURNOI_POUR_INFO);

		setContentView(R.layout.activity_webview_tournoi);

		mActionbar = getSupportActionBar();
		mActionbar.setTitle(R.string.headerTournoi);
		mActionbar.setIcon(R.drawable.header_icon_tournoi);

		web = (WebView) findViewById(R.id.webViewInfoTournoi);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

		web.setWebViewClient(new myWebClient());
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl(tournoi.getUrl());
	}

	// To handle "Back" key press event for WebView to go back to previous screen.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (web != null && keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onStart() {
		super.onStart();
		// Google Analytics
		//EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Google Analytics
		//EasyTracker.getInstance().activityStop(this);
	}


}
