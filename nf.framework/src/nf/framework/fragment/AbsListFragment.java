package nf.framework.fragment;

import java.util.List;

import nf.framework.R;
import nf.framework.expand.widgets.OnFooterLoadMoreListener;
import nf.framework.expand.widgets.OnHeaderRefreshListener;
import nf.framework.expand.widgets.UpFreshListView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AbsListFragment<T> extends Fragment implements
		OnItemClickListener, OnHeaderRefreshListener, OnFooterLoadMoreListener {
	private UpFreshListView mlistview;
	private AbsListAdapter<?, ?> listItemAdapter;
	private View viewLayout = null;

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
	}

	private void initView(View v) {

		mlistview = (UpFreshListView) v.findViewById(R.id.common_listview);
		View headerView=getListHeaderView();
		if(headerView!=null){
			mlistview.addHeaderView(headerView);
		}
		listItemAdapter=createAbsListAdapter();
		if(listItemAdapter!=null){
			// ���listview��footView
			mlistview.setAdapter(listItemAdapter);
			listItemAdapter.notifyDataSetChanged();
		}
		/**
		 * ���¹���ˢ��
		 */
		mlistview.setOnHeaderRefreshListener(this);
		mlistview.setFooterOnClickListener(this);
		mlistview.setOnItemClickListener(this);
	}
	
	protected abstract AbsListAdapter<?, ?> createAbsListAdapter();
	
	
	protected View getListHeaderView(){
		return null;
		
	}
	
	protected UpFreshListView getFreshListView(){
		
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
	 * @param 2014-5-8 ����4:28:37
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
}
