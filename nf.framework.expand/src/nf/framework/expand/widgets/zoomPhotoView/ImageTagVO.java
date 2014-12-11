package nf.framework.expand.widgets.zoomPhotoView;

import java.io.Serializable;

import android.graphics.Rect;

public class ImageTagVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7375145002634100419L;

	String name;
	
	int x,y;
	
	int style;
	
	String link;
	
	Rect rect;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}
	
	
}
