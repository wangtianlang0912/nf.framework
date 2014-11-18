package cn.com.sina.finance;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class ShareUtils {

	public static boolean isDebug = false;
	
	
	public static void log(Class<?> c,String info){
		if(isDebug){
			if(c!= null && info != null){
				Log.d(c.getSimpleName(), info);
			}	
			else if(info != null){
				Log.d("ShareUtils", info);
			}
		}
	}
	
	
	public static void toast(Context context,String info){
		if(context != null && !TextUtils.isEmpty(info)){
			Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
		}	
	}
	
}
