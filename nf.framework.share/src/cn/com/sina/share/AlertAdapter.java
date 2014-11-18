package cn.com.sina.share;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlertAdapter extends BaseAdapter {
	private List<ShareItemVO> shareItemList;
	private Context context;

	public AlertAdapter(Context context,List<ShareItemVO> shareList) {
		this.context = context;
		this.shareItemList =shareList;
	}

	@Override
	public int getCount() {
		return shareItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return shareItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null){
			holder =new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.share_alert_dialog_menu_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.alert_dialog_item_iv);
			holder.text = (TextView) convertView.findViewById(R.id.alert_dialog_item_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		ShareItemVO shareItemVO=	shareItemList.get(position);
		holder.text.setText(shareItemVO.getTitle());
		holder.image.setImageResource(shareItemVO.getResId());
	
		
		return convertView;
	}

  class ViewHolder {
		// LinearLayout view;
		TextView text;
		ImageView image;
	}
}
