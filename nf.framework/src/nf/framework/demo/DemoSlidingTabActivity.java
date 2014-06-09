/**
 * 
 */
package nf.framework.demo;

import java.util.ArrayList;
import java.util.List;

import nf.framework.R;
import nf.framework.act.AbsSlidingTabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**

 * @TODO 

 * @author niufei

 * @Time 2014-6-9 下午6:38:09

 *

 */
public class DemoSlidingTabActivity extends AbsSlidingTabActivity{

	
	/** (non-Javadoc)
	 *
	 * @see nf.framework.act.AbsSlidingTabActivity#onCreate(android.os.Bundle)
	 *
	 * niufei
	 *
	 * 2014-6-9 下午6:38:40
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}
	/** (non-Javadoc)
	 *
	 * @see nf.framework.act.AbsSlidingTabActivity#getBehindContentView()
	 *
	 * niufei
	 *
	 * 2014-6-9 下午6:38:25
	 */
	@Override
	protected View getBehindContentView() {
		// TODO Auto-generated method stub
		return LayoutInflater.from(this).inflate(R.layout.bannerlist_item, null);
	}

	/** (non-Javadoc)
	 *
	 * @see nf.framework.act.AbsSlidingTabActivity#createItemTabData()
	 *
	 * niufei
	 *
	 * 2014-6-9 下午6:38:25
	 */
	@Override
	public List<TabItemActVO> createItemTabData() {
		// TODO Auto-generated method stub
		List<TabItemActVO> tabList=new ArrayList<TabItemActVO>();
		tabList.add(new TabItemActVO(DemoTabBarActivity.class,R.drawable.ic_launcher,"tab1"));
		tabList.add(new TabItemActVO(DemoTabBarActivity.class,R.drawable.ic_launcher,"tab2"));
		tabList.add(new TabItemActVO(DemoTabBarActivity.class,R.drawable.ic_launcher,"tab3"));
		return tabList;
	}

	/** (non-Javadoc)
	 *
	 * @see nf.framework.act.AbsSlidingTabActivity#buildItemTabView(nf.framework.act.AbsSlidingTabActivity.TabItemActVO)
	 *
	 * niufei
	 *
	 * 2014-6-9 下午6:38:25
	 */
	@Override
	public View buildItemTabView(TabItemActVO tabItem) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(this).inflate(R.layout.tab_item_menu, null);
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.tab_item_layout);
		ImageView iv = (ImageView) view.findViewById(R.id.tab_item_image);
		TextView tv1 = (TextView)view.findViewById(R.id.tab_item_text);
		iv.setImageResource(tabItem.getImageResId());
		tv1.setText(tabItem.getTitle());
		tv1.setVisibility(View.VISIBLE);
		
		return view;
	}

}
