package nf.framework.core.http;

public interface HttpRequestInterface{

	
	public String bulidUrl();
	

	public void onRequestCompleted(String responseData);
	
	
	public void onRequestFailured(String requestErrorMsg);
}
