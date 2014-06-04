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
import android.os.Bundle;

import com.nf.framework.BaseActivity;


public  class DemoListActivity extends BaseActivity {

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
	
}
