package nf.framework.video.view;

import android.app.Activity;
import android.view.View;

/**
 * 
 * @author yamin
 * 
 */
public abstract class KKViewPagerCotroller {
	public KKViewPagerCotroller(Activity activity) {
	};

	public abstract View getView();// viewpager��ȡview

	public abstract String getTitle();// viewpager��ȡ�������

	public abstract void onshow();// ��ʾ���津���¼�
}
