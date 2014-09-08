package nf.framework.expand.popup;

import java.util.ArrayList;
import java.util.List;

import nf.framework.expand.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChefListFilterDisplayAction extends PopupWindows{

	public enum FilterListMode{
		
		singleMode,doubleMode;
	}
	private View anchorView;
	ListView groupListView,childListView;
	List<FilterItemVO> groupList=new ArrayList<FilterItemVO>();

	List<FilterItemVO> childList=new ArrayList<FilterItemVO>();
	
	FilterAdapter groupFilterAdapter;
	FilterChildAdapter childFilterAdapter;
	AfterOnListItemClickCallBack mcallback;
	/**
	 * @param context
	 */
	public ChefListFilterDisplayAction(Context context,FilterListMode filterListMode,AfterOnListItemClickCallBack callBack) {
		// TODO Auto-generated constructor stub
		super(context);
		this.mcallback=callBack;
		
		switch (filterListMode) {
		case singleMode:
			childListView.setVisibility(View.GONE);
			break;
		case doubleMode:
			childFilterAdapter=new FilterChildAdapter(mContext, childList);
			childListView.setVisibility(View.INVISIBLE);
			childListView.setAdapter(childFilterAdapter);
			childFilterAdapter.notifyDataSetChanged();
			childListView.setOnItemClickListener(childItemClickListener);
			break;
		}
		groupList=new ArrayList<FilterItemVO>();
		groupFilterAdapter=new FilterAdapter(mContext, groupList,true);
		groupListView.setAdapter(groupFilterAdapter);
		groupFilterAdapter.notifyDataSetChanged();
		groupListView.setOnItemClickListener(groupItemClickListener);
	}
	@Override
	protected View getRootViewLayout() {
		// TODO Auto-generated method stub
		View layoutView=LayoutInflater.from(mContext).inflate(R.layout.chef_list_filter_layout,null);
		groupListView=	(ListView)layoutView.findViewById(R.id.filter_layout_listview1);
		childListView=(ListView)layoutView.findViewById(R.id.filter_layout_listview2);
		return layoutView;
	}
	public void setData(List<FilterItemVO> filterItemlist){
		if(filterItemlist!=null){
			groupList.addAll(filterItemlist);
			groupFilterAdapter.notifyDataSetChanged();
			if(groupList.size()>0){
				if(mcallback!=null){
					mcallback.afterOnItemClick(anchorView,groupList.get(0));
				}
			}
		}
	}
	
	
	public FilterItemVO getFilterItemByTitle(String title){
		FilterItemVO filterItemVO=	new FilterItemVO();
		filterItemVO.setTitle(title);
		if(groupList!=null&&groupList.contains(filterItemVO)){
			
			return groupList.get(groupList.indexOf(filterItemVO));
		}
		if(childList!=null&&childList.contains(filterItemVO)){
			
			return childList.get(childList.indexOf(filterItemVO));
		}
		return null;
		
	}
	OnItemClickListener groupItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			groupFilterAdapter.setSelectedPosition(arg2);
			groupFilterAdapter.notifyDataSetChanged();
			FilterItemVO filterItemVO=(FilterItemVO)arg0.getItemAtPosition(arg2);
			
			if(filterItemVO.getChildList()!=null&&!filterItemVO.getChildList().isEmpty()){
				childListView.setVisibility(View.VISIBLE);
				childList.clear();
				childList.addAll(filterItemVO.getChildList());
				childFilterAdapter.setSelectedPosition(-1);
				childFilterAdapter.notifyDataSetChanged();
			}else{
				if(childList!=null&&childFilterAdapter!=null){
					childList.clear();
					childFilterAdapter.setSelectedPosition(-1);
					childFilterAdapter.notifyDataSetChanged();
				}
				dismiss();
				if(mcallback!=null){
					mcallback.afterOnItemClick(anchorView,filterItemVO);
				}
			}
		}
		
	};
	OnItemClickListener childItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			FilterItemVO filterItemVO=(FilterItemVO)arg0.getItemAtPosition(arg2);
			childFilterAdapter.setSelectedPosition(arg2);
			childFilterAdapter.notifyDataSetChanged();
			dismiss();
			if(mcallback!=null){
				mcallback.afterOnItemClick(anchorView,filterItemVO);
			}
		}
		
	};
	@Override
	protected void setWindowLayoutParam() {
		// TODO Auto-generated method stub
		mWindow.setWidth(WindowManager.LayoutParams.FILL_PARENT);
		mWindow.setHeight(dip2px(mContext,200));
//		mWindow.setHeight(WindowManager.LayoutParams.FILL_PARENT);
		
	}
	 private int dip2px(Context context, float pxValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (pxValue * scale + 0.5f);  
	    }  
	/* (non-Javadoc)
	 * @see com.Apricotforest.ActionBar.PopupWindows#show(android.view.View)
	 */
	public void show(View anchor) {
		// TODO Auto-generated method stub
		super.show(anchor);
		this.anchorView=anchor;
	}
	
	public interface AfterOnListItemClickCallBack{
		
		public void afterOnItemClick(View anchorView,FilterItemVO filterItem);
	}
	
	public class FilterAdapter extends BaseAdapter{

		
		private Context mcontext;
		private List<FilterItemVO> mList;
		private LayoutInflater mInflater;
		private int selectedPosition=-1;
		private boolean isItemShowLogo;
		private class FilterHolder{
			
			TextView titleTextView;
		}
		/**
		 * 
		 */
		public FilterAdapter(Context context,List<FilterItemVO> list) {
			// TODO Auto-generated constructor stub
			this(context, list,false);
		}
		
		public FilterAdapter(Context context,
				List<FilterItemVO> list,boolean isItemShowLogo) {
			this.mcontext=context;
			this.mList=list;
			this.isItemShowLogo=isItemShowLogo;
		    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}
		
		public int getSelectedPosition() {
			return selectedPosition;
		}
		public void setSelectedPosition(int selectedPosition) {
			this.selectedPosition = selectedPosition;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			FilterHolder viewHolder;
			if(convertView!=null){
				viewHolder = (FilterHolder) convertView.getTag();
	        	 }else{
	        		convertView= mInflater.inflate(R.layout.chef_list_filter_item, arg2, false);
	    			viewHolder=new FilterHolder();
		    		viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.filter_item_txt);
		    		convertView.setTag(viewHolder);
	        	 }
			if(getSelectedPosition()==arg0){
				convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}else{
				convertView.setBackgroundResource(R.drawable.filter_item_group_bg);
			}
			FilterItemVO filterItemVO=mList.get(arg0);
			viewHolder.titleTextView.setText(filterItemVO.getTitle());
			return convertView;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return mList.get(position).getId();
		}
		
	}
	
	public class FilterChildAdapter extends BaseAdapter{

		
		private Context mcontext;
		private List<FilterItemVO> mList;
		private LayoutInflater mInflater;
		private int selectedPosition=-1;
		private boolean isItemShowLogo;
		private class MagzineFilterHolder{
			
			TextView titleTextView;
		}
		/**
		 * 
		 */
		public FilterChildAdapter(Context context,List<FilterItemVO> list) {
			// TODO Auto-generated constructor stub
			this(context, list,false);
		}
		
		public FilterChildAdapter(Context context,
				List<FilterItemVO> list,boolean isItemShowLogo) {
			this.mcontext=context;
			this.mList=list;
			this.isItemShowLogo=isItemShowLogo;
		    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}
		
		public int getSelectedPosition() {
			return selectedPosition;
		}
		public void setSelectedPosition(int selectedPosition) {
			this.selectedPosition = selectedPosition;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			MagzineFilterHolder viewHolder;
			if(convertView!=null){
				viewHolder = (MagzineFilterHolder) convertView.getTag();
	        	 }else{
	        		convertView= mInflater.inflate(R.layout.chef_list_filter_item, arg2, false);
	    			viewHolder=new MagzineFilterHolder();
		    		viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.filter_item_txt);
		    		convertView.setTag(viewHolder);
	        	 }
			FilterItemVO filterItemVO=mList.get(arg0);
			viewHolder.titleTextView.setText(filterItemVO.getTitle());
			return convertView;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return mList.get(position).getId();
		}
		
	}
	
	
	public static class FilterItemVO {
		
		int id;
		String title;
		Object object;
		List<FilterItemVO> childList=new ArrayList<ChefListFilterDisplayAction.FilterItemVO>();
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<FilterItemVO> getChildList() {
			return childList;
		}
		public void setChildList(List<FilterItemVO> childList) {
			this.childList = childList;
		}
		public Object getObject() {
			return object;
		}
		public void setObject(Object object) {
			this.object = object;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((title == null) ? 0 : title.hashCode());
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
			FilterItemVO other = (FilterItemVO) obj;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			return true;
		}
		
	}
}
