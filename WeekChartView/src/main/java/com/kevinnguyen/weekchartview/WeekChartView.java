package com.kevinnguyen.weekchartview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * WeekChartView
 */
public class WeekChartView extends View implements WeekChartViewInterface {

    // default color
    private static final int DEFAULT_COLOR_BACKGROUND = 0xFF000000;
    private static final int DEFAULT_COLOR_TEXT = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR_LINE = 0xFF8AC53F;
    private static final int DEFAULT_COLOR_START_GRADIENT = 0xFF000000;
    private static final int DEFAULT_COLOR_END_GRADIENT = 0xFF8AC53F;

    // default size
    private static final int DEFAULT_TEXT_SIZE_IN_SP = 18;
    private static final int DEFAULT_LINE_SIZE_IN_DP = 4;
    private static final int DEFAULT_HEIGHT_IN_DP = 20;
    private static final int DEFAULT_WIDTH_SPACE_BETWEEN_COLUMN_IN_DP = 8;
    private static final int DEFAULT_COLUMN_TOP_OFFSET_IN_DP = 30;
    private static final int DEFAULT_LEFT_RIGHT_OFFSET_IN_DP = 16;
    private static final int DEFAULT_TEXT_BOTTOM_OFFSET_IN_DP = 2;
    private static final int DEFAULT_ANIMATION_TIME = 3000;

    // smooth line value
    private static final float GRAPH_SMOOTHNES = 0.15f;

    // settings variables
    private int mBackgroundColor;
    private int mTextColor;
    private int mLineColor;
    private int mStartGradientColor;
    private int mEndGradientColor;
    private int mTextSize;
    private int mLineSize;
    private boolean enableAnimation;

    // temp variables
    private int mAnimationTime;
    private int mMaxColumnHeight;
    private int mWidthSpace;
    private int mColumnTopOffset;
    private int mLeftRightOffset;
    private int mTextBottomOffset;
    private int mViewWidth;
    private int mViewHeight;
    private Rect mAnimationRect;
    private Paint mBackgroundPaint;
    private Paint mBackgroundGradientPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mOverlayPaint;

    // days and amounts
    private String[] mDays;
    private int[] amounts;

