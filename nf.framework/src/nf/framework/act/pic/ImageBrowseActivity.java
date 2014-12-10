package nf.framework.act.pic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nf.framework.R;
import nf.framework.act.AbsBaseActivity;
import nf.framework.core.util.android.CloseActivityClass;
import nf.framework.expand.widgets.HackyViewPager;
import nf.framework.expand.widgets.zoomPhotoView.PhotoView;
import nf.framework.expand.widgets.zoomPhotoView.PhotoViewAttacher.OnViewTapListener;
import nf.framework.http.imageload.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ImageBrowseActivity extends AbsBaseActivity {

	private ViewPager mViewPager;

	public static final String WALLPAPERBROWSE_INTENT_LIST = "wallPaperList";

	public static final String WALLPAPERBROWSE_INTENT_PAGEINDEX = "pageIndex";

	private List<ImageBrowserVO> list = new ArrayList<ImageBrowserVO>();
	private WallPaperBrowseAdapter wallPaperAdapter = null;
	private Context mcontext;
	private int currentPosition=0;
	private View bottomLayout;
	private TextView desView;
	private boolean isHidden;
//	private DisplayImageOptions options;
//	protected  ImageLoader imageLoader = ImageLoader.getInstance();
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mcontext = this;
		CloseActivityClass.activityList.add(this);
		
		
		initView();
//		initImageLoader(this);
		Intent intent = getIntent();
		list = (List<ImageBrowserVO>) intent
				.getSerializableExtra(WALLPAPERBROWSE_INTENT_LIST);
		currentPosition = intent.getIntExtra(WALLPAPERBROWSE_INTENT_PAGEINDEX, 0);

		wallPaperAdapter = new WallPaperBrowseAdapter(this, list);
		mViewPager.setAdapter(wallPaperAdapter);
		mViewPager.setCurrentItem(currentPosition, true);
		top_textview.setText((currentPosition+1) + "/" + list.size());
		desView.setText(list.get(currentPosition).getDescription());
//		options = new DisplayImageOptions.Builder()
//		.showImageForEmptyUri(R.drawable.icon_empty)
//		.showImageOnFail(R.drawable.ic_error)
//		.resetViewBeforeLoading(true)
//		.cacheOnDisc(true)
//		.imageScaleType(ImageScaleType.EXACTLY)
//		.bitmapConfig(Bitmap.Config.RGB_565)
//		.displayer(new FadeInBitmapDisplayer(300))
//		.build();
	}

	/***
	 * 初始化视图控件
	 */
	private void initView() {
		 // 设置布局
		View mainView=	 LayoutInflater.from(this).inflate(R.layout.imagebrowser_main,super.mainlayout,false);
		super.mainlayout.addView(mainView);
		super.framelayout.setBackgroundColor(Color.BLACK);
		mViewPager = (HackyViewPager)mainView.findViewById(R.id.imagebrowser_main_viewpager);
 		bottomLayout=	mainView.findViewById(R.id.imagebrowser_main_des_layout);
		desView=(TextView)mainView.findViewById(R.id.imagebrowser_main_des_txt);

		super.leftButton.setVisibility(View.VISIBLE);
		super.leftButton.setImageResource(R.drawable.common_navigate_back_btn);
		super.leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		super.rightButton.setVisibility(View.VISIBLE);
		super.rightButton.setImageResource(R.drawable.common_navigate_share_btn);
		super.rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openOptionsMenu();
			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentPosition=arg0;
				if(list!=null&&desView!=null)
					desView.setText(list.get(arg0).getDescription());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				String title=(arg0 + 1) + File.separator+ mViewPager.getAdapter().getCount();
				top_textview.setText(title);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		

	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	/**
	 * 按下返回按钮
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	/* (non-Javadoc)
	 * @see com.Apricotforest.MJAbsBaseActivity#onFinishResult()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
	
	private OnViewTapListener onImageViewTapListener(){
		return new OnViewTapListener() {
			
			@Override
			public void onViewTap(View view, float x, float y) {
				// TODO Auto-generated method stub
				isHidden =!isHidden;
				navigationBarLayout.setVisibility(isHidden?View.GONE:View.VISIBLE);
				setPullDownAnimation(navigationBarLayout,isHidden);
				bottomLayout.setVisibility(isHidden?View.GONE:View.VISIBLE);
			}
		};
		
	}
	
	private void setPullDownAnimation(View view ,boolean isHidden){
		
		Animation animation = AnimationUtils.loadAnimation(this,isHidden? R.anim.common_slide_down_out:R.anim.common_slide_up_in);   
		view.startAnimation(animation); 
	}
	 class WallPaperBrowseAdapter extends PagerAdapter {

		List<ImageBrowserVO> mlist = new ArrayList<ImageBrowserVO>();
		private ViewGroup mcontainer;
		private LayoutInflater inflater;
		private ImageLoader loadImage;
		public WallPaperBrowseAdapter(Context context, List<ImageBrowserVO> list) {
			this.mlist = list;
			inflater =((Activity) context).getLayoutInflater();
			if(loadImage==null){
				loadImage=ImageLoader.getInstance(ImageBrowseActivity.this);
			}
		}

		@Override
		public int getCount() {
			return mlist.size();
		}
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			// Now just add PhotoView to ViewPager and return it
			View imageLayout = inflater.inflate(R.layout.wallpaper_browse_layout, container, false);
			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.wallpaper_image_layout_image);
			final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.wallpaper_image_layout_loading);
			ImageBrowserVO wallPaper = mlist.get(position);
			new DownloadImgTask(mcontext,loadImage,imageView, progressBar,R.drawable.package_loading,wallPaper).execute();
			container.addView(imageLayout, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mcontainer = container;
			imageView.setOnViewTapListener(onImageViewTapListener());
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			
			mcontainer = container;
		}

		@Override
		public void startUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.startUpdate(container);
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.finishUpdate(container);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public ViewGroup getMcontainer() {
			return mcontainer;
		}

		public void setMcontainer(ViewGroup mcontainer) {
			this.mcontainer = mcontainer;
		}

	}
	/***
	 * return current pic name
	 * @return
	 */
	public String getCurrentPosPicName(){
		ImageBrowserVO imageBrowser=list.get(currentPosition);
		String bitmapFileName=ImageBrowseUtil.getTempBitmapFileName(mcontext,imageBrowser.getPicUrl());
		return bitmapFileName;
	}
	/**
	 * return current pic url
	 * @return
	 */
	public String getCurrentPosPicUrl(){
		
		String bitmapName=getCurrentPosPicName();
		String localImageUrl=ImageBrowseUtil.getTempBitmapFilePath(mcontext, bitmapName);
		return localImageUrl;
	}
}