package cn.com.sina.share;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;
import cn.com.sina.finance.ShareUtils;
import cn.com.sina.share.impl.OnShareItemSelectedCallback;
import cn.com.sina.share.impl.OnShareItemSelectedListener;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ShareComponent {

	private Context mcontext;
	private static IWXAPI WXAPI;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final int THUMB_SIZE = 150;
	private List<ShareType> shareHiddenTypeList=new ArrayList<ShareType>();

	// IYXAPI 是第三方app和易信通信的openapi接口
	private static IYXAPI YXAPI;
	
	private Dialog mdialog;
	
	private static Tencent tencent;
	private static QQAuth mQqAuth;
	private static QQShare mQQShare;
	private static  IWeiboShareAPI mWeiboShareAPI;
	public ShareComponent(Context context) {

		this.mcontext = context;
		
	}

	/***
	 * 设置分享项显示状态，默认全部为显示
	 * @param shareTypes
	 */
	public void setShareItemHidden(ShareType... shareTypes) {

		if(shareTypes!=null){
			this.shareHiddenTypeList =	 Arrays.asList(shareTypes);
		}
		
	}

	
	public void setShareDialogShow(final Map<ShareType,ShareContentVO>  shareContentMap,final OnShareItemSelectedCallback onCallback){
		if(mcontext == null) {
			
			throw new RuntimeException("setShareDialogShow() context  is empty");
		}
		if(shareContentMap == null){
			throw new RuntimeException("setShareDialogShow() shareContentMap  is empty");
		}
		List<ShareItemVO> shareItemVOs=getShareItemList(mcontext);
		mdialog = ShareDialog.showAlert(mcontext,shareItemVOs, new OnShareItemSelectedListener() {
			
			@Override
			public void onShareItemSelected(ShareItemVO shareItem) {
				// TODO Auto-generated method stub
				if(onCallback!=null){
					boolean isExcute =onCallback.onShareItemSelectedCallBack(shareItem);
					if(isExcute){//如果执行回调方法并且返回true ，则不再执行下面程序
						return ;
					}
				}
				//优先取单个设置的 分享类型对应内容，如果没有选择common类型中的内容
				ShareContentVO shareContent = shareContentMap.get(shareItem.getShareType());
				if(shareContent==null){
					
					shareContent = shareContentMap.get(ShareType.common);
					if(shareContent==null){
						ShareUtils.toast(mcontext, "分享内容为空");
						return;
					}
				}
				try{
					switch (shareItem.shareType) {
						case sina:
							regusterWeiboAPI(mcontext);
							
							sendMessageToSinaWeibo(shareContent);
							break;
		
						case weixin:
							//注册 微信
							registerWXAPI(mcontext);
							boolean sIsWXAppInstalledAndSupported =	isWXAppInstalledAndSupported(mcontext, WXAPI);
							if (!sIsWXAppInstalledAndSupported) {
								ShareUtils.log(ShareComponent.class, "~~~~~~~~~~~~~~微信客户端未安装，请确认");
								ShareUtils.toast(mcontext, "微信客户端未安装，请确认");
								return;
							}
							shareMessageToWX(shareContent,false);
							break;
						case weixin_friend:
							//注册 微信
							registerWXAPI(mcontext);
							
							boolean isSupported =	isWXAppInstalledAndSupported(mcontext, WXAPI);
							if (!isSupported) {
								ShareUtils.log(ShareComponent.class, "~~~~~~~~~~~~~~微信客户端未安装，请确认");
								ShareUtils.toast(mcontext, "微信客户端未安装，请确认");
								return;
							}
							if(!isSupportWXFriend()){
								ShareUtils.log(ShareComponent.class, "~~~~~~~~~~~~~~当前微信客户端不支持朋友圈分享");
								ShareUtils.toast(mcontext, "当前微信客户端版本不支持朋友圈分享");
								return;
							}
							shareMessageToWX(shareContent,true);
							
							break;
						case yixin:
							
							//注册 易信
							registerYXAPI(mcontext);
							sendMessageToYX(shareContent,false);
							
							break;
						case yixin_friend:
							//注册 易信
							registerYXAPI(mcontext);
							sendMessageToYX(shareContent,true);
							break;
							
						case QQ:
							registerQQAPI(mcontext);
							
							shareMessageToQQ(shareContent);
							break;
							
						case QQ_Zone:
							registerQQAPI(mcontext);
							shareMessageToQQZone(shareContent);
							break;
						case email:
							
							shareMessageToEmail(shareContent);
							break;
						default:
							break;
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}
			}

		});
	}
/***
 * 微信  支持图片 文字
 * @param shareItem
 * @param isFriend
 * @throws MalformedURLException
 * @throws IOException
 */
	private void shareMessageToWX(final ShareContentVO shareContent,final boolean isFriend) throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WXMediaMessage msg = new WXMediaMessage();
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				WXWebpageObject appdata = new WXWebpageObject();
				appdata.webpageUrl=shareContent.getLink();
				Bitmap bmp = null;
				if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
					 bmp = BitmapFactory.decodeFile(shareContent.getLocalPicPath());
				 }else if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
						try {
							bmp = BitmapFactory.decodeStream(new URL(shareContent.getPicUrl()).openStream());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				 }
				Bitmap thumbBmp =null;
				if(bmp!=null){
					thumbBmp =getThumbImageBitmap(bmp);
				}
				if(thumbBmp ==null&&shareContent.getLogoResId()!=0){
					thumbBmp = BitmapFactory.decodeResource(
                            mcontext.getResources(),shareContent.getLogoResId());
				}
				if(thumbBmp!=null){
					msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
				}
				msg.mediaObject = appdata;
				msg.setThumbImage(thumbBmp);
				msg.title =shareContent.getTitle();
				msg.description = shareContent.getContent();
				req.transaction = buildTransaction("webpage");
				ShareUtils.log(getClass(),"ShareContent ：：：：：："+ shareContent.getContent());
				req.message = msg;
				req.scene = isFriend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
				WXAPI.sendReq(req);	
				ShareUtils.log(getClass(),"ShareContent ：：：：：："+ req.toString());
			}
		}).start();
		
		
		
		
	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/***
	 * 易信
	 * @param shareContent
	 * @param isFriend
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private void sendMessageToYX(final ShareContentVO shareContent,final boolean isFriend) throws MalformedURLException, IOException{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				// 用YXTextObject对象初始化一个YXMessage对象
				YXMessage msg = new YXMessage();
				SendMessageToYX.Req req = new SendMessageToYX.Req();
				YXWebPageMessageData appdata = new YXWebPageMessageData();
				appdata.webPageUrl = shareContent.getLink();
				Bitmap bmp = null;
				if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
					 bmp = BitmapFactory.decodeFile(shareContent.getLocalPicPath());
				 }else if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
						try {
							bmp = BitmapFactory.decodeStream(new URL(shareContent.getPicUrl()).openStream());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				 }
				Bitmap thumbBmp =null;
				if(bmp!=null){
					thumbBmp =getThumbImageBitmap(bmp);
				}
				if(thumbBmp ==null&&shareContent.getLogoResId()!=0){
					thumbBmp = BitmapFactory.decodeResource(
                            mcontext.getResources(),shareContent.getLogoResId());
				}
				if(thumbBmp!=null){
					msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
				}
				msg.title =shareContent.getTitle();
				// 发送文本类型的消息时，title字段不起作用
				msg.description = shareContent.getContent();
				msg.messageData = appdata;
				// transaction字段用于唯一标识一个请求
				req.transaction = buildTransaction("webpage"); 
				req.message = msg;
				req.scene =isFriend ? SendMessageToYX.Req.YXSceneTimeline
						: SendMessageToYX.Req.YXSceneSession;
		
				if(YXAPI!=null){
					// 调用api接口发送数据到易信
					YXAPI.sendRequest(req);
				}
			}
		}).start();
	}
	/**
	 * 分享到qq
	 * @param shareContent
	 */
	private void shareMessageToQQ(ShareContentVO shareContent) {
		// TODO Auto-generated method stub
		Bundle bundle=new Bundle();
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE,shareContent.getTitle());
		bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareContent.getLink());
		bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY,shareContent.getContent());
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME,mcontext.getString(R.string.app_name));
		if(!TextUtils.isEmpty(shareContent.getAppLogoUrl())){
			 bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareContent.getAppLogoUrl());
		}else if (!TextUtils.isEmpty(shareContent.getLocalPicPath())) {
	    	bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareContent.getLocalPicPath());
	    } else if(!TextUtils.isEmpty(shareContent.getPicUrl())){
	    	bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContent.getPicUrl());
	    }
		
		shareMessageToQQ((Activity)mcontext, bundle,false);
	}
	/**
	 * qq Zone
	 * @param shareContent
	 */
	private void shareMessageToQQZone(ShareContentVO shareContent) {
		// TODO Auto-generated method stub
		  final Bundle params = new Bundle();
          params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
          if(!TextUtils.isEmpty(shareContent.getTitle()))
          params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.getTitle());
          if(!TextUtils.isEmpty(shareContent.getContent()))
          params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getContent());
          if(!TextUtils.isEmpty(shareContent.getLink()))
          params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,shareContent.getLink());
          ArrayList<String> imageUrls = new ArrayList<String>();
          if(!TextUtils.isEmpty(shareContent.getAppLogoUrl()))
        	  imageUrls.add(shareContent.getAppLogoUrl());
          if(!TextUtils.isEmpty(shareContent.getPicUrl()))
        	  imageUrls.add(shareContent.getPicUrl());
          if(!TextUtils.isEmpty(shareContent.getLocalPicPath()))
        	  imageUrls.add(shareContent.getLocalPicPath());
          params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
          
          
          shareMessageToQQ((Activity)mcontext,params,true);
	}
	
	   /**
     * 用异步方式启动分享
     * @param params
     */
    private void shareMessageToQQ(final Activity activity,final Bundle params,boolean isQQZone) {
        if(isQQZone){      
        	tencent.shareToQzone(activity, params, new IUiListener() {

                @Override
                public void onCancel() {
                	 Toast.makeText(activity,R.string.errcode_cancel, 0).show();
                }

                @Override
                public void onError(UiError e) {
                    // TODO Auto-generated method stub
                	Toast.makeText(activity,"onError: " + e.errorMessage, 0).show();
                }

				@Override
				public void onComplete(Object response) {
					// TODO Auto-generated method stub
					Toast.makeText(activity, R.string.errcode_success,0).show();
				}

            });  
        }else{
        	
        	mQQShare.shareToQQ( activity, params, new IUiListener() {

                @Override
                public void onCancel() {
                	 Toast.makeText(activity,R.string.errcode_cancel, 0).show();
                }

                @Override
                public void onComplete(Object response) {
                    // TODO Auto-generated method stub
                   Toast.makeText(activity, R.string.errcode_success,0).show();
                }

                @Override
                public void onError(UiError e) {
                    // TODO Auto-generated method stub
                	 Toast.makeText(activity, "onError: " + e.errorMessage, 0).show();
                }

            });
        }
    }

    /**
     * email
     * @param shareContent
     * @throws IOException 
     * @throws MalformedURLException 
     */
    private void shareMessageToEmail(ShareContentVO shareContent) throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
    	Bitmap bitmap = null;
    	if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
    		bitmap = BitmapFactory.decodeFile(shareContent.getLocalPicPath());
    	}
    	if(!TextUtils.isEmpty(shareContent.getPicUrl())){
    		bitmap = BitmapFactory.decodeStream(new URL(shareContent.getPicUrl()).openStream());
    	}
    	sendInfoByEmail(mcontext,
    			shareContent.getContent()
    			, shareContent.getTitle(), null, bitmap);
	}
    /**
     * weibo
     * @param shareContent
     */
    public void sendMessageToSinaWeibo(final ShareContentVO shareContent){
    	 
		new Thread(new Runnable() {
				
			@Override
			public void run() {
				// TODO Auto-generated method stub	
    	
	    	// 1. 初始化微博的分享消息
		    WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			if(TextUtils.isEmpty(shareContent.getLocalPicPath())&&TextUtils.isEmpty(shareContent.getPicUrl())){
				TextObject textObject = new TextObject();
		        textObject.text =shareContent.getContent();
		        weiboMessage.textObject = textObject;
		        ShareUtils.log(ShareComponent.class,shareContent.getContent());
			}else if(!TextUtils.isEmpty(shareContent.getLocalPicPath())){
				ShareUtils.log(ShareComponent.class,shareContent.getLocalPicPath());
				ImageObject imageObject = new ImageObject();
				Bitmap bmp = BitmapFactory.decodeFile(shareContent.getLocalPicPath());
				Bitmap thumbBmp =null;
				if(bmp!=null){
					imageObject.setImageObject(bmp);
					thumbBmp =getThumbImageBitmap(bmp);
				}
				if(thumbBmp!=null){
					imageObject.setImageObject(thumbBmp);
				}
		        imageObject.setThumbImage(thumbBmp);
				weiboMessage.imageObject =imageObject;
				
			}else if(!TextUtils.isEmpty(shareContent.getPicUrl())){
				ShareUtils.log(ShareComponent.class,shareContent.getPicUrl());
				ImageObject imageObject = new ImageObject();
				Bitmap bmp=null;
				try {
					bmp = BitmapFactory.decodeStream(new URL(shareContent.getPicUrl()).openStream());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap thumbBmp =null;
				if(bmp!=null){
					imageObject.setImageObject(bmp);
					// 这里修改150参数可以测试缩略图质量情况
					thumbBmp = getThumbImageBitmap(bmp);
				}
				if(thumbBmp!=null){
			        imageObject.setThumbImage(thumbBmp);
				}
				weiboMessage.imageObject =imageObject;
		    }
		    // 2. 初始化从第三方到微博的消息请求
		    SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		    // 用transaction唯一标识一个请求
		    request.transaction = String.valueOf(System.currentTimeMillis());
		    request.multiMessage = weiboMessage;
		    if(mWeiboShareAPI!=null)
			    // 3. 发送请求消息到微博，唤起微博分享界面
			    mWeiboShareAPI.sendRequest(request);
		}
		}).start();
    }
    /***
     * 缩略图
     * @param bmp
     * @return
     */
    private static Bitmap getThumbImageBitmap(Bitmap bmp){
    	
    	Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
		bmp.recycle();
		
		return thumbBmp;
    }
    /**
     * 邮件
     * @param context
     * @param sendContent
     * @param subject
     * @param emailReciver
     * @param bitmap
     */
	public void sendInfoByEmail(Context context, String sendContent,
			String subject, String[] emailReciver, Bitmap bitmap) {
		// 系统邮件系统的动作为android.content.Intent.ACTION_SEND
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		// 设置邮件默认地址
		if (emailReciver != null && emailReciver.length != 0) {
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		}
		String type=null;
		if (bitmap != null) {
			type = "image/*";
			emailIntent.setType("message/rfc882");
			// Uri uri = Uri.parse("file://"+attachFilePath);
			Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
					context.getContentResolver(), bitmap, null, null));
			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		emailIntent.setType(type == null ? "plain/text" : type);// 默认为文本
		// 设置邮件默认标题
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		// 设置要默认发送的内容
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sendContent);
		
		try{
			// 调用系统的邮件系统
			context.startActivity(Intent.createChooser(emailIntent, "请选择邮件发送软件"));

		}catch(Exception e){
			Toast.makeText(mcontext, "抱歉，该设备未安装邮件客户端", 0).show();
		}
		bitmap = null;
	}

	/***
	 * 
	 * @param context
	 * @return
	 */
	private  List<ShareItemVO> getShareItemList(Context context){
		
		List<ShareItemVO> shareList=new ArrayList<ShareItemVO>();
		
		if(!shareHiddenTypeList.contains(ShareType.sina)){
			ShareItemVO sinaShareItem=new ShareItemVO(getResStrId(context,R.string.sina_txt),R.drawable.sina_icon,ShareType.sina);
			shareList.add(sinaShareItem);
		}
		if(!shareHiddenTypeList.contains(ShareType.weixin)){
			ShareItemVO weixinShareItem=new ShareItemVO(getResStrId(context,R.string.weixin_txt),R.drawable.weixin_icon,ShareType.weixin);
			shareList.add(weixinShareItem);
		}
		if(!shareHiddenTypeList.contains(ShareType.weixin_friend)){
			ShareItemVO wxFriendShareItem=new ShareItemVO(getResStrId(context,R.string.weixin_friend_txt),R.drawable.weixin_friend_icon,ShareType.weixin_friend);
			shareList.add(wxFriendShareItem);
		}
		if(!shareHiddenTypeList.contains(ShareType.yixin)){
			ShareItemVO yixinShareItem=new ShareItemVO(getResStrId(context,R.string.yixin_txt),R.drawable.yixin_icon,ShareType.yixin);
			shareList.add(yixinShareItem);
		}
		
		if(!shareHiddenTypeList.contains(ShareType.yixin_friend)){
			ShareItemVO yxFriendShareItem=new ShareItemVO(getResStrId(context,R.string.yixin_friend_txt),R.drawable.yixin_friend_icon,ShareType.yixin_friend);
			shareList.add(yxFriendShareItem);
		}
		if(!shareHiddenTypeList.contains(ShareType.QQ)){
			ShareItemVO yxFriendShareItem=new ShareItemVO(getResStrId(context,R.string.qq_txt),R.drawable.qq_icon,ShareType.QQ);
			shareList.add(yxFriendShareItem);
		}
		
		if(!shareHiddenTypeList.contains(ShareType.QQ_Zone)){
			ShareItemVO yxFriendShareItem=new ShareItemVO(getResStrId(context,R.string.qq_zone_txt),R.drawable.qq_zone_icon,ShareType.QQ_Zone);
			shareList.add(yxFriendShareItem);
		}
		if(!shareHiddenTypeList.contains(ShareType.email)){
			ShareItemVO emailShareItem=new ShareItemVO(getResStrId(context,R.string.email_txt),R.drawable.email_icon,ShareType.email);
			shareList.add(emailShareItem);
		}
		return shareList;
	}
	
	private  String getResStrId(Context context,int resId){
		
		return context.getString(resId);
	}
	
	/***
	 * 微信
	 * @param context
	 */
	public static void registerWXAPI(Context context){
		if(WXAPI==null){
			WXAPI = WXAPIFactory.createWXAPI(context, Constants.APP_WX_ID, false);
			WXAPI.registerApp(Constants.APP_WX_ID);    	
		}
	}
	/***
	 * 易信
	 * @param context
	 */
	public static void registerYXAPI(Context context){
		if(YXAPI==null){
			// 通过YXAPIFactory工厂，获取IYXAPI的实例
			YXAPI = YXAPIFactory.createYXAPI(context,Constants.YX_APP_KEY);
			// 将该app注册到易信
			YXAPI.registerApp();
		}
	}
	/**
	 * 新浪微博
	 * @param context
	 */
	public void regusterWeiboAPI(final Context context){
		if(mWeiboShareAPI==null){
			 // 创建微博分享接口实例
	        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, Constants.APP_WEIBO_KEY);
	        
	        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
	        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
	        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
	        mWeiboShareAPI.registerApp();
	        
	        // 如果未安装微博客户端，设置下载微博对应的回调
	        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
	            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
	                @Override
	                public void onCancel() {
	                    Toast.makeText(context, 
	                            R.string.cancel_download_weibo, 
	                            Toast.LENGTH_SHORT).show();
	                }
	            });
	        }
		}
	}
	/***
	 * 腾讯qq qq空间
	 * @return
	 */
	public static void registerQQAPI(Context context){
		
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		if(mQqAuth==null){
			
			mQqAuth = QQAuth.createInstance(Constants.APP_QQ_ID, context.getApplicationContext());
		}
		if(tencent==null){
			
			tencent = Tencent.createInstance(Constants.APP_QQ_ID, context);
		}
		mQQShare = new QQShare(context, mQqAuth.getQQToken());
	}
	
	
	
	public static IWXAPI getWXAPI() {
		return WXAPI;
	}

	public static IYXAPI getYXAPI() {
		return YXAPI;
	}

	/***
	 * 是否支持朋友圈
	 * @return
	 */
	private boolean isSupportWXFriend(){
		if(WXAPI==null){
			return false;
		}
		int wxSdkVersion = WXAPI.getWXAppSupportAPI();
		return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
	}
	/**
	 * 判断是否安装微信
	 * @param context
	 * @param api
	 * @return
	 */
	private boolean isWXAppInstalledAndSupported(Context context,
			IWXAPI api) {
		// LogOutput.d(TAG, "isWXAppInstalledAndSupported");
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
				&& api.isWXAppSupportAPI();

		return sIsWXAppInstalledAndSupported;
	}

	public Dialog getMdialog() {
		return mdialog;
	}

	public static IWeiboShareAPI getmWeiboShareAPI() {
		return mWeiboShareAPI;
	}
	
}