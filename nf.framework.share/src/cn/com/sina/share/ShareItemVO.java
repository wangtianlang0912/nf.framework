package cn.com.sina.share;

import java.io.Serializable;

public class ShareItemVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1430889348566951238L;

	String title;
	
	int resId;
	
	ShareType shareType;
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public ShareType getShareType() {
		return shareType;
	}

	public void setShareType(ShareType shareType) {
		this.shareType = shareType;
	}

	public ShareItemVO(String title, int resId, ShareType shareType) {
		super();
		this.title = title;
		this.resId = resId;
		this.shareType = shareType;
	}

	
	
	
	public ShareItemVO() {
		super();
	}
	
}
