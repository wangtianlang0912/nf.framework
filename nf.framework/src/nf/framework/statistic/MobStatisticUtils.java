package nf.framework.statistic;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
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
	
	public void onEvent(){
		
	}
}
