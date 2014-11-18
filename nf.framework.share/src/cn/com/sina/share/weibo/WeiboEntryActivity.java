package cn.com.sina.share.weibo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import cn.com.sina.share.R;
import cn.com.sina.share.ShareComponent;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;

public class WeiboEntryActivity extends Activity implements IWeiboHandler.Response{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		
		IWeiboShareAPI iWeiboShareAPI=ShareComponent.getmWeiboShareAPI();
		if(iWeiboShareAPI!=null){
			iWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}
	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		  switch (baseResp.errCode) {
	        case WBConstants.ErrorCode.ERR_OK:
	            Toast.makeText(this, R.string.weibosdk_toast_share_success, Toast.LENGTH_LONG).show();
	            break;
	        case WBConstants.ErrorCode.ERR_CANCEL:
	            Toast.makeText(this, R.string.weibosdk_toast_share_canceled, Toast.LENGTH_LONG).show();
	            break;
	        case WBConstants.ErrorCode.ERR_FAIL:
	            Toast.makeText(this, 
	                    getString(R.string.weibosdk_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
	                    Toast.LENGTH_LONG).show();
	            break;
		  }
		  finish();
	}
}
