/**   
 * @Title: StaticEventInterface.java 
 * @Package com.ApricotforestStatistic 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-3-18 ����1:51:05 
 * @version V1.0   
*/
package nf.framework.statistic.service;

import android.content.Context;

interface ApricotEventAgentInterface {

	public void onEvent (Context mcontext,String eventName,int userId,String relatedParam);
	
}
