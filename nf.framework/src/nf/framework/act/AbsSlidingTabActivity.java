package nf.framework.act;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingTabActivity;

public abstract class AbsSlidingTabActivity extends SlidingTabActivity{

    protected SlidingMenu slidingMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TabHost  tabHost = getTabHostView();
        setContentView(tabHost);
        
        setBehindContentView(getBehindContentView());
        
        slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidth(getWindowManager().getDefaultDisplay().getWidth() / 40);
//        slidingMenu.setShadowDrawable(R.drawable.shadow);
//        slidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
        slidingMenu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() / 5);
        slidingMenu.setFadeEnabled(true);
        slidingMenu.setFadeDegree(0.4f);
        slidingMenu.setBehindScrollScale(0);
        
        List<TabItemActVO> tabItemList=     createItemTabData();
        if(tabItemList==null){
        	
        	throw new RuntimeException(" tabItemList is null");
        }
        for(TabItemActVO tabItemAct: tabItemList){
        
        	tabHost.addTab(tabHost.newTabSpec(tabItemAct.getTitle())
    				.setIndicator(buildItemTabView(tabItemAct))
    				.setContent(new Intent(this,tabItemAct.getActivity())));	
        }
        
        int leng = tabHost.getTabWidget().getChildCount();
		for (int i = 0; i < leng; i++) {
			if (Integer.parseInt(Build.VERSION.SDK) <= 5) {

			} else {
				tabHost.setPadding(tabHost.getPaddingLeft(),
						tabHost.getPaddingTop(), tabHost.getPaddingRight(),
						tabHost.getPaddingBottom() - 1);
			}
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(AbsSlidingTabActivity.this,arg0,0).show();
			}
		});
		
    }
    
    /**
	 * @return
	 *
	 * niufei
	 *
	 * 2014-6-9 下午6:28:51
	 */
    protected abstract View getBehindContentView();

	public abstract List<TabItemActVO> createItemTabData();
    
    public abstract View buildItemTabView(TabItemActVO tabItem);
    
	private TabHost getTabHostView(){
		
		TabHost tabHost=new TabHost(this);
		tabHost.setId(android.R.id.tabhost);
		LinearLayout linearLayout=new LinearLayout(this);
		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		FrameLayout  frameLayout=new FrameLayout(this);
		frameLayout.setId(android.R.id.tabcontent);
		frameLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		linearLayout.addView(frameLayout,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,0, 1));
		TabWidget tabWidget=new TabWidget(this);
		tabWidget.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,dip2px(this,50)));
		tabWidget.setClickable(false);
		tabWidget.setId(android.R.id.tabs);
		tabWidget.setBackgroundColor(Color.YELLOW);
		linearLayout.addView(tabWidget);
		tabHost.addView(linearLayout, LinearLayout.LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		return tabHost;
	}

	 public static int dip2px(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	    }  
	   
	protected static  class TabItemActVO{
		
    	Class activity;
		int imageResId;
		String title;
		/**
		 * @return the activity
		 */
		public Class getActivity() {
			return activity;
		}
		/**
		 * @param activity the activity to set
		 */
		public void setActivity(Class activity) {
			this.activity = activity;
		}
		/**
		 * @return the imageResId
		 */
		public int getImageResId() {
			return imageResId;
		}
		/**
		 * @param imageResId the imageResId to set
		 */
		public void setImageResId(int imageResId) {
			this.imageResId = imageResId;
		}
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		/**
		 * @param activity
		 * @param imageResId
		 * @param title
		 *
		 * niufei
		 *
		 * 2014-6-9 下午5:13:11
		 */
		public TabItemActVO(Class activity, int imageResId,
				String title) {
			super();
			this.activity = activity;
			this.imageResId = imageResId;
			this.title = title;
		}
	
		
	}
}
