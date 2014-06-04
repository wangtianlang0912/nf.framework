package nf.framework.httprequest;

public interface HttpRequestInterface{

	
	public String bulidUrl();
	

	public void onRequestCompleted(String responseData);
	
	
	public void onRequestFailured(String requestErrorMsg);
}
