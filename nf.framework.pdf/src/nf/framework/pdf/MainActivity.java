package nf.framework.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;

import com.artifex.mupdfdemo.MuPDFActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DealNetworkOnMainThreadException();
		String pdffilename =Environment.getExternalStorageDirectory().getPath()+File.separator+"b.pdf";
		InitAssetsFileToSDCard(this, "b.pdf", pdffilename);
		//http://1.caijiatong.sinaapp.com/b.pdf
		Uri uri = Uri.parse(pdffilename);
		Intent intent = new Intent(this,MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		startActivity(intent);
	}
	
	/**
	 * 将Assets中的初始文件保存到指定目录下
	 * 
	 * @param assetsFileName
	 *            Assets中文件的名称
	 * @param fileSavePath
	 *            转储的文件路径文件名称
	 */
	public boolean InitAssetsFileToSDCard(Context mcontext,
			String assetsFileName, String fileSavePath) {
		if (assetsFileName == null) {
			return false;
		}
		InputStream inputStream = null;
		try {
			inputStream = mcontext.getResources().getAssets()
					.open(assetsFileName);
			SaveInputStreamToFile(inputStream, fileSavePath);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				inputStream.close();
				inputStream = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
		}
		return true;
	}
	
	public void SaveInputStreamToFile(InputStream inputStream,
			String fileSavePath) throws Exception {

		int len = 4096;//
		int readCount = 0, readSum = 0;
		byte[] buffer = new byte[len];
		FileOutputStream fos = new FileOutputStream(fileSavePath);
		while ((readCount = inputStream.read(buffer)) != -1) {
			readSum += readCount;
			fos.write(buffer, 0, readCount);
		}
		fos.flush();
		fos.close();
	}
	@SuppressLint("NewApi")
	private void DealNetworkOnMainThreadException(){
		if(Build.VERSION.SDK_INT<=10){
			return;
		}
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
//		.penaltyLog()
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//		.detectLeakedSqlLiteObjects()
		.detectLeakedClosableObjects()
//		.penaltyLog()
		.penaltyDeath().build());
	}
}
