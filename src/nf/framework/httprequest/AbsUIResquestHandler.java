/**   
 * @Title: AbsUIResquestHandler.java 
 * @Package com.example.edittexttest.httprequest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-13 ����9:51:41 
 * @version V1.0   
*/
package nf.framework.httprequest;

public interface AbsUIResquestHandler<T> {

	
	public void onPreExcute();
	
	public void onSuccessPostExecute(T object);
	
	public void onFailurePostExecute(String failureMsg);
	
	public void onCompleteExcute();
}
