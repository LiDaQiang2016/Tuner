package com.park.tunner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * DashboardView style 1
 * Created by woxingxiao on 2016-11-19.
 */

public class DashboardView extends View {

    private int mRadius; // 扇形半径
    private int mStartAngle = 180; // 起始角度
    private int mSweepAngle = 180; // 绘制角度
    private int mPointerAngle = 0; // 指针初始绘制角度
    private int mStandardSize = 440; // 指针初始绘制角度



    private int mScaleColor = Color.BLUE; // 刻度盘颜色
    private int mPointerColor = Color.YELLOW; // 指针默认颜色
    private int mPointerSelectColor = Color.GREEN; // 指针到达正确位置颜色

    public DashboardView(Context context) {
        super(context);
        init();
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setStandardSize(int size){
       mStandardSize=size;
       mMin = 0; // 最小值
       mMax = mStandardSize*2; // 最大值
       for (int i = 0; i < mTexts.length; i++) {
           int n = (mMax - mMin) / mSection;
           mTexts[i] = String.valueOf(mMin + i * n);
       }
       setRealTimeValue(0);
       invalidate();
//       requestLayout();

    }

    public void setmHeaderText(String text){
        mHeaderText=text;
        postInvalidate();
    }

//    private int mStartAngle = 0; // 起始角度
//    private int mSweepAngle = 0; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = mStandardSize*2; // 最大值
    private int mSection = 10; // 值域（mMax-mMin）等分份数
    private int mPortion = 10; // 一个mSection等分份数
    private String mHeaderText = "park"; // 表头
    private int mRealTimeValue = mMin; // 实时读数
    private boolean isShowValue = true; // 是否显示实时读数
    private int mStrokeWidth; // 画笔宽度
    private int mLength1; // 长刻度的相对圆弧的长度
    private int mLength2; // 刻度读数顶部的相对圆弧的长度
    private int mPLRadius; // 指针长半径
    private int mPSRadius; // 指针短半径

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFArc;
    private Path mPath;
    private RectF mRectFInnerArc;
    private Rect mRectText;
    private String[] mTexts;

    private void init() {
        mStrokeWidth = dp2px(1);
        mLength1 = dp2px(8) + mStrokeWidth;
        mLength2 = mLength1 + dp2px(2);
        mPSRadius = dp2px(10);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mRectFArc = new RectF();
        mPath = new Path();
        mRectFInnerArc = new RectF();
        mRectText = new Rect();

        mTexts = new String[mSection + 1]; // 需要显示mSection + 1个刻度读数
        for (int i = 0; i < mTexts.length; i++) {
            int n = (mMax - mMin) / mSection;
            mTexts[i] = String.valueOf(mMin + i * n);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();

        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;

        mPaint.setTextSize(sp2px(16));
        if (isShowValue) { // 显示实时读数，View高度增加字体高度3倍
            mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        } else {
            mPaint.getTextBounds("0", 0, 0, mRectText);
        }
        // 由半径+指针短半径+实时读数文字高度确定的高度
        int height1 = mRadius + mStrokeWidth * 2 + mPSRadius + mRectText.height() * 3;
        // 由起始角度确定的高度
        float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
        // 由结束角度确定的高度
        float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
        // 取最大值
        int max = (int) Math.max(
                height1,
                Math.max(point1[1] + mRadius + mStrokeWidth * 2, point2[1] + mRadius + mStrokeWidth * 2)
        );
        setMeasuredDimension(width, max + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

//        int width = resolveSize(dp2px(200), widthMeasureSpec);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
//        int  width = resolveSize(dp2px(width), widthMeasureSpec);

        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;

        mPaint.setTextSize(sp2px(16));
        if (isShowValue) { // 显示实时读数，View高度增加字体高度3倍
            mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        } else {
            mPaint.getTextBounds("0", 0, 0, mRectText);
        }
        // 由半径+指针短半径+实时读数文字高度确定的高度
        int height1 = mRadius + mStrokeWidth * 2 + mPSRadius + mRectText.height() * 3;
        // 由起始角度确定的高度
        float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
        // 由结束角度确定的高度
        float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
        // 取最大值
        int max = (int) Math.max(
                height1,
                Math.max(point1[1] + mRadius + mStrokeWidth * 2, point2[1] + mRadius + mStrokeWidth * 2)
        );

        mCenterX = mCenterY = getWidth() / 2f;
        mRectFArc.set(
                getPaddingLeft() + mStrokeWidth,
                getPaddingTop() + mStrokeWidth,
                getWidth() - getPaddingRight() - mStrokeWidth,
                getWidth() - getPaddingBottom() - mStrokeWidth
        );

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mRectFInnerArc.set(
                getPaddingLeft() + mLength2 + mRectText.height(),
                getPaddingTop() + mLength2 + mRectText.height(),
                getWidth() - getPaddingRight() - mLength2 - mRectText.height(),
                getWidth() - getPaddingBottom() - mLength2 - mRectText.height()
        );

        mPLRadius = mRadius - (mLength2 + mRectText.height() + dp2px(5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画圆弧
         */
        drawArc(canvas);

        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - cos));
        float y0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - sin));
        float x1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * sin);

        canvas.save();
        drawLongUnits(canvas, x0, y0, x1, y1);
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        canvas.save();
        drawShortUnits(canvas, cos, sin, x0, y0);
        canvas.restore();

        /**
         * 画长刻度读数
         * 添加一个圆弧path，文字沿着path绘制
         */
        drawUnitsText(canvas);

        /**
         * 画表头
         * 没有表头就不画
         */
        drawHead(canvas);

        /**
         * 画指针
         */
        drawPin(canvas);

        /**
         * 画指针围绕的镂空圆心
         */
        drawEmptyCircle(canvas);

        /**
         * 画实时度数值
         */
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (isShowValue) {
            mPaint.setTextSize(sp2px(16));
            mPaint.setTextAlign(Paint.Align.CENTER);
//            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            mPaint.setColor(mScaleColor);
            String value = String.valueOf(mRealTimeValue);
            mPaint.getTextBounds(value, 0, value.length(), mRectText);
            canvas.drawText(value, mCenterX, mCenterY + mPSRadius + mRectText.height() * 2, mPaint);
        }
    }

