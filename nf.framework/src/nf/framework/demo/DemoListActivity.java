/**   
 * @Title: ListActivity.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����11:23:50 
 * @version V1.0   
*/
package nf.framework.demo;

import nf.framework.R;
import nf.framework.act.AbsBaseActivity;
import nf.framework.statistic.service.ApricotStatisticAgent;
import android.os.Bundle;


public  class DemoListActivity extends AbsBaseActivity {

	private DemoListFragment demoListFragment;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		demoListFragment=new DemoListFragment();
		setFragmentView(R.id.common_basemain_main_layout, demoListFragment);
	}
	
	/** (non-Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 *
	 * niufei
	 *
	 * 2014-6-5 下午6:31:17
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		ApricotStatisticAgent.onResume(this, 0,null);
	}
}
