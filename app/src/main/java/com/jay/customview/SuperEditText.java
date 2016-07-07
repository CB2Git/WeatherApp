package com.jay.customview;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.jay.locationselect.R;

public class SuperEditText extends EditText {

	private Drawable mSearch;
	private Drawable mCancel;

	private boolean mShowCancel = false;
	private int mBound = 0;

	private OnCancelClickListener mListener;

	public SuperEditText(Context context) {
		super(context);
		loadIcon();
	}

	public SuperEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadIcon();
	}

	public SuperEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadIcon();
	}

	// 加载图片
	private void loadIcon() {
		mSearch = getResources().getDrawable(R.drawable.search);
		mCancel = getResources().getDrawable(R.drawable.cancel);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		mBound = Math.min(heightSize, widthSize) - 10;
		mSearch.setBounds(0, 0, mBound, mBound);
		mCancel.setBounds(0, 0, mBound - 10, mBound - 10);
		setPadding(0, 0, 5, 0);
		showCancelIcon(mShowCancel);
	}

	public void showCancelIcon(boolean show) {
		mShowCancel = show;
		if (mShowCancel) {
			setCompoundDrawables(mSearch, null, mCancel, null);
		} else {
			setCompoundDrawables(mSearch, null, null, null);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		RectF rect = new RectF(0, 0, getMeasuredWidth() - mBound, mBound);
		if (event.getAction() == MotionEvent.ACTION_UP && mShowCancel
				&& !rect.contains(x, y) && mListener != null) {
			mListener.OnCancelClick();
			Log.v("x", "cancel dianji");
			setCompoundDrawables(mSearch, null, null, null);
		}
		return super.onTouchEvent(event);
	}

	public interface OnCancelClickListener {
		public void OnCancelClick();
	}

	public void setOnCancelClickListener(OnCancelClickListener listener) {
		mListener = listener;
	}
}
