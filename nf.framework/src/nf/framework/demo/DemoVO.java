/**   
 * @Title: DemoVO.java 
 * @Package com.example.apricotforest_commontest 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-8 ����12:28:49 
 * @version V1.0   
*/
package nf.framework.demo;

import java.io.Serializable;

public class DemoVO implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(��һ�仰�������������ʾʲô) 
	*/ 
	public static final String Column_ID="id";
	
	private static final long serialVersionUID = 1L;
	public String id;
	String text;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
