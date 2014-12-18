package nf.framework.act.browser;

import nf.framework.R;
import nf.framework.act.AbsBaseActivity;
import nf.framework.core.LoadSysSoft;
import nf.framework.core.util.android.CloseActivityClass;
import nf.framework.expand.dialog.AbsBaseDialog;
import nf.framework.expand.dialog.AbsBaseDialog.DialogUpBtnOnClickListener;
import nf.framework.expand.dialog.BaseDialog;
import nf.framework.expand.dialog.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class InnerBrowserByTitleActivity extends AbsBaseActivity {

	private Context mcontext;
	private WebView detailwebview;
	private Intent homeIntent;
	private ProgressDialog spd = null;

	public static final String INTENT_TITLE = "param_item";
	public static final String INTENT_URL = "URL";
	public static final String INTENT_ReferInfo = "ReferInfo";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mcontext = this;
		CloseActivityClass.activityList.add(this);

		initView();
		homeIntent = this.getIntent();
		String titleName = homeIntent.getStringExtra(INTENT_TITLE);
		String urlAddress = homeIntent.getStringExtra(INTENT_URL);
		super.top_textview.setText(titleName);
		spd = new ProgressDialog(mcontext);
		detailwebview.loadUrl(urlAddress);
	}

	private void initView() {
		// View mainView=
		// LayoutInflater.from(mcontext).inflate(R.layout.moredetail_main,null);
		super.leftButton.setVisibility(View.VISIBLE);
		super.leftButton.setImageResource(R.drawable.common_navigate_back_btn);
		// detailwebview = (WebView)
		// this.findViewById(R.id.moredetail_main_webview);
		detailwebview = new WebView(this);
		detailwebview.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		super.mainlayout.addView(detailwebview);
		detailwebview.getSettings().setJavaScriptEnabled(true);
		detailwebview.canGoBack();
		detailwebview.setVerticalScrollBarEnabled(true);
		detailwebview.requestFocus();
		detailwebview.setWebViewClient(new InnerWebViewClient(
				InnerBrowserByTitleActivity.this));

		detailwebview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress == 100) {  
					spd.dismiss();
				} else {
					if (!spd.isShowing())
						spd.show();
				}
			}
		});
		detailwebview.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getUrl(String param, String url) {
				if (url != null) {

				}
			}

			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getVideoUrl(String videoUrl) {
				if (videoUrl != null) {
					new LoadSysSoft().OpenVideo(mcontext, videoUrl);
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
			public void getSuccessFeedBack(String message) {
				BaseDialog baseDialog = new BaseDialog(mcontext,
						AbsBaseDialog.DIALOG_BUTTON_STYLE_ONE);
				baseDialog.show();
				baseDialog.setTitle("提示");
				baseDialog.setContent(message);
				baseDialog
						.setDialogUpBtnOnClickListener(new DialogUpBtnOnClickListener() {
							@Override
							public void onButtonClick(View upBtn) {
								// TODO Auto-generated method stub
								FinishActivity();
							}
						});
			}

			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getFailureFeedBack(String message) {
				BaseDialog baseDialog = new BaseDialog(mcontext,
						AbsBaseDialog.DIALOG_BUTTON_STYLE_ONE);
				baseDialog.show();
				baseDialog.setTitle("提示");
				baseDialog.setContent(message);
			}
		}, "JsCallBack");
		detailwebview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				detailwebview.requestFocus();
				return false;
			}
		});
		super.leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!detailwebview.canGoBack()) {
					FinishActivity();
				} else {
					detailwebview.goBack();
				}
			}
		});
	}

	protected void onResume() {

		super.onResume();
	}

	protected void onPause() {
		super.onPause();
		Log.d("MoreDetailActivity",
				"onPause=========================================");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!detailwebview.canGoBack()) {
				// detailwebview.clearHistory();
				// detailwebview.clearCache(true);
				FinishActivity();
			} else {
				detailwebview.goBack();  
			}
		}
		return false;
	}
	private void FinishActivity() {
		setResult(RESULT_OK, homeIntent);
		finish();
		overridePendingTransition(R.anim.common_push_right_in,
				R.anim.common_push_right_out);
	}
}
