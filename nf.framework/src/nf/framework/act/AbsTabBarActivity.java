package nf.framework.act;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Tab页面手势滑动切换以及动画效果
 * 
 * @author D.Winter
 * 
 */
public abstract class AbsTabBarActivity extends AbsBaseActivity {

	private ViewPager mPager;// 页卡内容
	private ImageView imageView;
	private int bmpW;
	private int offset;
	private List<TabBarVO> tabBarList = new ArrayList<TabBarVO>();// 页卡头标
	private LinearLayout titleLayout = null;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewGroup mainLanderView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		InitTabBarView();
		// InitImageView();
		InitViewPager();
	}

	private void initView() {
		// 设置布局
		mainLanderView = (ViewGroup) LayoutInflater.from(this).inflate(getMainLayout(), null);
		super.mainlayout.addView(mainLanderView);
	}

	protected abstract int getMainLayout();

	protected abstract int getTabBarLinearLayoutId();

	/**
	 * 初始化头标
	 */
	private void InitTabBarView() {
		tabBarList.clear();
		tabBarList.addAll(makeTabBarList());
		titleLayout = (LinearLayout) findViewById(getTabBarLinearLayoutId());
		if (tabBarList.size() <= 1) {
			titleLayout.setVisibility(View.GONE);
			return;
		}
		titleLayout.setWeightSum(tabBarList.size());
		for (int i = 0; i < tabBarList.size(); i++) {
			TabBarVO tabBar = tabBarList.get(i);
			View itemView = getTabBarItemView(tabBar);
			itemView.setTag(i);
			itemView.setOnClickListener(new TabBarTitleViewOnClickListener());
			titleLayout.addView(itemView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f));
		}

	}

	protected abstract View getTabBarItemView(TabBarVO tabBar);

	protected abstract List<TabBarVO> makeTabBarList();

	protected abstract int getViewPagerId();

	public List<TabBarVO> getTabBarList() {
		return tabBarList;
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(getViewPagerId());
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getFragmentList(tabBarList));
		mPager.setAdapter(mSectionsPagerAdapter);
		mPager.setOnPageChangeListener(new TabBarOnPageChangeListener());
		mPager.setOffscreenPageLimit(tabBarList.size());// 设置缓存页面，当前页面的相邻N各页面都会被缓存
		setCurrentTabItem(0);
	}

	// private void InitImageView() {
	// imageView= (ImageView) findViewById(R.id.cursor);
	// bmpW = BitmapFactory.decodeResource(getResources(),
	// R.drawable.icon).getWidth();// 获取图片宽度
	// DisplayMetrics dm = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int screenW = dm.widthPixels;// 获取分辨率宽度
	// offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
	// Matrix matrix = new Matrix();
	// matrix.postTranslate(offset, 0);
	// imageView.setImageMatrix(matrix);// 设置动画初始位置
	// }
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

		if (titleLayout != null && titleLayout.getVisibility() != View.GONE) {
			View selectedView = titleLayout.getChildAt(position);
			selectedView.setSelected(true);
		}
		mPager.setCurrentItem(position);
	}

	public Fragment getCurrentTabFragment() {
		int position = 0;
		if (titleLayout != null && titleLayout.getVisibility() != View.GONE) {
			for (int i = 0; i < titleLayout.getChildCount(); i++) {
				View view = (View) titleLayout.getChildAt(i);
				if (view.isSelected()) {
					position = i;
					break;
				}
			}
		}
		return mSectionsPagerAdapter.getItem(position);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		List<Fragment> dataFragmentList = null;

		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> dataFragmentList) {
			super(fm);
			this.dataFragmentList = dataFragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = dataFragmentList.get(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return dataFragmentList.size();
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
			if (titleLayout != null && titleLayout.getVisibility() != View.GONE) {
				for (int i = 0; i < titleLayout.getChildCount(); i++) {
					titleLayout.getChildAt(i).setSelected(i == position ? true : false);
				}
			}
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

	public interface DataFragmentCallBack {

		public void fragmentCallBack(Fragment fragment, TabBarVO mTabBarVO);

		public void fragmentListOnItemClick(Fragment fragment, AdapterView<?> arg0, View arg01, int arg2, long arg3);

		public void fragmentListReloadCallBack(Fragment fragment, TabBarVO mTabBarVO);

		public void fragmentListLoadMoreCallBack(Fragment fragment, TabBarVO mTabBarVO, int pageIndex);
	}

	public class TabBarVO {

		String tabTitle;

		int tabId;

		String description;

		int imageResId;

		Object object;

		int level = -1;

		public String getTabTitle() {
			return tabTitle;
		}

		public void setTabTitle(String tabTitle) {
			this.tabTitle = tabTitle;
		}

		public int getTabId() {
			return tabId;
		}

		public void setTabId(int tabId) {
			this.tabId = tabId;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getImageResId() {
			return imageResId;
		}

		public void setImageResId(int imageResId) {
			this.imageResId = imageResId;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + tabId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TabBarVO other = (TabBarVO) obj;
			if (tabId != other.tabId)
				return false;
			return true;
		}

		public TabBarVO(String tabTitle, int tabId, String description, int imageResId) {
			super();
			this.tabTitle = tabTitle;
			this.tabId = tabId;
			this.description = description;
			this.imageResId = imageResId;
		}

		public TabBarVO(String tabTitle, int tabId, int imageResId) {
			super();
			this.tabTitle = tabTitle;
			this.tabId = tabId;
			this.imageResId = imageResId;
		}

		public TabBarVO(int tabId, String tabTitle) {
			super();
			this.tabTitle = tabTitle;
			this.tabId = tabId;
		}

		public TabBarVO(int tabId, String tabTitle, Object object) {
			super();
			this.tabTitle = tabTitle;
			this.tabId = tabId;
			this.object = object;
		}

		public TabBarVO() {
			super();
		}
	}
}