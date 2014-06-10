package nf.framework.demo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import nf.framework.core.http.AbsBaseRequestData;
import nf.framework.core.http.AbsUIResquestHandler;
import nf.framework.core.http.HttpPostRequestInterface;
import nf.framework.core.http.HttpRequestInterface;
import nf.framework.core.http.ServerEngine;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DemoRequestData extends AbsBaseRequestData<List<DemoVO>> {

	private AbsUIResquestHandler<List<DemoVO>> mUIResquestHandler;
	
	private Context mcontext;
	private Map<String,String> mMap;
	public DemoRequestData(Context mcontext) {
		super(mcontext,true);
		// TODO Auto-generated constructor stub
		this.mcontext=mcontext;
	}
	
	@Override
	protected List<DemoVO> resolveJsonToObject(String jsonData) {
		// TODO Auto-generated method stub
		if(jsonData==null){
			return null;
		}
		GsonBuilder gsonBuilder=new GsonBuilder();
		Gson gson =gsonBuilder.create();
		Type listType=new TypeToken<List<DemoVO>>(){}.getType();
		return gson.fromJson(jsonData, listType);
	}
	@Override
	public List<DemoVO> getDataFromCache() {
		// TODO Auto-generated method stub
		String cacheData=getCacheStr();
		if(!TextUtils.isEmpty(cacheData)){
			return resolveJsonToObject(cacheData);
		}
		return null;
	}
	
	
	@Override
	public void getDataFromNet(Map<String, String> map,
			AbsUIResquestHandler<List<DemoVO>> absUIResquestHandler) {
		// TODO Auto-generated method stub
		this.mUIResquestHandler=absUIResquestHandler;
		this.mMap=map;
		if(mUIResquestHandler!=null){
			mUIResquestHandler.onPreExcute();
		}
		ServerEngine.getInstance().request(this);
	}
	@Override
	public HttpRequestInterface getHttpRequestInterface() {
		// TODO Auto-generated method stub
		return new HttpPostRequestInterface() {
			
			@Override
			public String bulidUrl() {
				// TODO Auto-generated method stub
				return "http://115.28.211.113/ci/index.php/category/get_grades?";
			}

			@Override
			public Map<String, String> getPostParamMap() {
				// TODO Auto-generated method stub
				return mMap;
			}
			@Override
			public void onRequestCompleted(final String responseData) {
				// TODO Auto-generated method stub
				Log.d("TestRequestData__onRequestCompleted",responseData);
				if(!TextUtils.isEmpty(responseData)){
					List<DemoVO> list=	resolveJsonToObject(responseData);
					DemoDB4oHelper.getIntance(mcontext).saveObjectList(list);
					List<DemoVO> locallist=		DemoDB4oHelper.getIntance(mcontext).fetchAllRows();
					Log.w("TestRequestData_",locallist.size()+"");
					sendSuccessResultToUI(mcontext,mUIResquestHandler,list);
				}else{
					sendFailureResultToUI(mcontext, mUIResquestHandler, "请求失败");
				}
			}

			@Override
			public void onRequestFailured(String requestErrorMsg) {
				// TODO Auto-generated method stub
				sendFailureResultToUI(mcontext, mUIResquestHandler, requestErrorMsg);
			}

		};
	}
	
	private void sendSuccessResultToUI(Context mcontext
			, final AbsUIResquestHandler<List<DemoVO>> mUIResquestHandler,final List<DemoVO> list){
		((Activity)mcontext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mUIResquestHandler.onSuccessPostExecute(list);
				mUIResquestHandler.onCompleteExcute();
			}
		});
	}
	private void sendFailureResultToUI(Context mcontext
			, final AbsUIResquestHandler<List<DemoVO>> mUIResquestHandler,final String failureMsg){
		
		((Activity)mcontext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mUIResquestHandler.onFailurePostExecute(failureMsg);
				mUIResquestHandler.onCompleteExcute();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.example.edittexttest.httprequest.AbsBaseRequestData#getCacheFileName()
	 */
	@Override
	protected String getCacheFileName() {
		// TODO Auto-generated method stub
		return DemoRequestData.class.getSimpleName();
	}
}
