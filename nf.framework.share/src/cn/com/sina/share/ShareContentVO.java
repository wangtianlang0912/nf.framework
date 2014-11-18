package cn.com.sina.share;

import java.io.Serializable;

public class ShareContentVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3699576843042534935L;

	String content;
	
	String title;
	
	String picUrl;
	
	String localPicPath;
	
	String link;
	
	String appLogoUrl;
	
	int logoResId;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getLocalPicPath() {
		return localPicPath;
	}

	public void setLocalPicPath(String localPicPath) {
		this.localPicPath = localPicPath;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAppLogoUrl() {
		return appLogoUrl;
	}

	public void setAppLogoUrl(String appLogoUrl) {
		this.appLogoUrl = appLogoUrl;
	}

	public int getLogoResId() {
		return logoResId;
	}

	public void setLogoResId(int logoResId) {
		this.logoResId = logoResId;
	}

	
}
