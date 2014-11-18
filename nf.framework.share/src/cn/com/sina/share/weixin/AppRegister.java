package cn.com.sina.share.weixin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.sina.share.ShareComponent;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
/**
 * 微信
 * @author niufei
 *
 */
public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		ShareComponent.registerWXAPI(context);
	}
}
