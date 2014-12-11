/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
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
package nf.framework.expand.widgets.zoomPhotoView;

import java.util.List;

import nf.framework.expand.widgets.zoomPhotoView.PhotoViewAttacher.OnMatrixChangedListener;
import nf.framework.expand.widgets.zoomPhotoView.PhotoViewAttacher.OnPhotoTapListener;
import nf.framework.expand.widgets.zoomPhotoView.PhotoViewAttacher.OnViewTapListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class PhotoView extends ImageView implements IPhotoView {

	private final PhotoViewAttacher mAttacher;

	private ScaleType mPendingScaleType;

	private boolean isShowTag=true;
	private Paint paint;
	private int innerCircle=3;
	private int ringWidth=1;
	private int rectHeight=20;
	private int padding=5;
	private int rectWidth =30;
	private RectF currentDrawRectF;
	private List<ImageTagVO> imageTagList ;
	private OnViewTapListener onViewTapListener;
	private OnImageTagItemClickListener onImageTagItemClickListener;
	private OnMatrixChangedListener onMatrixChangedListener;
	public PhotoView(Context context) {
		this(context, null);
	}

	public PhotoView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}
	
	public PhotoView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		super.setScaleType(ScaleType.MATRIX);
		mAttacher = new PhotoViewAttacher(this);

		if (null != mPendingScaleType) {
			setScaleType(mPendingScaleType);
			mPendingScaleType = null;
		}
		innerCircle = dip2px(getContext(),3); //设置内圆半径  
        ringWidth = dip2px(getContext(),1); //设置圆环宽度  
        rectHeight= dip2px(getContext(),20);
        rectWidth =dip2px(getContext(), 40);
        padding=dip2px(getContext(), 5);
        
		this.paint = new Paint();  
        this.paint.setAntiAlias(true); //消除锯齿  
	    this.paint.setStyle(Paint.Style.STROKE); //绘制空心圆   
	    
	    mAttacher.setOnViewTapListener(new OnViewTapListener() {
			
			@Override
			public void onViewTap(View view, float x, float y) {
				// TODO Auto-generated method stub
				boolean flag =whichTxtView(x,y);
				if(!flag&&onViewTapListener!=null){
					onViewTapListener.onViewTap(view, x, y);
				}
			}
		});
	    mAttacher.setOnMatrixChangeListener(new OnMatrixChangedListener() {
			
			@Override
			public void onMatrixChanged(RectF rect) {
				// TODO Auto-generated method stub
				currentDrawRectF=rect;
				postInvalidate();
				if(onMatrixChangedListener!=null){
					onMatrixChangedListener.onMatrixChanged(rect);
				}
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	
		if(imageTagList!=null&&isShowTag()){
			for(ImageTagVO imageTag :imageTagList){
				drawRing(canvas,this.paint,imageTag);
			}
		}
	}
	
	private void drawRing(Canvas canvas,Paint paint,ImageTagVO imageTag){
		
        if(currentDrawRectF==null){
    	   return;
        }
        float circleCenterX =currentDrawRectF.left+imageTag.getX()*getScale();
        float circleCenterY =currentDrawRectF.top+imageTag.getY()*getScale();
        //绘制内圆  
		paint.setARGB(155, 167, 190, 206);  
		paint.setStrokeWidth(2);  
        canvas.drawCircle(circleCenterX,circleCenterY, innerCircle, paint);  
          
        //绘制圆环  
        paint.setARGB(255, 212 ,225, 233);  
        paint.setStrokeWidth(ringWidth);  
        canvas.drawCircle(circleCenterX,circleCenterY, innerCircle+1+ringWidth/2, paint);  
          
        //绘制外圆  
        paint.setARGB(155, 167, 190, 206);  
        paint.setStrokeWidth(2);  
        canvas.drawCircle(circleCenterX,circleCenterY, innerCircle+ringWidth,paint); 
        
        
        Paint textPaint = new Paint( Paint.ANTI_ALIAS_FLAG);  
        textPaint.setTextSize(sp2px(getContext(), 14));  
        textPaint.setColor(Color.LTGRAY);  
        canvas.drawText(imageTag.getName(),circleCenterX+innerCircle+ringWidth+padding
        		,circleCenterY+sp2px(getContext(), 5), textPaint);  
       
        paint.setStrokeWidth(ringWidth);
        Rect rect=new Rect((int)circleCenterX+innerCircle+ringWidth
        		,(int)circleCenterY-rectHeight/2
        		, (int)circleCenterX+innerCircle+ringWidth+rectWidth
        		, (int)circleCenterY+rectHeight/2);
        canvas.drawRect(rect, paint);
        imageTag.setRect(rect);
	}
	
	
	@Override
	public boolean canZoom() {
		return mAttacher.canZoom();
	}

	@Override
	public RectF getDisplayRect() {
		return mAttacher.getDisplayRect();
	}

	@Override
	public float getMinScale() {
		return mAttacher.getMinScale();
	}

	@Override
	public float getMidScale() {
		return mAttacher.getMidScale();
	}

	@Override
	public float getMaxScale() {
		return mAttacher.getMaxScale();
	}

	@Override
	public float getScale() {
		return mAttacher.getScale();
	}

	@Override
	public ScaleType getScaleType() {
		return mAttacher.getScaleType();
	}

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
	public void setMinScale(float minScale) {
		mAttacher.setMinScale(minScale);
	}

	@Override
	public void setMidScale(float midScale) {
		mAttacher.setMidScale(midScale);
	}

	@Override
	public void setMaxScale(float maxScale) {
		mAttacher.setMaxScale(maxScale);
	}

	@Override
	// setImageBitmap calls through to this method
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	public void setImageTagList(List<ImageTagVO> imageTagList){
		
		this.imageTagList =imageTagList;
	}
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		this.onMatrixChangedListener=listener;
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mAttacher.setOnLongClickListener(l);
	}

	@Override
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mAttacher.setOnPhotoTapListener(listener);
	}

	@Override
	public void setOnViewTapListener(OnViewTapListener listener) {
		this.onViewTapListener =listener;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (null != mAttacher) {
			mAttacher.setScaleType(scaleType);
		} else {
			mPendingScaleType = scaleType;
		}
	}

	@Override
	public void setZoomable(boolean zoomable) {
		mAttacher.setZoomable(zoomable);
	}

	@Override
	public void zoomTo(float scale, float focalX, float focalY) {
		mAttacher.zoomTo(scale, focalX, focalY);
	}

	public boolean isShowTag() {
		return isShowTag;
	}

	public void setShowTag(boolean isShowTag) {
		this.isShowTag = isShowTag;
		postInvalidate();
	}

	@Override
	protected void onDetachedFromWindow() {
		mAttacher.cleanup();
		super.onDetachedFromWindow();
	}
	 @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
		 // 获取点击屏幕时的点的坐标   
         float x = event.getX();   
         float y = event.getY();   
		 
         whichTxtView(x,y);
		 
		 try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	 
	 private boolean whichTxtView(float x, float y) {
		// TODO Auto-generated method stub

		if(imageTagList!=null&&onImageTagItemClickListener!=null){
			for (ImageTagVO imageTag:imageTagList) {
				Rect rect =imageTag.getRect();
				if(rect!=null){
					if(x>rect.left&&x<rect.right&&y>rect.top&&y<rect.bottom){
						onImageTagItemClickListener.onImageTagItemClicked(imageTag);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** 
	     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
    
    public void setOnImageTagItemClickListener(
			OnImageTagItemClickListener onImageTagItemClickListener) {
		this.onImageTagItemClickListener = onImageTagItemClickListener;
	}

	public interface OnImageTagItemClickListener{
    	
    	public void onImageTagItemClicked(ImageTagVO imageTag);
    }
}