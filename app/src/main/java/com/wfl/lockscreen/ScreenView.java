package com.wfl.lockscreen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ScreenView extends View {
    private static final String TAG = "ScreenView";
    private String mExampleString;
    private int mExampleColor = Color.RED;
    private float mExampleDimension = 0;
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private int mScreenWidth;
    private List<Point> mPoints;
    private Paint mPaint;
    private Paint mLinePaint;
    private Point mCurrentPoint = new Point();

    private List<Integer> mUnlockList = new ArrayList<>();
    private List<Integer> mTouchedPoints = new ArrayList<>();

    private boolean mUnlocking = false;
    private OnUnlockListener mOnUnlockListener;

    public ScreenView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ScreenView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.ScreenView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.ScreenView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.ScreenView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.ScreenView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.ScreenView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();


        mUnlockList.add(0);
        mUnlockList.add(1);
        mUnlockList.add(3);
        mUnlockList.add(4);
        mUnlockList.add(5);
        mUnlockList.add(7);
        initPaint();
    }

    private void initPoints() {
        mPoints = new ArrayList<>();
        for (int i=1; i<=5; i+=2) {
            for (int j=1; j<=5; j+=2) {
                Point point = new Point();
                point.set(mScreenWidth * j / 6, mScreenWidth * i / 6);
                mPoints.add(point);
            }
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStrokeWidth(40);


        mLinePaint = new Paint();
        mLinePaint.setColor(Color.parseColor("#88000000"));
        mLinePaint.setStrokeWidth(40);
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPoints == null)
            initPoints();

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/

        drawPoints(canvas);
        drawLines(canvas);
    }

    private void drawPoints(Canvas canvas) {
        for (int i=0; i<mPoints.size(); i++) {
            drawOnePoint(canvas, mPoints.get(i));
        }
    }

    private void drawOnePoint(Canvas canvas, Point point) {
        canvas.drawCircle(point.x, point.y, 20, mPaint);
    }

    private void drawLines(Canvas canvas) {
        for (int i=0; i<mTouchedPoints.size() - 1; i++) {
            int position = mTouchedPoints.get(i);
            Point point = mPoints.get(position);
            Point point2 = mPoints.get(mTouchedPoints.get(i+1));
            canvas.drawLine(point.x, point.y, point2.x, point2.y, mLinePaint);
            canvas.drawCircle(point.x, point.y, 40, mLinePaint);
        }
        if (mUnlocking) {
            Point point = mPoints.get(mTouchedPoints.get(mTouchedPoints.size() - 1));
            canvas.drawLine(point.x, point.y, mCurrentPoint.x, mCurrentPoint.y, mLinePaint);
            canvas.drawCircle(point.x, point.y, 40, mLinePaint);
            canvas.drawCircle(mCurrentPoint.x, mCurrentPoint.y, 20, mLinePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mScreenWidth = w;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        int hitResult = hitThePoint(x, y);
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                if (hitResult != -1) {
                    if (!mUnlocking) {
                        unlockStart();
                        mTouchedPoints.add(hitResult);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPoint.set(x, y);
                if (hitResult != -1) {
                    if (!mUnlocking) {
                        unlockStart();
                        mTouchedPoints.add(hitResult);
                    } else {
                        mTouchedPoints.add(hitResult);
                    }
                } else {

                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "result: " + mTouchedPoints.toString());
                if (judgeUnlock()) {
                    if (mOnUnlockListener != null) {
                        mOnUnlockListener.onUnlockSuccess();
                    }
                }
                mTouchedPoints.clear();

                break;
        }
        return super.onTouchEvent(event);
    }

    private int hitThePoint(int x, int y) {
        for (int i=0; i<mPoints.size(); i++) {
            Point point = mPoints.get(i);
            if (Math.abs(x - point.x) < 80 && Math.abs(y - point.y) < 80) {
                if (!mTouchedPoints.contains(i)) {
                    Log.v(TAG, "hit: " + i);
                    return i;
                }
            }
        }
        return -1;
    }

    private void unlockStart() {
        mUnlocking = true;
        mTouchedPoints.clear();
    }

    private boolean judgeUnlock() {
        mUnlocking = false;
        return mTouchedPoints.toString().equals(mUnlockList.toString());
    }



    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public interface OnUnlockListener {
        void onUnlockSuccess();
    }


    public void setOnUnlockListener(OnUnlockListener onUnlockListener) {
        this.mOnUnlockListener = onUnlockListener;
    }
}
