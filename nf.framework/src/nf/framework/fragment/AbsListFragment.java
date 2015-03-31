package nf.framework.fragment;

import java.util.List;

import nf.framework.R;
import nf.framework.expand.widgets.OnHeaderRefreshListener;
import nf.framework.expand.widgets.OnScrollLoadMoreListener;
import nf.framework.expand.widgets.UpFreshListView;
import nf.framework.statistic.MobStatisticUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AbsListFragment<T> extends Fragment implements
		OnItemClickListener, OnHeaderRefreshListener, OnScrollLoadMoreListener {
	private UpFreshListView mlistview;
	private AbsListAdapter<?, ?> listItemAdapter;
	private View viewLayout = null;
	private MobStatisticUtils mobStatisticUtils;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		mobStatisticUtils=new MobStatisticUtils(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		viewLayout = inflater.inflate(R.layout.listview_layout, container,false);
		initView(viewLayout);
		return viewLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		mobStatisticUtils.onStaFragmentResume(
				getPageName()!=null?getPageName():getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();
		mobStatisticUtils.onStaFragmentPause(
				getPageName()!=null?getPageName():getClass().getSimpleName());
	}
	protected void initView(View v) {

		mlistview = (UpFreshListView) v.findViewById(R.id.common_listview);
		View headerView=getListHeaderView();
		if(headerView!=null){
			mlistview.addHeaderView(headerView,null, false);
		}
		listItemAdapter=createAbsListAdapter();
		if(listItemAdapter!=null){
			mlistview.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
		}
		mlistview.setOnHeaderRefreshListener(this);
		mlistview.setOnScrollLoadMoreListener(this);
		mlistview.setOnItemClickListener(this);
	}
	
	protected abstract AbsListAdapter<?, ?> createAbsListAdapter();
	
	
	protected View getListHeaderView(){
		return null;
		
	}
	
	@Override  
    public void setUserVisibleHint(boolean isVisibleToUser) {  
       
       if (isVisibleToUser &&this.isResumed()){
           //相当于Fragment的onResume  
    	   onlazyLoad();
       } else {  
           //相当于Fragment的onPause  
       } 
       super.setUserVisibleHint(isVisibleToUser);
    } 
	
	public void onlazyLoad() {
		
	}
	public UpFreshListView getFreshListView(){
		
		return mlistview;
	}
	
	
	protected AbsListAdapter<?, ?> getCurrentListAdapter(){
		
		return listItemAdapter;
	}
	
	protected int getListItemCount(){
		
		return listItemAdapter!=null?listItemAdapter.getCount():0;
	}
	
	/***
	 * 
	 * @param absListview
	 * @param packagelist
	 * @param numRows
	 * @param niufei
	 * @return void
	 * @throws
	 */
	public void setFooterViewVisible(UpFreshListView absListview,List<?> list,int numRows){
		
		if(absListview==null){
			throw new RuntimeException("absListView is empty !!");
		}
		if (list.size() < numRows&&list.size()!=0) {
			absListview.addAutoFooterView();
		} else {
			absListview.removeAutoFooterView();
		}
		absListview.onRefreshComplete();
	}
	
	protected abstract String getPageName();
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	
		if(listItemAdapter!=null){
			listItemAdapter.clearDisplayedImages();
		}
		
	}
}
