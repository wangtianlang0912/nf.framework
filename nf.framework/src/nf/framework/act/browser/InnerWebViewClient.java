package nf.framework.act.browser;

import nf.framework.core.LoadSysSoft;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InnerWebViewClient extends WebViewClient {

	private Context context;

	/**
	 * 
	 */
	public InnerWebViewClient(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url != null && (url.endsWith(".mp3") || url.endsWith(".mp4"))) {
			new LoadSysSoft().OpenVideo(context, url);
		} else {
			view.loadUrl(url);
		}
		return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
	}
}
