/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nf.framework.expand.viewpagerindicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import nf.framework.expand.R;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {
    /** Title text used when no title is provided by the adapter. */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    private Runnable mTabSelector;

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView)view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
            if (oldSelected == newSelected && mTabReselectedListener != null) {
                mTabReselectedListener.onTabReselected(newSelected);
            }
        }
    };

    private final IcsLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mMaxTabWidth;
    private int mSelectedTabIndex;
    
    private OnTabReselectedListener mTabReselectedListener;

    private int mTabViewGravity = Gravity.CENTER;
    
    private int mTabViewBackground = R.drawable.expand_tab_indicator_bg;
    
    private int mTabViewPaddingTop =10;
    private int mTabViewPaddingLeft =10;
    private int mTabViewPaddingRight =10;
    private int mTabViewPaddingBottom =10;
    
    private int mTabViewTextSize =14;
    private int mTabViewTextColor = Color.BLACK;
    
    private TabImgPosition mImgPosition= TabImgPosition.Left;
    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        mTabLayout = new IcsLinearLayout(context,attrs);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {
                mMaxTabWidth = (int)(MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private void addTab(int index, CharSequence text, int iconResId) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);
        tabView.setSingleLine(true);
        tabView.setTextColor(mTabViewTextColor);
        tabView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabViewTextSize);
        tabView.setGravity(mTabViewGravity);
        tabView.setBackgroundResource(mTabViewBackground);
        tabView.setPadding(mTabViewPaddingLeft,mTabViewPaddingTop, mTabViewPaddingRight,mTabViewPaddingBottom);
        if (iconResId != 0) {
        	switch (this.mImgPosition) {
			case Left:
				tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
				break;
			case Top:
				tabView.setCompoundDrawablesWithIntrinsicBounds(0,iconResId, 0, 0);
				break;
			case Right:
				tabView.setCompoundDrawablesWithIntrinsicBounds(0,0,iconResId, 0);
				break;
			case Bottom:
				tabView.setCompoundDrawablesWithIntrinsicBounds(0,0, 0,iconResId);
				break;
			}
        }

        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }

    public void setTabViewGravity(int gravity){
    	
    	this.mTabViewGravity=gravity;
    	for(int i= 0;i<mTabLayout.getChildCount();i++){
    		View childView =mTabLayout.getChildAt(i);
    		if(childView instanceof TabView){
    			((TabView) childView).setGravity(mTabViewGravity);
    		}
    	}
    }
    public void setImgPosition(TabImgPosition position){
    	
    	this.mImgPosition =position;
    	notifyDataSetChanged();
    }
    
    public void setTabViewBackground(int backgroundDrawable){
    	
    	this.mTabViewBackground = backgroundDrawable;
    	for(int i= 0;i<mTabLayout.getChildCount();i++){
    		View childView =mTabLayout.getChildAt(i);
    		if(childView instanceof TabView){
    			childView.setBackgroundResource(backgroundDrawable);
    		}
    	}
    }
    public void setTabViewPadding(int left,int top,int right ,int bottom){
    	this.mTabViewPaddingLeft =left;
    	this.mTabViewPaddingTop =top;
    	this.mTabViewPaddingRight =right;
    	this.mTabViewPaddingBottom =bottom;
    	for(int i= 0;i<mTabLayout.getChildCount();i++){
    		View childView =mTabLayout.getChildAt(i);
    		if(childView instanceof TabView){
    			childView.setPadding(left, top, right, bottom);
    		}
    	}
    }
    
    public void setTabViewTextSize(int textSize){
    	
    	this.mTabViewTextSize =textSize;
    	for(int i= 0;i<mTabLayout.getChildCount();i++){
    		View childView =mTabLayout.getChildAt(i);
    		if(childView instanceof TabView){
    			((TabView) childView).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    		}
    	}
    }
    public void setTabViewTextColor(int textColor){
    	
    	this.mTabViewTextColor =textColor;
    	for(int i= 0;i<mTabLayout.getChildCount();i++){
    		View childView =mTabLayout.getChildAt(i);
    		if(childView instanceof TabView){
    			((TabView) childView).setTextColor(getResources().getColorStateList(mTabViewTextColor));
    		}
    	}
    }
    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter)adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    private class TabView extends TextView {
        private int mIndex;

        
        public TabView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		public TabView(Context context,AttributeSet attrs) {
            super(context,attrs);
        }
        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }

        public int getIndex() {
            return mIndex;
        }
    }
    
    public enum TabImgPosition{
    	
    	Left,Top,Right,Bottom
    }
}
