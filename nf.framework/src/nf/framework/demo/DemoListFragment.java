/**   
 * @Title: DemoListFragment.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����12:25:40 
 * @version V1.0   
*/
package nf.framework.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nf.framework.core.http.AbsUIResquestHandler;
import nf.framework.fragment.AbsListAdapter;
import nf.framework.fragment.AbsListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


public class DemoListFragment extends AbsListFragment<DemoVO>{
	
	private List<DemoVO> list=new ArrayList<DemoVO>();
	/* (non-Javadoc)
	 * @see com.example.apricotforest_commontest.AbsListFragment#getAbsListAdapter()
	 */
	@Override
	protected AbsListAdapter<?, ?> createAbsListAdapter() {
		return new DemoListAdapter(getActivity(), list);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ApricotforestCommon.widgets.OnHeaderRefreshListener#onRefresh()
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		
		getFreshListView().onRefreshComplete();
	}

	/* (non-Javadoc)
	 * @see OnFooterClick(android.view.View)
	 */
	@Override
	public void OnFooterClick(View v) {
		// TODO Auto-generated method stub
		
		
		getFreshListView().onRefreshComplete();
		setFooterViewVisible(getFreshListView(), list,0);
	}
	
	/* (non-Javadoc)
	 * @see com.example.apricotforest_commontest.AbsListFragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		DemoRequestData demoRequestData=new  DemoRequestData(getActivity());
		List<DemoVO> demoList=	demoRequestData.getDataFromCache();
		System.out.println("DemoListFragment.onResume()"+demoList);
		HashMap<String,String> hashMap=new HashMap<String, String>();
		hashMap.put("course_cid","4");
		demoRequestData.getDataFromNet(hashMap, absUIResquestHandler);
		
	}
	
	
	/** (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
	 *
	 * niufei
	 *
	 * 2014-6-5 上午10:51:14
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	/** (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#getUserVisibleHint()
	 *
	 * niufei
	 *
	 * 2014-6-5 上午10:51:30
	 */
	@Override
	public boolean getUserVisibleHint() {
		// TODO Auto-generated method stub
		return super.getUserVisibleHint();
		
		
	}
	
	
	private AbsUIResquestHandler<List<DemoVO>> absUIResquestHandler=new AbsUIResquestHandler<List<DemoVO>>() {
		
		@Override
		public void onPreExcute() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onSuccessPostExecute(List<DemoVO> object) {
			// TODO Auto-generated method stub
			System.out.println("DemoListFragment.onSuccessPostExecute()");
			
			list.addAll(object);
			getCurrentListAdapter().notifyDataSetChanged();
		}
		
		@Override
		public void onFailurePostExecute(String failureMsg) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), failureMsg, 0).show();
		}
		
		@Override
		public void onCompleteExcute() {
			// TODO Auto-generated method stub
			System.out.println("DemoListFragment.onCompleteExcute()");
		}
	};
}