package nf.framework.statistic;

import android.app.Activity;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;
/***
 * 统计事件
 * @author niufei
 *
 */
public class MobStatisticUtils {

	private Activity activity;
	public MobStatisticUtils(Activity activity){
		
		this.activity= activity;
	}
	
	public void onStatisticResume(){
		
		MobclickAgent.onResume(activity);
	}
	
	public void onStatisticPause(){
		
		MobclickAgent.onPause(activity);
		
	}
	
	public void onStaFragmentResume(String pageName){
		
		MobclickAgent.onPageStart(pageName);
	}
	
	public void onStaFragmentPause(String pageName){
		
		MobclickAgent.onPageEnd(pageName);
	}
	
	public static void onEvent(Context context,String value){
		
		MobclickAgent.onEvent(context, value);
	}
	
	public static void onEvent(Context context,String value,String param){
		
		MobclickAgent.onEvent(context, value, param);
	}
}
