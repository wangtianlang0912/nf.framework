/**   
 * @Title: ListAdapter.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����12:08:56 
 * @version V1.0   
*/
package nf.framework.fragment;

import java.util.List;

import nf.framework.R;
import nf.framework.expand.widgets.UpFreshListView;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public abstract class AbsListAdapter<T,ViewHolder> extends BaseAdapter {
	
	protected List<T> mList;
	protected LayoutInflater mLayoutInflater;
	protected DisplayImageOptions options;
	/**
	 * @param mcontext
	 * @param list
	 */
	public AbsListAdapter(Context mcontext, List<T> list) {
		// TODO Auto-generated constructor stub
		this(mcontext,null,list);
		
	}
	/**
	 * @param mcontext
	 * @param category_listview
	 * @param list
	 */
	public AbsListAdapter(Context mcontext, UpFreshListView category_listview,List<T> list) {
		// TODO Auto-generated constructor stub
		mList=list;
		mLayoutInflater=LayoutInflater.from(mcontext);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}
	
	public AbsListAdapter(Context mcontext, UpFreshListView category_listview,List<T> list,DisplayImageOptions options) {
		
		mList=list;
		mLayoutInflater=LayoutInflater.from(mcontext);
		this.options=options;
		
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList!=null?mList.size():0;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mList!=null?mList.get(position):null;
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();

		} else{ 
			int layoutId=getItemViewLayout();
			if(layoutId==0){
				throw new RuntimeException("itemLayoutid == 0");
			}
			convertView =mLayoutInflater.inflate(layoutId,null);
			if(convertView==null){
				throw new RuntimeException("convertView is empty");
			}
			holder=	buildItemViewHolder(convertView);
			if(holder==null){
				throw new RuntimeException("holder is empty");
			}
			convertView.setTag(holder);
		}	
		
		T object=getItem(position);
		
		if(object!=null){	
			bindDataToView(object,holder);
		}
		return convertView;
	}
	
	protected  void setImageLoader(ImageView imageView,String url,SimpleImageLoadingListener simpleImageLoadingListener){
		
		setImageLoader(imageView, url, simpleImageLoadingListener,null);
	}
	
	protected  void setImageLoader(ImageView imageView,String url,SimpleImageLoadingListener simpleImageLoadingListener,ImageLoadingProgressListener progressListener){
		if(imageView==null||TextUtils.isEmpty(url)){
			return;
		}
		ImageLoader.getInstance().displayImage(url,imageView, options, simpleImageLoadingListener,progressListener);
	}
	
	protected abstract int getItemViewLayout();
	
	
	protected abstract ViewHolder buildItemViewHolder(View convertView);
	
	
	protected abstract void bindDataToView(T object, ViewHolder holder);
}
