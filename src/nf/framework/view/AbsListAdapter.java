/**   
 * @Title: ListAdapter.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����12:08:56 
 * @version V1.0   
*/
package nf.framework.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nf.framework.widgets.UpFreshListView;

public abstract class AbsListAdapter<T,ViewHolder> extends BaseAdapter {
	
	protected List<T> mList;
	protected LayoutInflater mLayoutInflater;
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
	
	protected abstract int getItemViewLayout();
	
	
	protected abstract ViewHolder buildItemViewHolder(View convertView);
	
	
	protected abstract void bindDataToView(T object, ViewHolder holder);
	
}
