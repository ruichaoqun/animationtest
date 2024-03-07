package com.mercedes.cluster.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mercedes.cluster.R;


public class IndicatorView extends View {
    private static int mNormalRadius = 5;
    private static int mSelectRadius = 8;

    private static int mDivider = 24;

    private Paint mNormalPaint;
    private Paint mSelectPaint;


    private int mCount = 1;

    private int mSelectPosition = 0;

    private int mSelectY = mSelectRadius;

    private Handler mHandler;


    public IndicatorView(Context context) {
        super(context);
        init();
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalPaint.setStyle(Paint.Style.FILL);
        mNormalPaint.setColor(getContext().getColor(R.color.white));

        mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectPaint.setStyle(Paint.Style.FILL);
        mSelectPaint.setColor(getContext().getColor(R.color.blue));
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                setVisibility(View.INVISIBLE);
            }
        };
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        invalidate();
    }

    public void setCurrentPosition(int position) {
        scrollToPosition(mSelectPosition, position);
        mSelectPosition = position;
        setVisibility(View.VISIBLE);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1,5000);
    }

    private void scrollToPosition(int oldPosition, int newPosition) {
        int oldX = mSelectRadius + oldPosition * mDivider;
        int newX = mSelectRadius + newPosition * mDivider;
        ValueAnimator animator = ObjectAnimator.ofInt(oldX,newX);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            mSelectY = (int) animation.getAnimatedValue();
            postInvalidate();
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mDivider * (mCount - 1) + mSelectRadius * 2;
        setMeasuredDimension(mSelectRadius * 2, height);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < mCount; i++) {
            canvas.drawCircle(mSelectRadius,mSelectRadius + mDivider * i,mNormalRadius,mNormalPaint);
        }
        canvas.drawCircle(mSelectRadius,mSelectY,mSelectRadius,mSelectPaint);
    }
}
