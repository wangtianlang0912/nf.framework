package nf.framework.act;

import java.util.ArrayList;
import java.util.List;

import nf.framework.expand.viewpagerindicator.IconPagerAdapter;
import nf.framework.expand.viewpagerindicator.TabPageIndicator;
import nf.framework.expand.viewpagerindicator.TabPageIndicator.TabImgPosition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;

/**
 * Tab页面手势滑动切换以及动画效果
 * 
 * @author D.Winter
 * 
 */
public abstract class AbsSlidingTabBarActivity extends AbsSlidingBaseActivity{

	private ViewPager mPager;// 页卡内容
	private List<TabBarVO> tabBarList = new ArrayList<TabBarVO>();// 页卡头标
	
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private TabPageIndicator indicator = null;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewGroup mainLanderView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		InitTabBarView();
		InitViewPager();
	}

	private void initView() {
		// 设置布局
		mainLanderView = (ViewGroup) LayoutInflater.from(this).inflate(getMainLayout(), null);
		super.mainlayout.addView(mainLanderView);
		indicator = (TabPageIndicator) findViewById(getTabBarLinearLayoutId());
		mPager = (ViewPager) findViewById(getViewPagerId());
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),fragmentList);
		mPager.setAdapter(mSectionsPagerAdapter);
		mSectionsPagerAdapter.notifyDataSetChanged();
		indicator.setViewPager(mPager);
		indicator.setOnPageChangeListener(new TabBarOnPageChangeListener());
	}

	protected abstract int getMainLayout();

	protected abstract int getTabBarLinearLayoutId();

	/**
	 * 初始化头标
	 */
	private void InitTabBarView() {
		tabBarList.clear();
		tabBarList.addAll(makeTabBarList());
		if (tabBarList.size() <= 1) {
			indicator.setVisibility(View.GONE);
			return;
		}
		indicator.setVisibility(View.VISIBLE);
		indicator.notifyDataSetChanged();
		indicator.setTabViewPadding(30,10,30,10);
	}

	protected abstract List<TabBarVO> makeTabBarList();

	protected abstract int getViewPagerId();

	public List<TabBarVO> getTabBarList() {
		return tabBarList;
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mSectionsPagerAdapter.setFragments(getFragmentList(tabBarList));
		mPager.setOffscreenPageLimit(tabBarList.size());// 设置缓存页面，当前页面的相邻N各页面都会被缓存
		setCurrentTabItem(0);
	}

	protected void onRebuildTabView(){
		
		InitTabBarView();
		InitViewPager();
	}
	protected abstract List<Fragment> getFragmentList(List<TabBarVO> tabBarList);

	/***
	 * 
	 * @param tabBar
	 */
	public void setCurrentTabItem(TabBarVO tabBar) {
		if (tabBar == null || !tabBarList.contains(tabBar)) {
			return;
		}
		setCurrentTabItem(tabBarList.indexOf(tabBar));
	}

	/***
	 * 
	 * @param position
	 */
	public void setCurrentTabItem(int position) {

		mPager.setCurrentItem(position);
	}

	public Fragment getCurrentTabFragment() {
		
		return mSectionsPagerAdapter.getItem(mPager.getCurrentItem());
	
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
		List<Fragment> dataFragmentList = null;
		FragmentManager mFragmentManager;
		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> dataFragmentList) {
			super(fm);
			this.mFragmentManager=fm;
			this.dataFragmentList = dataFragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = dataFragmentList.get(position);
			return fragment;
		}
		@Override  
		public int getItemPosition(Object object) {  
		    return POSITION_NONE;  
		}  
		
		@Override
        public CharSequence getPageTitle(int position) {
            return tabBarList.get(position).getTabTitle();
        }
		@Override
		public int getCount() {

			return tabBarList.size();
		}
		public void setFragments( List<Fragment> fragmentList) {
		   if(this.dataFragmentList != null){
		      FragmentTransaction ft = mFragmentManager.beginTransaction();
		      for(Fragment f:this.dataFragmentList){
		        ft.remove(f);
		      }
		      ft.commit();
		      ft=null;
		      mFragmentManager.executePendingTransactions();
		   }
		  this.dataFragmentList = fragmentList;
		  notifyDataSetChanged();
		}

		@Override
		public int getIconResId(int index) {
			// TODO Auto-generated method stub
			return tabBarList.get(index).getImageResId();
		}
	}

	public DataFragmentCallBack dataCallBack = new DataFragmentCallBack() {

		@Override
		public void fragmentCallBack(Fragment fragment, TabBarVO mTabBarVO) {
			// TODO Auto-generated method stub
			// current fragment view init data
			if (mSectionsPagerAdapter.getItem(mPager.getCurrentItem()).equals(fragment)) {
				buildCurrentFragmentListData(fragment);
			}
		}

		@Override
		public void fragmentListOnItemClick(Fragment fragment, AdapterView<?> arg0, View arg01, int arg2, long arg3) {
			// TODO Auto-generated method stub
			currentFragmentListOnItemClick(fragment, arg0, arg01, arg2, arg3);
		}

		@Override
		public void fragmentListLoadMoreCallBack(Fragment fragment, TabBarVO mTabBarVO, int pageIndex) {
			// TODO Auto-generated method stub
			listLoadMoreListener(fragment, mTabBarVO, pageIndex);
		}

		@Override
		public void fragmentListReloadCallBack(Fragment fragment, TabBarVO mTabBarVO) {
			// TODO Auto-generated method stub
			fragmentListReloadCallBack(fragment, mTabBarVO);
		}
	};

	protected abstract void buildCurrentFragmentListData(Fragment currentFragment);

	protected abstract void currentFragmentListOnItemClick(Fragment tabBarListFragment, AdapterView<?> arg0, View arg01, int arg2, long arg3);

	protected void listLoadMoreListener(Fragment fragment, TabBarVO mTabBarVO, int pageIndex) {

	}

	protected void listReloadListener(Fragment fragment, TabBarVO mTabBarVO) {

	}

	/**
	 * 头标点击监听
	 */
	public class TabBarTitleViewOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem((Integer) v.getTag());
		}
	};

	public ViewPager getmPager() {
		return mPager;
	}

	public void slideview(final View view, final float p1, final float p2) {
		TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
		animation.setInterpolator(new OvershootInterpolator());
		animation.setDuration(300);
		// animation.setStartOffset(delayMillis);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				int left = view.getLeft() + (int) (p2 - p1);
				int top = view.getTop();
				int width = view.getWidth();
				int height = view.getHeight();
				view.clearAnimation();
				view.layout(left, top, left + width, top + height);
			}
		});
		view.startAnimation(animation);
	}

	/**
	 * 页卡切换监听
	 */
	public class TabBarOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			Fragment fragment = (Fragment) (mSectionsPagerAdapter.getItem(position));
			buildCurrentFragmentListData(fragment);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}