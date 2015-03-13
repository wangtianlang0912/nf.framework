package nf.framework.act;

import nf.framework.R;
import nf.framework.expand.widgets.OnHeaderRefreshListener;
import nf.framework.expand.widgets.OnScrollLoadMoreListener;
import nf.framework.expand.widgets.UpFreshListView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AbsBaseListActivity extends AbsBaseActivity implements OnItemClickListener,OnHeaderRefreshListener,OnScrollLoadMoreListener{

	protected UpFreshListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		View mainView =LayoutInflater.from(this).inflate(R.layout.listview_layout,super.mainlayout,false);
		super.mainlayout.addView(mainView);
		
		mListView=(UpFreshListView)mainView.findViewById(R.id.common_listview);
	
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollLoadMoreListener(this);
		mListView.setOnHeaderRefreshListener(this);
	}
}
