package com.wollenstein.derek.amazingrecipeapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.wollenstein.derek.amazingrecipeapp.R;

/**
 * Class representing some sort of item of food.
 * // TODO: Support foods that are not banana-shaped
 */
public class FoodView extends View {

    private Path mBodyPath;

    private enum FoodShape {
        mystery, banana
    }

    private final FoodShape foodShape;
    private Paint mRibPaint;
    private Paint mSkinPaint;

    public FoodView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray foodViewAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FoodView,
                0, 0);

        try {
            TypedValue foodType = new TypedValue();
            if (foodViewAttrs.getValue(R.styleable.FoodView_foodType, foodType)) {
                foodShape = foodType.data == 1 ? FoodShape.banana : FoodShape.mystery;
            } else {
                foodShape = FoodShape.mystery;
            }
        } finally {
            foodViewAttrs.recycle();
        }

        initShapes();
    }

    private void initShapes() {
        mSkinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSkinPaint.setColor(getResources().getColor(R.color.banana_yellow));
        mSkinPaint.setStyle(Paint.Style.FILL);

        mRibPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRibPaint.setColor(getResources().getColor(R.color.black));
        mRibPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float left = getFromLeft(w, 0.2f);
        float right = getFromLeft(w, 0.4f);
        mBodyPath = new Path();
        mBodyPath.setFillType(Path.FillType.EVEN_ODD);
        // The arc created is defined by the bounds of the oval the arc is from.
        // Hence, to raise or lower the top, we must also raise and lower the bottom if we want
        // and identical y position
        mBodyPath.addArc(new RectF(left, getFromTop(h, 0.2f), right, getFromTop(h, 0.8f)), 180, 180);
        mBodyPath.addArc(new RectF(left, getFromTop(h, 0.4f), right, getFromTop(h, 0.6f)), 0, -180);
    }

    private float getFromLeft(int width, float percentage) {
        int usableWidth = width - getPaddingLeft() - getPaddingRight();
        return getPaddingLeft() + usableWidth * percentage;
    }

    private float getFromRight(int width, float percentage) {
        int usableWidth = width - getPaddingLeft() - getPaddingRight();
        return width - getPaddingRight() - usableWidth * percentage;
    }

    private float getFromBottom(int height, float percentage) {
        int usableHeight = height - getPaddingTop() - getPaddingBottom();
        return height - getPaddingBottom() - usableHeight * percentage;
    }

    private float getFromTop(int height, float percentage) {
        int usableHeight = height - getPaddingTop() - getPaddingBottom();
        return getPaddingTop() + usableHeight * percentage;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mBodyPath, mSkinPaint);
        // TODO: Complete a banana, or any other shape.
    }
}