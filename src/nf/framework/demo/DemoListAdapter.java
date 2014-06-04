/**   
 * @Title: DemoLIstAdapter.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����12:27:23 
 * @version V1.0   
*/
package nf.framework.demo;

import java.util.List;

import nf.framework.R;
import nf.framework.view.AbsListAdapter;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nf.framework.widgets.BannerGalleryView;

public class DemoListAdapter extends AbsListAdapter<DemoVO,DemoViewHolder>{
	/**
	 * @param mcontext
	 * @param list
	 */
	public DemoListAdapter(Context mcontext, List<DemoVO> list) {
		super(mcontext, list);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see com.example.apricotforest_commontest.AbsListAdapter#buildItemViewHolder(android.view.View)
	 */
	@Override
	protected DemoViewHolder buildItemViewHolder(View convertView) {
		// TODO Auto-generated method stub
		DemoViewHolder holder = new DemoViewHolder();
		holder.titleview = (TextView) convertView.findViewById(R.id.titleview);
		holder.contentView = (TextView) convertView.findViewById(R.id.contentView);
		holder.bannerGalleryView =(BannerGalleryView)convertView.findViewById(R.id.bannerGalleryView1);
		return holder;
	}
	/* (non-Javadoc)
	 * @see com.example.apricotforest_commontest.AbsListAdapter#bindDataToView(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void bindDataToView(DemoVO object,DemoViewHolder holder) {
		// TODO Auto-generated method stub
		
		holder.contentView.setText(object.getText());
		holder.titleview.setText(object.getText());
	}
	/* (non-Javadoc)
	 * @see com.example.apricotforest_commontest.Abslist.AbsListAdapter#getItemViewLayout()
	 */
	@Override
	protected int getItemViewLayout() {
		// TODO Auto-generated method stub
		return R.layout.bannerlist_item;
	}
	
}
