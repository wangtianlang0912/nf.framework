package cn.com.sina.share;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import cn.com.sina.share.impl.OnShareItemSelectedListener;

public class ShareDialog {

	private OnShareItemSelectedListener onSelectedListener;
	
	/***
	 * 
	 * @param context
	 * @param cancelListener
	 * @return
	 */
	public static Dialog showAlert(final Context context,List<ShareItemVO> list,final OnShareItemSelectedListener onSelectedListener) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = (View) inflater.inflate(R.layout.share_alert_dialog_menu_layout, null);
		Button cancelBtn=	(Button)layout.findViewById(R.id.alert_dialog_menu_cancel_btn);
		final GridView gridView = (GridView) layout.findViewById(R.id.content_gridview);
		AlertAdapter adapter = new AlertAdapter(context,list);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ShareItemVO shareItemVO=	(ShareItemVO)parent.getItemAtPosition(position);
				if(onSelectedListener!=null){
					onSelectedListener.onShareItemSelected(shareItemVO);
				}
				dlg.cancel();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.cancel();
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		Window window = dlg.getWindow();
		window.setGravity(Gravity.BOTTOM);
		if(context instanceof Activity){
			WindowManager windowManager = ((Activity)context).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			dlg.getWindow().setAttributes(lp);
		}
		return dlg;
	}

}
