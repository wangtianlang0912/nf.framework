package nf.framework.httprequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class HttpRequest implements NetworkRequest {

public static final String CHARSET = "UTF-8";
	
	public static final int HTTP_OK = HttpURLConnection.HTTP_OK;
	/**
	 * net work is unavailable
	 */
	public static final int HTTP_REQUEST_NETWORK_ERROR = 1000;
	/***
	 * error input when request excuted
	 */
	public static final int HTTP_REQUEST_ERROR_INPUT = 1001;
	/***
	 * error url address when request excuted
	 */
	public static final int HTTP_REQUEST_ERROR_URL = 1003;
	/**
	 * request cancel
	 */
	public static final int HTTP_REQUEST_ERROR_CANCELED = 1004;
	/**
	 * connect request failure 
	 */
	public static final int HTTP_REQUEST_ERROR_CONNECT = 1005;
	/****
	 * request exception when excuted
	 */
	public static final int HTTP_REQUEST_EXCEPTION = 1006;

	public static final int TIMEOUT = 30000;
	private static final int BUF_SIZE = 1024;
	
	private HttpURLConnection mConnection = null;
	private boolean mStop = false;
	private int mRequestErrorCode = 0;
	private int mResponseCode=0;
	private String mResultDesc = "unknown";
	
	protected Object objAbort = new Object();
	
	private Context mcontext;
	
	
	
	public HttpRequest(Context mcontext) {
		super();
		this.mcontext = mcontext;
	}

	@Override
	public boolean downloadFile(String url, String filePath) {
		// TODO Auto-generated method stub
		try {
			return getResponseFile(url, filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String postRequest(String strUrl, Map<String, String> params) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		InputStream inStream = null;
		if (params == null) {
			return null;
		}
		try {
	        String BOUNDARY = java.util.UUID.randomUUID().toString();
	        String MULTIPART_FORM_DATA = "multipart/form-data";
			String PREFIX = "--", LINEND = "\r\n";
			
	        URL url = new URL(strUrl);
	        
			conn = getConnection(url);
			if (conn == null) {
				mRequestErrorCode = HTTP_REQUEST_ERROR_CONNECT;
				return null;
			}
			
			mConnection = conn;
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("Charset", CHARSET);
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

	        try {
				conn.connect();
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				checkStop();
				return null;
			}
	        if (checkStop()) {
				return null;
			}
	        
	        // construct params for Text
	        StringBuilder sb = new StringBuilder();
 			for (Map.Entry<String, String> entry : params.entrySet()) {
 				sb.append(PREFIX);
 				sb.append(BOUNDARY);
 				sb.append(LINEND);
 				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
 				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
 				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
 				sb.append(LINEND);
 				sb.append(entry.getValue());
 				sb.append(LINEND);
 			}
	     			
	        outStream = new DataOutputStream(conn.getOutputStream());
	        outStream.write(sb.toString().getBytes(CHARSET));
	        outStream.flush();
	        
	        if (checkStop()) {
				return null;
			}
	        
	        mResponseCode = conn.getResponseCode();  
	        mResultDesc = conn.getResponseMessage();
	        if (mResponseCode != HttpURLConnection.HTTP_OK) {
	        	mResultDesc = conn.getResponseMessage();
	        	return null;
	        }
	        
	        if (checkStop()) {
				return null;
			}
	        
	        inStream = conn.getInputStream();  
	        return getResponseData(inStream);
	    } 
		catch (SocketException e) {
			e.printStackTrace();
			mRequestErrorCode = HTTP_REQUEST_NETWORK_ERROR;
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    	mRequestErrorCode = HTTP_REQUEST_NETWORK_ERROR;
	    }
		finally {
			try {
				if (outStream != null){
					outStream.close();
					outStream = null;
				}
				if (inStream != null){
					inStream.close();
					inStream = null;
				}
				if (conn != null){
					conn.disconnect();
					conn = null;
				}
			} 
			catch (Exception e){
				e.printStackTrace();
			}
            if (checkStop()) {
            	synchronized (objAbort) {
            	    objAbort.notify();
            	}
            }
		}
	    
	    return null;
	}

	@Override
	public String getRequest(String url) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(url)) {
			mRequestErrorCode = HTTP_REQUEST_ERROR_INPUT;
			return null;
		}
		
		URL getUrl = null;
		try {
			getUrl = new URL(url);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			mRequestErrorCode = HTTP_REQUEST_ERROR_URL;
			return null;
		}
		
		mStop = false;
		InputStream input = null;
		HttpURLConnection conn = null;
		try {
			conn = getConnection(getUrl);
			if (conn == null) {
				mRequestErrorCode = HTTP_REQUEST_ERROR_CONNECT;
				return null;
			}
			mConnection = conn;
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIMEOUT*2);
			conn.setReadTimeout(TIMEOUT*2);
			conn.setDoInput(true);
	        
			if (checkStop()) {
				return null;
			}
			
			try {
				conn.connect();
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				checkStop();
				return null;
			}
			if (checkStop()) {
				return null;
			}
	        
	        mResponseCode = conn.getResponseCode();
            mResultDesc = conn.getResponseMessage();
			return getResponseData(conn.getInputStream());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (input != null){
					input.close();
					input = null;
				}
				if (conn != null){
					conn.disconnect();
					conn = null;
				}
			} 
			catch (Exception e){
				e.printStackTrace();
			}
            if (checkStop()) {
            	synchronized (objAbort) {
            	    objAbort.notify();
            	}
            }
		}
		if (!checkStop()) {
			mRequestErrorCode = HTTP_REQUEST_EXCEPTION;
		}
		return null;
	}

	@Override
	public String postFileRequest(String strUrl, byte[] data, String filePath) {
		// TODO Auto-generated method stub
	
		HttpURLConnection conn = null;
		OutputStream outStream = null;
		InputStream inStream = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				return null;
			}
			
			String BOUNDARY = java.util.UUID.randomUUID().toString();
	        String MULTIPART_FORM_DATA = "multipart/form-data";
	        String PREFIX = "--", LINEND = "\r\n";
			
	        URL url = new URL(strUrl);
	        
			conn = getConnection(url);
			if (conn == null) {
				mRequestErrorCode = HTTP_REQUEST_ERROR_CONNECT;
				return null;
			}
			// user stop
			if (checkStop()) {
				return null;
			}
			
			// Length
			int contentLength = (int) file.length();
			if (data != null) {
				contentLength += data.length;
				contentLength += 8;
			}
			
			mConnection = conn;
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("Charset", CHARSET);
	        conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);  
			//conn.setRequestProperty("Content-Length", String.valueOf(contentLength));

			try {
				conn.connect();
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				checkStop();
				return null;
			}
			if (checkStop()) {
				return null;
			}
			
			outStream = new BufferedOutputStream(conn.getOutputStream());

			if (data != null) {
				outStream.write(data);
				outStream.flush();
			}
			
			BufferedInputStream bis = null;
			try {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"" + filePath + "\"; filename=\"" + filePath + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes(CHARSET));
				
				bis = new BufferedInputStream(new FileInputStream(file));
				byte[] temp = new byte[BUF_SIZE];
				int nReadLength = 0;
				int postLength = 0;
				while ((nReadLength = bis.read(temp)) != -1) {
					// user stop
					if (checkStop()) {
						return null;
					}

					outStream.write(temp, 0, nReadLength);
					outStream.flush();
					postLength += nReadLength;
				}
				//outStream.write((newLine + divLine).getBytes());
			}
			catch (Exception e) {
				Log.e("TAG", e.getMessage());
			}
			finally {
				try {
					if (bis != null) {
						bis.close();
						bis = null;
					}
				}
				catch (Exception e) {
					Log.e("TAG", e.getMessage());
				}
			}

			outStream.write(LINEND.getBytes(CHARSET));
			//outStream.write(divLine.getBytes());
			outStream.flush();
			
			// the finsh flag
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(CHARSET);
			outStream.write(end_data);
			outStream.flush();
			
			mResponseCode = conn.getResponseCode();  
	        mResultDesc = conn.getResponseMessage();
	        if (mResponseCode != HttpURLConnection.HTTP_OK) {
	        	mResultDesc = conn.getResponseMessage();
	        	return null;
	        }
	        
	        if (checkStop()) {
				return null;
			}
	        
	        inStream = conn.getInputStream();  
	        return getResponseData(inStream);
		}
		catch (Exception e) {
	    	e.printStackTrace();
	    	mResultDesc = e.getMessage();
	    }
		finally {
			try {
				if (outStream != null){
					outStream.close();
					outStream = null;
				}
				if (inStream != null){
					inStream.close();
					inStream = null;
				}
				if (conn != null){
					conn.disconnect();
					conn = null;
				}
			} 
			catch (Exception e){
				e.printStackTrace();
			}
            if (checkStop()) {
            	synchronized (objAbort) {
            	    objAbort.notify();
            	}
            }
		}
	    
	    return null;
	}

	@Override
	public String postParamAndFile(String actionUrl,Map<String, String> params,Map<String, File> files) {
		// TODO Auto-generated method stub
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		InputStream inStream = null;
		OutputStream outStream=null;
		HttpURLConnection conn =null;
		try{
			URL uri = new URL(actionUrl);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(TIMEOUT); // cache max time
			conn.setConnectTimeout(TIMEOUT);

			conn.setDoInput(true);// allow input
			conn.setDoOutput(true);// allow output
			conn.setChunkedStreamingMode(0);
			conn.setUseCaches(false); // cache is disable
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			/*if(headers != null){
				for (String key : headers.keySet()) {
					conn.setRequestProperty(key, headers.get(key));
				}
			}*/
			outStream = new BufferedOutputStream(conn.getOutputStream());
			// construct params for Text
			if(params!=null){
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}
				outStream.write(sb.toString().getBytes("utf-8"));
			}
			// send data
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					// if (!files.get(file).exists())
					// continue;
					if (!file.getValue().exists())
						continue;
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" + file.getValue() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					try {
						if (file.getValue().length() > 0) {
							byte[] buffer = new byte[10240];
							int len = 0;
							while ((len = is.read(buffer)) != -1) {
								if (Thread.interrupted()) { // add by wlk
									throw new Exception("thread has  been interrupted!");
								}
								outStream.write(buffer, 0, len);
							}
						}
					} catch (Exception e) {
						throw e;
					} finally {
						is.close();
						outStream.write(LINEND.getBytes("utf-8"));
					}

				}
			}

			// the finsh flag
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			mResponseCode = conn.getResponseCode();  
	        mResultDesc = conn.getResponseMessage();
	        if (mResponseCode != HttpURLConnection.HTTP_OK) {
	        	mResultDesc = conn.getResponseMessage();
	        	return null;
	        }
	        
	        if (checkStop()) {
				return null;
			}
	        
	        inStream = conn.getInputStream();  
	        return getResponseData(inStream);
	        
		}
		catch (Exception e) {
	    	e.printStackTrace();
	    	mResultDesc = e.getMessage();
	    }
		finally {
			try {
				if (outStream != null){
					outStream.close();
					outStream = null;
				}
				if (inStream != null){
					inStream.close();
					inStream = null;
				}
				if (conn != null){
					conn.disconnect();
					conn = null;
				}
			} 
			catch (Exception e){
				e.printStackTrace();
			}
	        if (checkStop()) {
	        	synchronized (objAbort) {
	        	    objAbort.notify();
	        	}
	        }
		}
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}
	@Override
	public int getResponseCode() {
		// TODO Auto-generated method stub
		return mResponseCode;
	}

	@Override
	public String getResponseMessage() {
		// TODO Auto-generated method stub
		return mResultDesc;
	}
	/**
	 * return request error code
	 * @return
	 * @Param niufei
	 * @Param 2014-5-11
	 */
	@Override
	public int getRequestErrorCode() {
		return mRequestErrorCode;
	}


	public boolean getResponseFile(final String strUrl, String filePath) {
		if (TextUtils.isEmpty(strUrl)) {
			mRequestErrorCode = HTTP_REQUEST_ERROR_INPUT;
			return false;
		}
		
		URL getUrl = null;
		try {
			getUrl = new URL(strUrl);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			mRequestErrorCode = HTTP_REQUEST_ERROR_URL;
			return false;
		}
		
		mStop = false;
		InputStream input = null;
		HttpURLConnection conn = null;
		try {
			conn = getConnection(getUrl);
			if (conn == null) {
				mRequestErrorCode = HTTP_REQUEST_ERROR_CONNECT;
				return false;
			}
			mConnection = conn;
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setDoInput(true);
	        
			if (checkStop()) {
				return false;
			}
			
			try {
				conn.connect();
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				checkStop();
				return false;
			}
			if (checkStop()) {
				return false;
			}
	        
	        mResponseCode = conn.getResponseCode();
            mResultDesc = conn.getResponseMessage();
	        
	        File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(file);
			
			return getResponse(conn.getInputStream(), fos);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (input != null){
					input.close();
					input = null;
				}
				if (conn != null){
					conn.disconnect();
					conn = null;
				}
			} 
			catch (Exception e){
				e.printStackTrace();
			}
            if (checkStop()) {
            	synchronized (objAbort) {
            	    objAbort.notify();
            	}
            }
		}
		if (!checkStop()) {
			mRequestErrorCode = HTTP_REQUEST_EXCEPTION;
		}
		return false;
	}
	
	private synchronized boolean checkStop() {
		if (mStop) {
			mRequestErrorCode = HTTP_REQUEST_ERROR_CANCELED;
			return true;
		}
		return false;
	}
	
	private boolean getResponse(InputStream input, OutputStream output) {
		if (input == null || output == null) {
			return false;
		}
		byte[] data = new byte[1024];
		int i = 0;
		try {
			while(!checkStop() && (i = input.read(data)) != -1){
				output.write(data, 0, i);
				output.flush();
				
				if (checkStop()) {
					return false;
				}
			}
		
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (checkStop()){
			return false;
		}
		return true;
	}
	private HttpURLConnection getConnection(URL url) {
		String[] apnInfo = null;
		if(mcontext==null){
			throw new RuntimeException(" context is empty ");
		}
		WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState(); 
        if (wifiState == WifiManager.WIFI_STATE_DISABLED){
        	apnInfo = getAPN(mcontext);
        	if (apnInfo == null) {
        		return null;
        	}
        }
        
		HttpURLConnection conn = null;
		try {
			if (apnInfo != null && !TextUtils.isEmpty(apnInfo[0]) && !TextUtils.isEmpty(apnInfo[1])){
				InetSocketAddress addr = new InetSocketAddress(apnInfo[0], Integer.valueOf(apnInfo[1]));                  
			    Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);    
			    conn = (HttpURLConnection)url.openConnection(proxy);
			}
			else {
				conn = (HttpURLConnection)url.openConnection();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			mResultDesc = e.getMessage();
			conn = null;
		}
		
		return conn;
	}
	
	public String[] getAPN(Context context){
		if (context == null) {
			return null;
		}
		
		String[] apnInfo = new String[2];
		Cursor mCursor = context.getContentResolver().query( 
                Uri.parse("content://telephony/carriers/preferapn"), 
                null, null, null, null); 
		if (mCursor != null) { 
	        try { 
	        	mCursor.moveToFirst();
	        	apnInfo = new String[2];
	        	apnInfo[0] = mCursor.getString(mCursor.getColumnIndex("proxy"));
	        	apnInfo[1] = mCursor.getString(mCursor.getColumnIndex("port"));
	        } 
	        catch (Exception ex) { 
	        	ex.printStackTrace();
	        	mResultDesc = ex.getMessage();
	        } 
	        finally { 
	            mCursor.close(); 
	        } 
		}
		
		if (apnInfo == null || TextUtils.isEmpty(apnInfo[0]) || TextUtils.isEmpty(apnInfo[1])) {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        if (connectivity == null) {
	        	return null;
	        }
	        
	        NetworkInfo info = connectivity.getActiveNetworkInfo();
	        if (info == null || !info.isAvailable()) {
	            NetworkInfo[] infoAll = connectivity.getAllNetworkInfo();
	            if (infoAll != null) {
	                for (int i = 0; i < infoAll.length; i++) {
	                    if (infoAll[i].getState() == NetworkInfo.State.CONNECTED) {
	                        apnInfo[0] = android.net.Proxy.getDefaultHost();
	                        apnInfo[1] = String.valueOf(android.net.Proxy.getDefaultPort());
	                    	return apnInfo;
	                    }  
	                }  
	            } 
	        } 
	        else {
	          apnInfo[0] = android.net.Proxy.getHost(context);
	          apnInfo[1] = String.valueOf(android.net.Proxy.getPort(context));
	        }
		}
		return apnInfo;
	}
	
	
	
	private String getResponseData(InputStream input) {
		if (input == null) {
			return null;
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] tmpBuf = new byte[BUF_SIZE];
			int i = 0;
			while (!mStop && (i = input.read(tmpBuf)) != -1){
				output.write(tmpBuf, 0, i);
			}
			if (checkStop()) {
				return null;
			}
			
			return output.toString(HTTP.UTF_8);
		}
		catch (Exception e) {
			e.printStackTrace();
			mResultDesc = e.getMessage();
		}
		
		return null;
	}
}
