/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nf.framework.app;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import nf.framework.core.AbsApplication;
import nf.framework.core.exception.LogUtil;
import nf.framework.core.exception.CrashHandler.CrashMessageCallBack;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class UILApplication extends AbsApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
		LogUtil.OpenBug=isOpenBug();
	}

	public  void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
//		ImageLoaderConfiguration config1 = new ImageLoaderConfiguration.Builder(context)
//				.threadPriority(Thread.NORM_PRIORITY - 2)
//				.denyCacheImageMultipleSizesInMemory()
//				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs(isOpenBug())
//				.build();
//		
		
		HttpParams params = new BasicHttpParams();
        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        // Default connection and socket timeout of 10 seconds. Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
        HttpConnectionParams.setSoTimeout(params, 10 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // Don't handle redirects -- return them to the caller. Our code
        // often wants to re-POST after a redirect, which we must do ourselves.
        HttpClientParams.setRedirecting(params, false);
        // Set the specified user agent and register standard protocols.
        HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);


        ImageLoaderConfiguration config =   new ImageLoaderConfiguration.Builder(context)
        .threadPoolSize(1)
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .memoryCache(new WeakMemoryCache())
        .writeDebugLogs(isOpenBug())
        .imageDownloader(new HttpClientImageDownloader(this,new DefaultHttpClient(manager, params)))
        .build();
        
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	@Override
	public CrashMessageCallBack getCrashMessageCallBack() {
		// TODO Auto-generated method stub
		return null;
	}
}