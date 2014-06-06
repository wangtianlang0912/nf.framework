/**   
 * @Title: StatisticExtandDataCallBack.java 
 * @Package com.ApricotforestStatistic.sqlite 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author niufei
 * @date 2014-3-26 下午2:40:29 
 * @version V1.0   
*/
package nf.framework.statistic.sqlite;

import nf.framework.statistic.VO.StatisticExtandVO;

public interface StatisticExtandDataCallBack {

	public StatisticExtandVO extandDataCallBack(String statisticDbFilePath);
	
	public void afterStatisticDataCompleted(String extandFilePath);

}
