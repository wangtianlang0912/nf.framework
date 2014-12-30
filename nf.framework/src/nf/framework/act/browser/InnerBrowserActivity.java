package nf.framework.act.browser;

import nf.framework.R;
import nf.framework.act.AbsBaseActivity;
import nf.framework.core.LoadSysSoft;
import nf.framework.core.util.android.CloseActivityClass;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class InnerBrowserActivity extends AbsBaseActivity {
	private Context mcontext;
	private WebView webview;
	private Intent homeIntent;
	private String urlAddress;
	private ImageView gobackBtn;
	private ImageView goforwardBtn;
	private ImageView refreshBtn;
	private ImageView browserBtn;
	private RelativeLayout refeshProgressbar;
	private String intentSource;
	public static final String INTENT_TITLE = "param_title";
	public static final String INTENT_URL = "url";
	public static final String INTENT_SOURCE = "intentSource";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mcontext = this;
		CloseActivityClass.activityList.add(this);
		initView();
	}

	private void initView() {
		View view = LayoutInflater.from(mcontext).inflate(R.layout.common_web_browser_main, super.mainlayout,false);
		super.mainlayout.addView(view);
		super.leftButton.setVisibility(View.VISIBLE);
		super.leftButton.setImageResource(R.drawable.common_navigate_back_btn);
		refeshProgressbar = (RelativeLayout) this
				.findViewById(R.id.common_web_main_refesh_progressbar_layout);
		gobackBtn = (ImageView) this
				.findViewById(R.id.common_web_toolbar_goback_btn);
		goforwardBtn = (ImageView) this
				.findViewById(R.id.common_web_toolbar_goforward_btn);
		refreshBtn = (ImageView) this
				.findViewById(R.id.common_web_toolbar_refresh_btn);
		browserBtn = (ImageView) this
				.findViewById(R.id.common_web_toolbar_browser_btn);
		webview = (WebView) this.findViewById(R.id.common_web_main_web_context);
		webview.setWebViewClient(new InnerWebViewClient(InnerBrowserActivity.this));
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVerticalScrollBarEnabled(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
		webview.getSettings().setBuiltInZoomControls(true); 
		webview.requestFocus();
		webview.setWebChromeClient(new mWebChromeClient()); 

		webview.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			// @JavascriptInterface
			public void getVideoUrl(String videoUrl) {
				if (videoUrl != null) {
					new LoadSysSoft().OpenVideo(mcontext, videoUrl);
				}
			}

			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getPicUrl(String url) {
				if (url != null) {
					getShowPic(url);
				}
			}

			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getDownLoadUrl(String url) {
				if (url != null) {
					new LoadSysSoft().OpenBrowser(mcontext, url);
				}
			}

			@SuppressWarnings("unused")
			@JavascriptInterface
			public void OpenMarketApp(String appPackageName) {
				if (appPackageName != null) {
					new LoadSysSoft().OpenMarketApp(mcontext, appPackageName);
				}
			}
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void onFinish(){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						onBackPressed();
					}
				});
				
			}
		}, "JsCallBack");
		gobackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webview.canGoBack()) {
					webview.goBack();
				}
			}
		});
		goforwardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webview.canGoForward()) {
					webview.goForward();
				}
			}
		});
		super.leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FinishActivity();
			}
		});
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.loadUrl(urlAddress);
			}
		});
		browserBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String urlAddress = getIntent().getStringExtra(INTENT_URL);
				if (!TextUtils.isEmpty(urlAddress)) {
					new LoadSysSoft().OpenBrowser(mcontext, urlAddress);
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		webview.onResume();
		homeIntent = this.getIntent();
		String titleName = homeIntent.getStringExtra(INTENT_TITLE);
		if (!TextUtils.isEmpty(titleName)) {
			super.top_textview.setText(titleName);
		}
		urlAddress = homeIntent.getStringExtra(INTENT_URL);
		webview.loadUrl(urlAddress);
	}

	public void setToolbarState(boolean clickable, ImageView imageView) {
		imageView.setClickable(clickable);
		if (imageView.equals(gobackBtn)) {
			imageView
					.setImageResource(clickable ? R.drawable.common_web_toolbar_goback_btn_clickable
							: R.drawable.common_web_toolbar_goback_btn_normal);
		} else if (imageView.equals(goforwardBtn)) {
			imageView
					.setImageResource(clickable ? R.drawable.common_web_toolbar_gofoward_btn_clickable
							: R.drawable.common_web_toolbar_goforward_btn_normal);
		}

	}

	protected void onPause() {
		super.onPause();
		webview.onPause();
		webview.pauseTimers();  
		webview.stopLoading();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		webview.clearCache(true);
		webview.clearHistory();
		FinishActivity();
	}

	private void getShowPic(String str) {
		if (str != null) {
			
		} else {
		}
	}
	private void FinishActivity() {

		if (intentSource != null) {
			new LoadSysSoft().openAPP(mcontext, mcontext.getPackageName());
		} else {
			setResult(RESULT_OK, homeIntent);
		}
		finish();
//		overridePendingTransition(R.anim.common_slide_up_in,
//				R.anim.common_slide_down_out);
		overridePendingTransition(R.anim.common_push_right_in,
				R.anim.common_push_right_out);
	}
	
	
	private class mWebChromeClient extends InnerWebChromeClient{
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {

			if (newProgress == 100) {
				refeshProgressbar.setVisibility(View.INVISIBLE);
				setToolbarState(true, gobackBtn);
				setToolbarState(view.canGoBack(), gobackBtn);
				setToolbarState(view.canGoForward(), goforwardBtn);
				webview.requestFocus();
			} else {
				refeshProgressbar.setVisibility(View.VISIBLE);
				setToolbarState(false, gobackBtn);
				setToolbarState(false, goforwardBtn);
			}

		}
	}
}
