package nf.framework.video.mainui;


import nf.framework.video.utils.AppLog;
import nf.framework.video.utils.AppManager;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;


public abstract class BaseActivity extends FragmentActivity{
    private String TAG="BaseActivity";

    //
    private boolean mAllowFullScreen = true;
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }
	//Activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.debug(TAG, "---------onCreate ");
        // 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        }
        AppManager.getAppManager().addActivity(this);
    }
  //
    @Override  
    protected void onStart() {  
        super.onStart();  
        AppLog.logi(TAG, "onStart called.");  
    }  
      
    //Activity
    @Override  
    protected void onRestart() {  
        super.onRestart();  
        AppLog.logi(TAG, "onRestart called.");  
    }  
      
    //Activity
    @Override  
    protected void onResume() {  
        super.onResume();  
        AppLog.logi(TAG, "onResume called.");  
    }  
      
    //Activity
    /*@Override 
    public void onWindowFocusChanged(boolean hasFocus) { 
        super.onWindowFocusChanged(hasFocus); 
        Log.i(TAG, "onWindowFocusChanged called."); 
    }*/  
      
    //Activity
    @Override  
    protected void onPause() {  
        super.onPause();  
        AppLog.logi(TAG, "onPause called.");  
        //
    }  
      
    //Activity
    @Override  
    protected void onStop() {  
        super.onStop();  
        AppLog.logi(TAG, "onStop called.");     
    }  
      
    //Activity
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        AppLog.logi(TAG, "onDestory called.");  
    }  
      
    /** 
     * Activity
     */  
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);  
    }  
      
    /** 
     * Activity
    
     */  
    @Override  
    protected void onRestoreInstanceState(Bundle savedInstanceState) {  
        super.onRestoreInstanceState(savedInstanceState);  
    } 
    //
    @Override  
    public void onConfigurationChanged(Configuration newConfig) {  
        super.onConfigurationChanged(newConfig);  
        AppLog.logi(TAG, "onConfigurationChanged called.");  
        switch (newConfig.orientation) {  
        case Configuration.ORIENTATION_PORTRAIT:  
            //setContentView(R.layout.orientation_portrait);  
            break;  
        case Configuration.ORIENTATION_LANDSCAPE:  
            //setContentView(R.layout.orientation_landscape);  
            break;  
        }  
    }  
}
