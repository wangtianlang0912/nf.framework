package cn.com.sina.share.weixin;


import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.util.YixinConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import cn.com.sina.finance.ShareUtils;
import cn.com.sina.share.R;
import cn.com.sina.share.ShareComponent;
import cn.com.sina.share.yxapi.YXEntryActivity;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(ShareComponent.getWXAPI()!=null){
        	ShareComponent.getWXAPI().handleIntent(getIntent(), this);
        }
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
		if(ShareComponent.getWXAPI()!=null){
        	ShareComponent.getWXAPI().handleIntent(intent, this);
        }
	}

	@Override
	public void onReq(BaseReq req) {
		SendMessageToWX.Req req1 = (SendMessageToWX.Req) req;
		Toast.makeText(WXEntryActivity.this, req1.message.title, Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
	
	private void goToGetMsg() {
		
		ShareUtils.log(WXEntryActivity.class, "goToGetMsg");	
		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;		
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer(); 
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		ShareUtils.log(WXEntryActivity.class, msg.toString());	
		finish();
		
	}
}