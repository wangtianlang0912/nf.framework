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
		new Thread(new Runnable() {
			@Override
			public void run() {
				MobclickAgent.onResume(activity);
			}
		}).start();
		
	}
	
	public void onStatisticPause(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				MobclickAgent.onPause(activity);
			}
		}).start();
		
	}
	
	public void onStaFragmentResume(final String pageName){
		new Thread(new Runnable() {
			@Override
			public void run() {
				MobclickAgent.onPageStart(pageName);
			}
		}).start();
	}
	
	public void onStaFragmentPause(final String pageName){
		new Thread(new Runnable() {
			@Override
			public void run() {	
				MobclickAgent.onPageEnd(pageName);
			}
		}).start();
	}
	
	public static void onEvent(final Context context,final String value){
		new Thread(new Runnable() {
			@Override
			public void run() {	
				MobclickAgent.onEvent(context, value);
			}
		}).start();
	}
	
	public static void onEvent(final Context context,final String value,final String param){
		new Thread(new Runnable() {
			@Override
			public void run() {	
				MobclickAgent.onEvent(context, value, param);
			}
		}).start();
	}
}