    /**
     * Constructor
     *
     * @param context context
     */
    public WeekChartView(Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * Constructor
     *
     * @param context context
     * @param attrs   attrs
     */
    public WeekChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public WeekChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Init data
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        float density = getResources().getDisplayMetrics().density;

        // init color variables
        mBackgroundColor = DEFAULT_COLOR_BACKGROUND;
        mTextColor = DEFAULT_COLOR_TEXT;
        mLineColor = DEFAULT_COLOR_LINE;
        mStartGradientColor = DEFAULT_COLOR_START_GRADIENT;
        mEndGradientColor = DEFAULT_COLOR_END_GRADIENT;

        // init size variables
        mTextSize = (int) (DEFAULT_TEXT_SIZE_IN_SP * density);
        mLineSize = (int) (DEFAULT_LINE_SIZE_IN_DP * density);
        mMaxColumnHeight = (int) (DEFAULT_HEIGHT_IN_DP * density);
        mWidthSpace = (int) (DEFAULT_WIDTH_SPACE_BETWEEN_COLUMN_IN_DP * density);
        mColumnTopOffset = (int) (DEFAULT_COLUMN_TOP_OFFSET_IN_DP * density);
        mLeftRightOffset = (int) (DEFAULT_LEFT_RIGHT_OFFSET_IN_DP * density);
        mTextBottomOffset = (int) (DEFAULT_TEXT_BOTTOM_OFFSET_IN_DP * density);
        mDays = getResources().getStringArray(R.array.days);
        mAnimationTime = DEFAULT_ANIMATION_TIME;

        // getting attributes
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekChartView, defStyleAttr, 0);
            try {
                mBackgroundColor = typedArray.getColor(R.styleable.WeekChartView_wcv_background_color, mBackgroundColor);
                mTextColor = typedArray.getColor(R.styleable.WeekChartView_wcv_text_color, mTextColor);
                mLineColor = typedArray.getColor(R.styleable.WeekChartView_wcv_line_color, mLineColor);
                mStartGradientColor = typedArray.getColor(R.styleable.WeekChartView_wcv_start_gradient_color, mStartGradientColor);
                mEndGradientColor = typedArray.getColor(R.styleable.WeekChartView_wcv_end_gradient_color, mEndGradientColor);
                mTextSize = (int) typedArray.getDimension(R.styleable.WeekChartView_wcv_text_size, mTextSize);
                mLineSize = (int) typedArray.getDimension(R.styleable.WeekChartView_wcv_line_size, mLineSize);
            } finally {
                typedArray.recycle();
            }
        }

        // create background paint
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        // create text paint
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        // create line paint
        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineSize);

        // create gradient paint
        mBackgroundGradientPaint = new Paint();

        // create overlay paint
        mOverlayPaint = new Paint();
        mOverlayPaint.setColor(mBackgroundColor);
        mOverlayPaint.setAntiAlias(true);
        mOverlayPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Measure size
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // get mode and size
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // calculator desired width and height
        int desiredWidth = (mDays.length - 1) * mWidthSpace + 2 * mLeftRightOffset + getPaddingLeft() + getPaddingRight();
        int desiredHeight = mMaxColumnHeight + mColumnTopOffset + getPaddingTop() + getPaddingBottom();

        // detect width height
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        // set width height again
        setMeasuredDimension(width, height);
    }

    /**
     * On size changed
     *
     * @param w    w
     * @param h    h
     * @param oldw oldw
     * @param oldh oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // set new width and height
        mViewWidth = w;
        mViewHeight = h;

        // calculator real width space
        mWidthSpace = (mViewWidth - 2 * mLeftRightOffset - getPaddingLeft() - getPaddingRight()) / (mDays.length - 1);

        // calculator max column height
        mMaxColumnHeight = mViewHeight - mColumnTopOffset - getPaddingTop() - getPaddingBottom();
    }

    /**
     * Draw view
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        if (amounts != null && amounts.length > 0) {
            drawGradient(canvas);
            drawOverlay(canvas);
            drawLine(canvas);
        }
        drawAnimationBackground(canvas);
        drawText(canvas);
    }

    /**
     * Draw animation background
     *
     * @param canvas canvas
     */
    private void drawAnimationBackground(final Canvas canvas) {
        if (mAnimationRect != null && enableAnimation) {
            canvas.drawRect(mAnimationRect, mBackgroundPaint);
        }
    }

    /**
     * Draw chart line
     *
     * @param canvas canvas
     */
    private void drawLine(Canvas canvas) {
        // calculator max amount
        int maxAmount = getMaxInArray(amounts);

        // create path
        Path path = new Path();
        path.moveTo(getXPos(0), getYPos(amounts[0], maxAmount));

        // smooth path
        for (int i = 0; i < amounts.length - 1; i++) {
            float thisPointX = getXPos(i);
            float thisPointY = getYPos(amounts[i], maxAmount);
            float nextPointX = getXPos(i + 1);
            float nextPointY = getYPos(amounts[si(i + 1, amounts)], maxAmount);

            float startdiffX = (nextPointX - getXPos(si(i - 1, amounts)));
            float startdiffY = (nextPointY - getYPos(amounts[si(i - 1, amounts)], maxAmount));
            float endDiffX = (getXPos(si(i + 2, amounts)) - thisPointX);
            float endDiffY = (getYPos(amounts[si(i + 2, amounts)], maxAmount) - thisPointY);

            float firstControlX = thisPointX + (GRAPH_SMOOTHNES * startdiffX);
            float firstControlY = thisPointY + (GRAPH_SMOOTHNES * startdiffY);
            float secondControlX = nextPointX - (GRAPH_SMOOTHNES * endDiffX);
            float secondControlY = nextPointY - (GRAPH_SMOOTHNES * endDiffY);

            path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY);
        }

        // draw path
        canvas.drawPath(path, mLinePaint);
    }

    /**
     * Draw overlay
     *
     * @param canvas canvas
     */
    private void drawOverlay(Canvas canvas) {
        // calculator max amount
        int maxAmount = getMaxInArray(amounts);

        // create path
        Path path = new Path();
        path.moveTo(getXPos(0), getYPos(amounts[0], maxAmount));

        // smooth path
        for (int i = 0; i < amounts.length - 1; i++) {
            float thisPointX = getXPos(i);
            float thisPointY = getYPos(amounts[i], maxAmount);
            float nextPointX = getXPos(i + 1);
            float nextPointY = getYPos(amounts[si(i + 1, amounts)], maxAmount);

            float startdiffX = (nextPointX - getXPos(si(i - 1, amounts)));
            float startdiffY = (nextPointY - getYPos(amounts[si(i - 1, amounts)], maxAmount));
            float endDiffX = (getXPos(si(i + 2, amounts)) - thisPointX);
            float endDiffY = (getYPos(amounts[si(i + 2, amounts)], maxAmount) - thisPointY);

            float firstControlX = thisPointX + (GRAPH_SMOOTHNES * startdiffX);
            float firstControlY = thisPointY + (GRAPH_SMOOTHNES * startdiffY);
            float secondControlX = nextPointX - (GRAPH_SMOOTHNES * endDiffX);
            float secondControlY = nextPointY - (GRAPH_SMOOTHNES * endDiffY);

            path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY);
        }

        // move to right top
        path.lineTo(mViewWidth - mLeftRightOffset - getPaddingRight(), getPaddingTop());
        // move to left top
        path.lineTo(mLeftRightOffset + getPaddingLeft(), getPaddingTop());
        // move to first point
        path.lineTo(getXPos(0), getYPos(amounts[0], maxAmount));

        // draw path
        canvas.drawPath(path, mOverlayPaint);
    }

    /**
     * Draw text
     *
     * @param canvas canvas
     */
    private void drawText(Canvas canvas) {
        Rect textBounds = new Rect();
        for (int i = 0; i < mDays.length; i++) {
            mTextPaint.getTextBounds(mDays[i], 0, mDays[i].length(), textBounds);
            int left = getPaddingLeft() + mLeftRightOffset - textBounds.width() / 2 + i * mWidthSpace;
            int top = mViewHeight - (textBounds.height() + mTextBottomOffset + getPaddingBottom());
            canvas.drawText(mDays[i], left, top, mTextPaint);
        }
    }

    /**
     * Draw gradient background
     *
     * @param canvas canvas
     */
    private void drawGradient(Canvas canvas) {
        Shader shader = new LinearGradient(0, 0, 0, mViewHeight, mEndGradientColor, mStartGradientColor, Shader.TileMode.CLAMP);
        mBackgroundGradientPaint.setShader(shader);
        canvas.drawRect(mLeftRightOffset + getPaddingLeft(), getPaddingTop(), mViewWidth - mLeftRightOffset - getPaddingRight(), mViewHeight - getPaddingBottom(), mBackgroundGradientPaint);
    }

    /**
     * Draw background
     *
     * @param canvas canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, mViewWidth, mViewHeight, mBackgroundPaint);
    }

    /**
     * Start animation
     */
    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(mAnimationTime);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int animationValue = (int) animation.getAnimatedValue();
                int left = (int) (((float) animationValue / 100) * mViewWidth);
                mAnimationRect = new Rect(left, 0, mViewWidth, mViewHeight);
                invalidate(mAnimationRect);
            }
        });
        animator.start();
    }

    /**
     * Make sure right index
     *
     * @param i       i
     * @param amounts amounts
     * @return index
     */
    private int si(int i, int[] amounts) {
        if (i > amounts.length - 1) {
            return amounts.length - 1;
        } else if (i < 0) {
            return 0;
        }
        return i;
    }

    /**
     * Get x position
     *
     * @param i i
     * @return x position
     */
    private int getXPos(int i) {
        return i * mWidthSpace + mLeftRightOffset + getPaddingLeft();
    }

    /**
     * Get y position
     *
     * @param value value
     * @param max   max
     * @return y position
     */
    private int getYPos(int value, int max) {
        // get height of column
        value = (int) (((float) value / max) * mMaxColumnHeight);
        // invert y position
        value = mMaxColumnHeight - value;
        // plus to top offset
        value += mColumnTopOffset;
        // plus top padding
        value += getPaddingTop();
        // return y position
        return value;
    }

    /**
     * Get max value of array
     *
     * @param arr arr
     * @return max value of array
     */
    private int getMaxInArray(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    /**
     * Set amounts data
     *
     * @param amounts amounts
     */
    @Override
    public void setAmounts(int[] amounts) {
        this.amounts = amounts;
        if (enableAnimation) {
            startAnimation();
        } else {
            invalidate();
        }
    }

    /**
     * Set background color
     *
     * @param resid resid
     */
    @Override
    public void setViewBackgroundColor(@ColorRes int resid) {
        mBackgroundColor = ContextCompat.getColor(getContext(), resid);
        invalidate();
    }

    /**
     * Set text color
     *
     * @param resid resid
     */
    @Override
    public void setViewTextColor(int resid) {
        mTextColor = ContextCompat.getColor(getContext(), resid);
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    /**
     * Set text size
     *
     * @param resid resid
     */
    @Override
    public void setViewTextSize(int resid) {
        mTextSize = (int) getContext().getResources().getDimension(resid);
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    /**
     * Set line color
     *
     * @param resid resid
     */
    @Override
    public void setViewLineColor(int resid) {
        mLineColor = ContextCompat.getColor(getContext(), resid);
        mLinePaint.setColor(mLineColor);
        invalidate();
    }

    /**
     * Set start gradient color
     *
     * @param resid resid
     */
    @Override
    public void setViewStartGradientColor(int resid) {
        mStartGradientColor = ContextCompat.getColor(getContext(), resid);
        invalidate();
    }

    /**
     * Set end gradient color
     *
     * @param resid resid
     */
    @Override
    public void setViewEndGradientColor(int resid) {
        mEndGradientColor = ContextCompat.getColor(getContext(), resid);
        invalidate();
    }

    /**
     * Set line size
     *
     * @param resid resid
     */
    @Override
    public void setViewLineSize(int resid) {
        mLineSize = (int) getContext().getResources().getDimension(resid);
        mLinePaint.setStrokeWidth(mLineSize);
        invalidate();
    }

    /**
     * Set animation time
     *
     * @param milliseconds milliseconds
     */
    @Override
    public void setAnimationTime(int milliseconds) {
        mAnimationTime = milliseconds;
    }

    /**
     * Set animation enable
     *
     * @param enable enable
     */
    @Override
    public void setAnimationEnable(boolean enable) {
        enableAnimation = enable;
    }

}