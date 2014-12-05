package nf.framework.core.http;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import nf.framework.core.cache.CacheDataMaster;
import nf.framework.core.exception.NFRuntimeException;
import nf.framework.core.util.android.CheckInternet;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


public abstract class AbsBaseRequestData<T> {

	private static final String TAG="BaseRequestData";
	protected Context mcontext;
	protected NetworkRequest  mNetworkRequest;
	private boolean isCacheData=false;
	private HttpRequestInterface mHttpRequestInterface;
	private String session;
	private boolean interrupt;
	private CacheDataMaster cacheDataMaster;
	public AbsBaseRequestData(Context mcontext,boolean isCacheData) {
		if(mcontext==null){
			throw  new NFRuntimeException("mcontext can not be empty!");
		}
		this.isCacheData=isCacheData;
		this.mcontext = mcontext;
		if(mNetworkRequest==null)
			mNetworkRequest = new HttpRequest(mcontext);
		
		
		String fileFolderName="requestData";
		cacheDataMaster=new CacheDataMaster(mcontext, fileFolderName);
		
		this.mHttpRequestInterface=getHttpRequestInterface();
		
		
	}
	
	public abstract HttpRequestInterface getHttpRequestInterface();

	protected void run() {
		try {
			if(mHttpRequestInterface==null){
				throw new NFRuntimeException("HTTP request must implements HttpRequestInterface");	
			}
			
			if(mHttpRequestInterface instanceof HttpPostRequestInterface){
				
				httpPost(mNetworkRequest);
				
			}else if(mHttpRequestInterface instanceof HttpPostByteRequestInterface){

				httpPostByteData(mNetworkRequest);
			}else if(mHttpRequestInterface instanceof HttpPostByteFileRequestInterface){
			
				httpPostByteFileData(mNetworkRequest);
			}else{
				
				httpGet(mNetworkRequest);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void httpPostByteData(NetworkRequest mNetworkRequest2) {
		// TODO Auto-generated method stub
		if(mHttpRequestInterface==null){
			throw new NFRuntimeException("HTTP post byte request must implements HttpPostByteRequestInterface");
		}
		String url =mHttpRequestInterface.bulidUrl();
		String responseData = null;

		Log.d(TAG, "HTTP post BEGIN: " + url);
		HttpPostByteRequestInterface byteRequestInterface=(HttpPostByteRequestInterface)mHttpRequestInterface;
		responseData = mNetworkRequest.postRequest(url, byteRequestInterface.getByteData());
		mNetworkRequest.getSession();
		onNetWorkRequested(mNetworkRequest, responseData);	
	}
	private void httpPostByteFileData(NetworkRequest mNetworkRequest2) {
		// TODO Auto-generated method stub
		if(mHttpRequestInterface==null){
			throw new NFRuntimeException("HTTP post byte request must implements HttpPostByteRequestInterface");
		}
		String url =mHttpRequestInterface.bulidUrl();
		String responseData = null;

		Log.d(TAG, "HTTP post BEGIN: " + url);
		HttpPostByteFileRequestInterface byteRequestInterface=(HttpPostByteFileRequestInterface)mHttpRequestInterface;
		responseData = mNetworkRequest.postFileRequest(url, byteRequestInterface.getByteData(),byteRequestInterface.getFilePath());
		onNetWorkRequested(mNetworkRequest, responseData);	
	}
	private void httpPost(NetworkRequest mNetworkRequest) {
		// TODO Auto-generated method stub
		if(mHttpRequestInterface==null){
			throw new NFRuntimeException("HTTP post request must implements HttpPostRequestInterface");
		}
		String url =mHttpRequestInterface.bulidUrl();
		String responseData = null;

		Log.d(TAG, "HTTP post BEGIN: " + url);
		if(mHttpRequestInterface instanceof HttpPostFilesRequestInterface){
			Map<String, String> mPostParams=((HttpPostFilesRequestInterface)mHttpRequestInterface).getPostParamMap();
			Map<String, File> mPostFiles=((HttpPostFilesRequestInterface)mHttpRequestInterface).getPostFilesMap();
			if((mPostParams==null||mPostParams.isEmpty())&&(mPostFiles==null||mPostFiles.isEmpty())){
				throw new NFRuntimeException("HTTP post file request must contain a param map or a file map !");
			}
			responseData = mNetworkRequest.postParamAndFile(url, mPostParams, mPostFiles);
		}else {
			Map<String, String> mPostParams=((HttpPostRequestInterface)mHttpRequestInterface).getPostParamMap();
			if(mPostParams==null||mPostParams.isEmpty()){
				throw new NFRuntimeException("HTTP post request must contain a param map !");
			}
			responseData = mNetworkRequest.postRequest(url, mPostParams);
		}
		
		onNetWorkRequested(mNetworkRequest, responseData);
	}
	private void httpGet(NetworkRequest mNetworkRequest) {
		// TODO Auto-generated method stub
		if(mHttpRequestInterface==null){
			throw new NFRuntimeException("http get Request method must implements HttpGetRequestInterface");
		}
		String url =mHttpRequestInterface.bulidUrl();
		String responseData = null;

		Log.d(TAG, "HTTP GET BEGIN: " + url);
		
		responseData = mNetworkRequest.getRequest(url);
		onNetWorkRequested(mNetworkRequest, responseData);
	}


	public void cancel() {
		if (mNetworkRequest != null) {
			mNetworkRequest.cancel();
		}
		ServerEngine.getInstance().cancel(this);
	}
	
	private void onNetWorkRequested(NetworkRequest mNetworkRequest,String responseData){
		
		//while request cancel or other request error
		if( mNetworkRequest.getRequestErrorCode()!=0){
			
			if (mHttpRequestInterface != null) {
				mHttpRequestInterface.onRequestFailured(HttpReponseConfig.getReponseDataByCode(mNetworkRequest.getRequestErrorCode()));
			}
			return;
		}
		int responseCode=mNetworkRequest.getResponseCode();
		
		String session =mNetworkRequest.getSession();
		setSession(session);
		if (responseCode != HttpRequest.HTTP_OK) {
			
			String responseMessage=mNetworkRequest.getResponseMessage();
			if (mHttpRequestInterface != null) {
				mHttpRequestInterface.onRequestFailured(String.valueOf(responseCode)+"___"+responseMessage);
			}
			return;
		}
		
		if(onPretreatReponseData(responseData)){
			//缓存数据 
			saveCache(responseData);
			if (mHttpRequestInterface != null) {
				
				mHttpRequestInterface.onRequestCompleted(responseData);
			}
		}
	}
	
	private void setSession(String session2) {
		// TODO Auto-generated method stub
		this.session=session2;
	}

	public String getSession() {
		return session;
	}

	/**
	 * 数据预处理 用户处理session过期等通用逻辑处理
	 * 默认为true 
	 * @return
	 */
	protected boolean onPretreatReponseData(String responseData){
		
		return true;
	}
	
	protected abstract T resolveJsonToObject(String jsonData);
	
	protected abstract String getCacheFileName();
	
	protected  void saveCache(String jsonData){
	
		if(isCacheData&&!TextUtils.isEmpty(jsonData)){
			String cacheName=getCacheFileName();
			if(TextUtils.isEmpty(cacheName)){
				throw new NFRuntimeException(" cacheFileName is null");
			}
			cacheDataMaster.saveToCacheFile(jsonData,cacheName);
		}
	}
	
	protected String getCacheStr(){
		String cacheName=getCacheFileName();
		if(TextUtils.isEmpty(cacheName)){
			throw new NFRuntimeException(" cacheFileName is null");
		}
		return cacheDataMaster.readCacheFile(cacheName);
	}
	
	public abstract void requestDataFromNet(Map<String,String> map,AbsUIResquestHandler<T> absUIResquestHandler);

	public void excute(){
		if(isInterrupt()){
			return;
		}
		if(CheckInternet.checkInternet(mcontext)){
			ServerEngine.getInstance().request(this);
		}else{
			this.mHttpRequestInterface.onRequestFailured(HttpReponseConfig.getReponseDataByCode(HttpRequest.HTTP_REQUEST_NETWORK_ERROR));
		}
	}
	
	public void excuteWithSession(String session){
		if(isInterrupt()){
			return;
		}
		if(CheckInternet.checkInternet(mcontext)){
			mNetworkRequest.setSessionToHeader(session);
			ServerEngine.getInstance().request(this);
		}else{
			this.mHttpRequestInterface.onRequestFailured(HttpReponseConfig.getReponseDataByCode(HttpRequest.HTTP_REQUEST_NETWORK_ERROR));
		}	
	}
	
	public abstract T getDataFromCache();
	
	protected String buildGetUrlByMap(String url ,Map<String,String> map){
		
		StringBuffer sb=new StringBuffer();
		if(map!=null){
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				sb.append(key);
				sb.append("=");
				Object val = entry.getValue();
				sb.append(val);
				sb.append("&");
			}
		}
		return url + sb.toString();
	}

	public NetworkRequest getmNetworkRequest() {
		return mNetworkRequest;
	}

	public boolean isInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}
	
	
}
