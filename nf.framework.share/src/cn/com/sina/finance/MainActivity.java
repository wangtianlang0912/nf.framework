package cn.com.sina.finance;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.sina.share.ShareComponent;
import cn.com.sina.share.ShareContentVO;
import cn.com.sina.share.ShareItemVO;
import cn.com.sina.share.ShareType;
import cn.com.sina.share.impl.OnShareItemSelectedCallback;



public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.com.sina.share.R.layout.share_demo_activity_main);

		findViewById(cn.com.sina.share.R.id.btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				ShareComponent shareComponent =new ShareComponent(MainActivity.this);
				Map<ShareType,ShareContentVO> map =new HashMap<ShareType,ShareContentVO>();
				ShareContentVO shareContentVO=new ShareContentVO();
				shareContentVO.setContent("描述");
				shareContentVO.setTitle("描述");
//				shareContentVO.setPicUrl("http://www.sinaimg.cn/cj/2014/0819/U2508P31DT20140819115759.png");
				shareContentVO.setLink("http://www.baidu.com/");
				map.put(ShareType.common,shareContentVO);
				shareComponent.setShareDialogShow(map,new OnShareItemSelectedCallback() {
					
					@Override
					public boolean onShareItemSelectedCallBack(ShareItemVO shareItem) {
						return false;
					}
				});
			}
		});
		
	}
}
