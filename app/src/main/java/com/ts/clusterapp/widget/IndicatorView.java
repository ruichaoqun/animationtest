package com.ts.clusterapp.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ts.clusterapp.R;

public class IndicatorView extends View {
    private static int mNormalRadius = 3;
    private static int mSelectRadius = 5;
    private static int mWidth = 15;

    private static int mDivider = 10;

    private Paint mNormalPaint;
    private Paint mSelectPaint;


    private int mCount = 1;

    private int mSelectPosition = 0;

    private int mSelectY = 5;


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
        mNormalPaint.setColor(getContext().getColor(R.color.white));
        mNormalPaint.setStrokeWidth(mNormalRadius);

        mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectPaint.setColor(getContext().getColor(R.color.blue));
        mSelectPaint.setStrokeWidth(mSelectRadius);
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        invalidate();
    }

    public void setCurrentPosition(int position) {
        scrollToPosition(mSelectPosition, position);
        mSelectPosition = position;
    }

    private void scrollToPosition(int oldPosition, int newPosition) {
        int oldX = 5 + oldPosition * mDivider;
        int newX = 5 + newPosition * mDivider;
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
        int height = mDivider * (mCount - 1) + 10;
        setMeasuredDimension(10, height);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < mCount; i++) {
            canvas.drawPoint(5,5+ mDivider * mCount,mNormalPaint);
        }
        canvas.drawPoint(5,mSelectY,mSelectPaint);
    }
}
