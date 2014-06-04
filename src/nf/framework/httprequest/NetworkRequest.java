package nf.framework.httprequest;

import java.io.File;
import java.util.Map;

public interface NetworkRequest {

	

	public boolean downloadFile(final String url, String filePath);
	
	public String postRequest(String url, Map<String, String> params);
	
	public String postFileRequest(String strUrl, byte[] data, String filePath);
	
	public String getRequest(String url);
	
	public String postParamAndFile(String actionUrl, Map<String, String> params, Map<String, File> files);

	public void cancel();
	
	public int getResponseCode();
	
	public String getResponseMessage();

	public int getRequestErrorCode();

	
}
