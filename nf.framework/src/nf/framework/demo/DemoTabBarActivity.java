/**
 * 
 */
package nf.framework.demo;

import java.util.ArrayList;
import java.util.List;

import nf.framework.R;
import nf.framework.act.AbsTabBarActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * 
 * @TODO
 * 
 * @author niufei
 * 
 * @Time 2014-6-3 下午5:26:14
 * 
 * 
 */
public class DemoTabBarActivity extends AbsTabBarActivity {

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#getMainLayout()
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected int getMainLayout() {
		// TODO Auto-generated method stub
		return R.layout.demo_tabbar_main;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#getTabBarLinearLayoutId()
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected int getTabBarLinearLayoutId() {
		// TODO Auto-generated method stub
		return R.id.tabbar_main_title_layout;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#getTabBarItemView(nf.framework.act.AbsTabBarActivity.TabBarVO)
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected View getTabBarItemView(TabBarVO tabBar) {
		// TODO Auto-generated method stub

		View view = LayoutInflater.from(this).inflate(R.layout.demo_tab_item,
				null);
		TextView textView = (TextView) view.findViewById(R.id.tab_item_title);
		textView.setText(tabBar.getTabTitle());
		
		return view;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#makeTabBarList()
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected List<TabBarVO> makeTabBarList() {
		// TODO Auto-generated method stub
		List<TabBarVO> tabBarList = new ArrayList<AbsTabBarActivity.TabBarVO>();
		for (int i = 0; i < 4; i++) {
			tabBarList.add(new TabBarVO(i, "tabBar" + i));
		}
		return tabBarList;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#getViewPagerId()
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected int getViewPagerId() {
		// TODO Auto-generated method stub
		return R.id.tabbar_main_viewpager;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#getFragmentList(java.util.List)
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected List<Fragment> getFragmentList(List<TabBarVO> tabBarList) {
		// TODO Auto-generated method stub
		List<Fragment>	 fragmentList=new ArrayList<Fragment>();
		for(TabBarVO tabBar:tabBarList){
			
			DemoListFragment demoListFragment=new DemoListFragment();
			fragmentList.add(demoListFragment);
		} 
		return fragmentList;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#buildCurrentFragmentListData(android.support.v4.app.Fragment)
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected void buildCurrentFragmentListData(Fragment currentFragment) {
		// TODO Auto-generated method stub

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see nf.framework.act.AbsTabBarActivity#currentFragmentListOnItemClick(android.support.v4.app.Fragment,
	 *      android.widget.AdapterView, android.view.View, int, long)
	 * 
	 *      niufei
	 * 
	 *      2014-6-3 下午5:26:24
	 */
	@Override
	protected void currentFragmentListOnItemClick(Fragment tabBarListFragment,
			AdapterView<?> arg0, View arg01, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}
