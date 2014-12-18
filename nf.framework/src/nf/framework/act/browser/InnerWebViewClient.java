package nf.framework.act.browser;

import nf.framework.core.LoadSysSoft;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
		if (url != null){
			if (url.endsWith(".mp3") || url.endsWith(".mp4")) {
				new LoadSysSoft().OpenVideo(context, url);
			} else if(url.startsWith("tel:")){
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
				context.startActivity(intent); 
			}else{
				view.loadUrl(url);
			}
		}
		return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
	}
}
