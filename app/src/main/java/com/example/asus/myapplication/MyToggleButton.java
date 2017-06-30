package com.example.asus.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyToggleButton extends View {

	private Bitmap slideBG;
	private Bitmap switchBG;
	private float downX;
	private float left;
	private int max;
	private long startTime;

	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		slideBG = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button_background);
		switchBG = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
		max = switchBG.getWidth() - slideBG.getWidth();
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.mtb);
		isOpen=ta.getBoolean(R.styleable.mtb_isopen, false);
		left=isOpen?max:0;
		ta.recycle();
		
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 设置最终的尺寸
		setMeasuredDimension(switchBG.getWidth(), switchBG.getHeight());
	}


	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	// 4,绘制
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(switchBG, 0, 0, null);
		canvas.drawBitmap(slideBG, left, 0, null);
	}
	private boolean isOpen;
	private boolean isChange;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isChange=isOpen;
			downX = event.getX();
			startTime = SystemClock.uptimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getX();
			float dX = moveX - downX;
			left += dX;
			if (left < 0) {
				left = 0;
			}
			if (left > max) {
				left = max;
			}

			invalidate();

			downX = moveX;
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) event.getX();
			if (SystemClock.uptimeMillis() - startTime < 1000 && Math.abs(upX - downX) < 5) {

				if(!isOpen){
					if(upX>slideBG.getWidth()&&upX<switchBG.getWidth()){
						isOpen=true;
						left=max;
					}else{
						left=0;
					}
					invalidate();
				}else{
					if(upX>0&&upX<max){
						isOpen=false;
						left=0;
					}else{
						left=max;
					}
					invalidate();
				}
			} else {

				if (left > max / 2) {
					left = max;
					isOpen=true;
				} else {
					left = 0;
					isOpen=false;
				}
				invalidate();
			}
			
//			isChange=!((isChange&&isOpen)||(!isChange&&!isOpen));
			isChange=isChange^isOpen;
			
			
			if(isChange&&listener!=null){
				listener.onToggleButtonChange(isOpen);
			}
			break;

		default:
			break;
		}
		return true;
	}
	private OnToggleButtonChangeListener listener;
	public interface OnToggleButtonChangeListener{
		void onToggleButtonChange(boolean isOpen);
	}
	
	public void setOnToggleButtonChangeListener(OnToggleButtonChangeListener listener){
		this.listener=listener;
	}

}
