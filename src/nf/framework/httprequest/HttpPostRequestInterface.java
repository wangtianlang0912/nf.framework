package nf.framework.httprequest;

import java.util.Map;

public interface HttpPostRequestInterface extends HttpRequestInterface{

	
	public Map<String, String> getPostParamMap();
}
