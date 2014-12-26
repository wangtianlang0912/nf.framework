package nf.framework.act.browser;

import nf.framework.R;
import nf.framework.act.AbsBaseActivity;
import nf.framework.act.NFIntentUtils;
import nf.framework.core.LoadSysSoft;
import nf.framework.core.util.android.CloseActivityClass;
import nf.framework.expand.dialog.AbsBaseDialog;
import nf.framework.expand.dialog.AbsBaseDialog.DialogUpBtnOnClickListener;
import nf.framework.expand.dialog.BaseDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class InnerBrowserByTitleActivity extends AbsBaseActivity {

	private Context mcontext;
	private WebView detailwebview;
	private Intent homeIntent;
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
		detailwebview.loadUrl(urlAddress);
	}

	private void initView() {
		super.leftButton.setVisibility(View.VISIBLE);
		super.leftButton.setImageResource(R.drawable.common_navigate_back_btn);
		View webviewLayout = LayoutInflater.from(this).inflate(R.layout.common_web_layout,super.mainlayout,false);
		super.mainlayout.addView(webviewLayout);
		detailwebview=(WebView) findViewById(R.id.common_web_main_web_view);
		detailwebview.getSettings().setJavaScriptEnabled(true);
		detailwebview.canGoBack();
		detailwebview.setVerticalScrollBarEnabled(true);
		detailwebview.requestFocus();
		detailwebview.setWebViewClient(new InnerWebViewClient(this));
		detailwebview.setWebChromeClient(new InnerWebChromeClient()); 
		detailwebview.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void getUrl(String param, String url) {
				if (url != null) {
					NFIntentUtils.intentToInnerBrowserAct(mcontext, "web","web", url);
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
