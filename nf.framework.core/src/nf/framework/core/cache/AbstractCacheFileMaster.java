package nf.framework.core.cache;

import java.io.File;
import java.util.List;

import nf.framework.core.util.io.FileUtils;
import android.content.Context;

public abstract class AbstractCacheFileMaster {
	
	protected abstract void saveToCacheFile(String jsonData,String fileName);
	
	protected abstract String readCacheFile(String fileName);
	
	protected abstract boolean deleteCacheFileByName(String fileName);
	
	protected abstract boolean deleteAllCacheFiles();
	
	protected abstract List<String> queryAllCacheFileNameList();
	
	/**
	 * ���浽ϵͳ�ļ��� /data/data/...
	 * @param context
	 * @param jsonData
	 * @param fileFolderName
	 * @param fileName
	 * @throws Exception
	 */
	protected void SaveToSysFile(Context context,String jsonData,String fileFolderName,String fileName) throws Exception{
		
		if(jsonData==null){
			return;
		}
		String fileFolderPath=getSysSaveFilePath(context, fileFolderName);
		FileUtils fileUtils=FileUtils.getInstance();
		if(!fileUtils.isAbsolutePackageExist(fileFolderPath)){
			fileUtils.createAbsoluteDir(fileFolderPath);
		}
		String CacheFilePath=fileFolderPath+File.separator+fileName;
		saveDataToFile(CacheFilePath, jsonData);
		
	}
	/***
	 * ������ݵ��ļ�
	 * @param CacheFilePath
	 * @param jsonData
	 * @throws Exception 
	 */
	private void saveDataToFile(String CacheFilePath,String jsonData) throws Exception{
		
		//create log file
		File CacheFile=new File(CacheFilePath);		
		if(!CacheFile.exists()){
			CacheFile.createNewFile();
		}
		FileUtils fileUtils=FileUtils.getInstance();
		fileUtils.write(CacheFile, jsonData);
	}
	/***
	 * ��ȡ�����ļ�����
	 * @param context
	 * @param fileName
	 * @param fileFolderName
	 * @param isSdCard
	 * @return
	 * @throws Exception 
	 */
	protected String readCacheFile(Context context,String fileName,String fileFolderName) throws Exception {
		// TODO Auto-generated method stub
		if(fileName==null){
			return null;
		}
		String	fileFolderPath=getSysSaveFilePath(context, fileFolderName);
		if(new File(fileFolderPath).exists()){
			String CacheFilePath=fileFolderPath+File.separator+fileName;
			File CacheFile=new File(CacheFilePath);
			if(CacheFile.exists()){
				return FileUtils.getInstance().read(CacheFile);
			}
		}
		return null;
	}
	/***
	 * ɾ��ָ���ļ�
	 * @param context
	 * @param fileName
	 * @param fileFolderName
	 * @param isSdCard
	 * @return
	 */
	protected boolean deleteCacheFileByName(Context context,String fileName,String fileFolderName) {
		// TODO Auto-generated method stub
		if(fileName==null){
			return false;
		}
		String	fileFolderPath=getSysSaveFilePath(context, fileFolderName);
		if(new File(fileFolderPath).exists()){
			String CacheFilePath=fileFolderPath+File.separator+fileName;
			File CacheFile=new File(CacheFilePath);
			if(CacheFile.exists()){
				return FileUtils.getInstance().deleteFile(CacheFilePath);
			}
		}
		return false;
	}
	/***
	 * ������еı����ļ�
	 * @param context
	 * @param isSdCard  �ж��Ƿ�sd���в���
	 * @param fileFolderName
	 * @return
	 */
	public boolean deleteAllCacheFile(Context context,String fileFolderName){
		
		String	fileFolderPath=getSysSaveFilePath(context, fileFolderName);
		if(new File(fileFolderPath).exists()){
			FileUtils.getInstance().deleteDirectory(fileFolderPath);
		}
		return true;
	}
	/***
	 * ����ָ���ļ����е�ȫ���ļ����
	 * @param context
	 * @param fileFolderName
	 * @param isSdCard
	 * @return
	 */
	public List<String> queryAllCacheFile(Context context,String fileFolderName){
		
		String	fileFolderPath=getSysSaveFilePath(context, fileFolderName);
		if(new File(fileFolderPath).exists()){
			return 	FileUtils.getInstance().queryFileNameList(fileFolderPath);
		}
		return null;
	}
	
	/**
	 *
	 * @return
	 */
	protected String getSysSaveFilePath(Context context,String fileFolderName){
		
		return context.getCacheDir().getPath()+File.separator+fileFolderName;
	}
}
