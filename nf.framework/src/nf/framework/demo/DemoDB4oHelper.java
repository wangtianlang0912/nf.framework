/**   
 * @Title: DemoDB4oHelper.java 
 * @Package com.example.edittexttest.demo 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author niufei
 * @date 2014-5-13 ����3:58:56 
 * @version V1.0   
*/
package nf.framework.demo;

import java.io.File;
import java.io.IOException;

import nf.framework.core.db4o.AbstractDB4oHelper;
import android.content.Context;


public class DemoDB4oHelper extends AbstractDB4oHelper<DemoVO>{

	private static DemoDB4oHelper db4oHelper;
	private Context mcontext;
	/**
	 * @param ctx
	 */
	public DemoDB4oHelper(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
		this.mcontext=ctx;
	}
	
	public static DemoDB4oHelper getIntance(Context ctx){
		
		if(db4oHelper==null){
			db4oHelper=new DemoDB4oHelper(ctx);
		}
		return db4oHelper;
	}
	
	/* (non-Javadoc)
	 * @see com.example.edittexttest.db4o.AbstractDB4oHelper#getClassT()
	 */
	@Override
	protected Class<DemoVO> getClassT() {
		// TODO Auto-generated method stub
		return DemoVO.class;
	}

	/* (non-Javadoc)
	 * @see com.example.edittexttest.db4o.AbstractDB4oHelper#setObjectIndexedField()
	 */
	@Override
	protected String setObjectIndexedField() {
		// TODO Auto-generated method stub
		return DemoVO.Column_ID;
	}

	/* (non-Javadoc)
	 * @see com.example.edittexttest.db4o.AbstractDB4oHelper#getDbFilePath()
	 */
	@Override
	protected String getDbFilePath() {
		// TODO Auto-generated method stub
		String filePath= mcontext.getCacheDir().getPath()+File.separator+"demo.db4o";
		if(!new File(filePath).exists()){
			try {
				new File(filePath).createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filePath;
	}

	/* (non-Javadoc)
	 * @see com.example.edittexttest.db4o.AbstractDB4oHelper#hasChangedFilePath()
	 */
	@Override
	protected boolean hasChangedFilePath() {
		// TODO Auto-generated method stub
		return false;
	}

}