    private void drawEmptyCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, dp2px(2), mPaint);
    }

    private void drawPin(Canvas canvas) {
        float θ = mStartAngle+mPointerAngle + mSweepAngle * (mRealTimeValue - mMin) / (mMax - mMin); // 指针与水平线夹角

        if(mRealTimeValue==mStandardSize){
            mPaint.setColor(mPointerSelectColor);
        }else {
            mPaint.setColor(mPointerColor);
        }


        int d = dp2px(5); // 指针由两个等腰三角形构成，d为共底边长的一半
        mPath.reset();
        float[] p1 = getCoordinatePoint(d, θ - 90);
        mPath.moveTo(p1[0], p1[1]);
        float[] p2 = getCoordinatePoint(mPLRadius, θ);
        mPath.lineTo(p2[0], p2[1]);
        float[] p3 = getCoordinatePoint(d, θ + 90);
        mPath.lineTo(p3[0], p3[1]);
        float[] p4 = getCoordinatePoint(mPSRadius, θ - 180);
        mPath.lineTo(p4[0], p4[1]);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private void drawHead(Canvas canvas) {
        if (!TextUtils.isEmpty(mHeaderText)) {
            mPaint.setTextSize(sp2px(14));
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
            canvas.drawText(mHeaderText, mCenterX, mCenterY / 2f + mRectText.height(), mPaint);
        }
    }

    private void drawUnitsText(Canvas canvas) {
        mPaint.setTextSize(sp2px(10));
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < mTexts.length; i++) {
            mPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度
            float θ = (float) (180 * mRectText.width() / 2 /
                    (Math.PI * (mRadius - mLength2 - mRectText.height())));

            mPath.reset();
            mPath.addArc(
                    mRectFInnerArc,
                    mStartAngle + i * (mSweepAngle / mSection) - θ, // 正起始角度减去θ使文字居中对准长刻度
                    mSweepAngle
            );
            canvas.drawTextOnPath(mTexts[i], mPath, 0, 0, mPaint);
        }
    }

    private void drawShortUnits(Canvas canvas, double cos, double sin, float x0, float y0) {
        float angle;
        mPaint.setStrokeWidth(1);
        float x2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1 / 2f) * cos);
        float y2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1 / 2f) * sin);
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        angle = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 1; i < mSection * mPortion; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i % mPortion == 0) { // 避免与长刻度画重合
                continue;
            }
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
    }

    private void drawLongUnits(Canvas canvas, float x0, float y0, float x1, float y1) {
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPaint.setColor(mScaleColor);

        canvas.drawArc(mRectFArc, mStartAngle, mSweepAngle, false, mPaint);
    }

    private int dp2px(int dp) {
        dp *= 2;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    public int getRealTimeValue() {
        return mRealTimeValue;
    }

    public void setRealTimeValue(int realTimeValue) {
        if (mRealTimeValue == realTimeValue || realTimeValue < mMin || realTimeValue > mMax) {
            return;
        }

        mRealTimeValue = realTimeValue;
        postInvalidate();
    }
}
