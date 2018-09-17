package com.hugh.linechart;

/**
 * Created by Hugh on 2018/9/16.
 *
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DynamicLineChart extends View {
    private static final String TAG = "LineChart";
    private Context mContext;
    /**View宽度*/
    private int mViewWidth;
    /** View高度*/
    private int mViewHeight;
    /**边框线画笔*/
    private Paint mBorderLinePaint;
    /**文本画笔*/
    private Paint mTextPaint;
    /**要绘制的折线线画笔*/
    private Paint mBrokenLinePaint;
    /**圆画笔*/
    private Paint mCirclePaint;
    /**圆的半径*/
    private float mRadius;
    /**边框的左边距*/
    private float mBrokenLineLeft;
    /**边框的上边距*/
    private float mBrokenLineTop;
    /**边框的下边距*/
    private float mBrokenLineBottom;
    /**边框的右边距*/
    private float mBrokenLinerRight;
    /**需要绘制的宽度*/
    private float mNeedDrawWidth;
    /**需要绘制的高度*/
    private float mNeedDrawHeight;
    /**y轴文本*/
    private String[] mYAxisText = new String[]{"60","50","40","30","20","10"};
    /**x轴文本*/
    private String[] mXAxisText = new String[]{"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月"};
    /**数据值*/
    private float[] mLineOneValues = new float[]{11f,10f,15f,12f,34f,12f,22f,23f,33f,13f};
    private float[] mLineTwoValues = new float[]{13f,12f,15f,16f,36f,15f,25f,26f,35f,16f};
    int[] mLineTwoShadeColors = new int[]{Color.parseColor("#87CEFA"),Color.parseColor("#ADD8E6")};
    int[] mLineOneShadeColors = new int[]{Color.parseColor("#EEE8AA"),Color.parseColor("#FFFAF0")};
    LinkedList linkedList = new LinkedList<Float>();

    /**图表的最大值*/
    private int maxValue = 60;
    private int minValue = 10;
    private Paint mShaderPaint;

    public DynamicLineChart(Context context) {
        super(context);
    }

    public DynamicLineChart(Context context, @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mBrokenLineLeft = DensityUtil.dp2px(mContext,35);
        mBrokenLineTop = DensityUtil.dp2px(mContext,20);
        mBrokenLineBottom = DensityUtil.dp2px(mContext,29);
        mBrokenLinerRight = DensityUtil.dp2px(mContext,17);
        mRadius = DensityUtil.dp2px(mContext,2);
        Timer timer = new Timer();
        for (int i=0;i<mLineOneValues.length;i++){
            linkedList.add(mLineOneValues[i]);
        }
        TimerTask timerTask =new TimerTask() {
            @Override
            public void run() {
                float random = (float)(Math.random()*60);
                linkedList.offer(random);
                linkedList.poll();
                invalidate();
            }
        };
        timer.schedule(timerTask,3000,3000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        initNeedDrawWidthAndHeight();
        initPaint();
    }

    /**设置点数据*/
    public DynamicLineChart setMaxAndMin(int max, int min){
        maxValue = max;
        minValue = min;
        return this;
    }

    /**设置点数据*/
    public DynamicLineChart setLineOneValues(float[] values){
        mLineOneValues = values;
        return this;
    }

    /**设置点数据*/
    public DynamicLineChart setLineTwoValues(float[] values){
        mLineTwoValues = values;
        return this;
    }

    /**设置线条1颜色*/
    public DynamicLineChart setLineOneColors(int[] values){
        mLineOneShadeColors = values;
        return this;
    }

    /**设置线条2颜色*/
    public DynamicLineChart setLineTwoColors(int[] values){
        mLineTwoShadeColors = values;
        return this;
    }

    /**设置坐标轴数据*/
    public DynamicLineChart setAxisValues(String[] X_axis, String[] Y_axis){
        if (X_axis == null || Y_axis == null){

        }else {
            mXAxisText = X_axis;
            mYAxisText = Y_axis;
        }
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**根据数据绘制线*/
        /*DrawShader(canvas, mLineTwoShadeColors, mLineTwoValues);
        DrawLines(canvas, mLineTwoValues);
        DrawLineCircle(canvas, mLineTwoValues);*/
        DrawShader(canvas, mLineOneShadeColors, mLineOneValues);
        DrawLines(canvas, mLineOneValues);
        DrawLineCircle(canvas, mLineOneValues);
        /**绘制边框线和边框文本*/
        DrawBorderLineAndText(canvas);
    }

    private void DrawShader(Canvas canvas,int[] colors,float[] values){
        mShaderPaint.setStyle(Paint.Style.FILL);
        Point[] points= getPoints(linkedList,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
        Path mShaderPath = new Path();
        for (int i = 0; i < points.length; i++) {
            Point point= points[i];
            if(i==0){
                mShaderPath.moveTo(point.x,mViewHeight-mBrokenLineBottom);
                mShaderPath.lineTo(point.x,point.y);
            }else if (i==points.length-1){
                mShaderPath.lineTo(point.x,point.y);
                mShaderPath.lineTo(point.x,mViewHeight-mBrokenLineBottom);
            }else {
                mShaderPath.lineTo(point.x,point.y);
            }
        }
        mShaderPath.close();
        Shader mShader = new LinearGradient(0, mBrokenLineTop, 0, getHeight(), colors, new float[]{0.3f,0.7f}, Shader.TileMode.CLAMP);
        mShaderPaint.setShader(mShader);
        canvas.drawPath(mShaderPath, mShaderPaint);
    }

    /**绘制线上的圆*/
    private void DrawLineCircle(Canvas canvas,float[] values) {
        Point[] points= getPoints(linkedList,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
        for (int i = 0; i <points.length ; i++) {
            Point point=points[i];
            mCirclePaint.setColor(Color.BLUE);
            mCirclePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x,point.y, mRadius,mCirclePaint);
        }
    }

    /**根据值绘制折线*/
    private void DrawLines(final Canvas canvas, float[] values) {
        final Path mPath=new Path();
        mBrokenLinePaint.setColor(Color.BLUE);
        mBrokenLinePaint.setStrokeWidth(DensityUtil.dp2px(mContext,1));
        Point[] points = getPoints(linkedList, mNeedDrawHeight, mNeedDrawWidth, maxValue, mBrokenLineLeft, mBrokenLineTop);
        for (int i = 0; i < points.length; i++) {
            Point point= points[i];
            if(i==0){
                mPath.moveTo(point.x,point.y);
            }else {
                mPath.lineTo(point.x,point.y);
            }

        }
        PathMeasure measure = new PathMeasure(mPath, false);
        final float length = measure.getLength();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float phase = (float) animation.getAnimatedValue();
                DashPathEffect dashPathEffect = new DashPathEffect(new float[]{phase * length, length}, 0);
                mBrokenLinePaint.setPathEffect(dashPathEffect);
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBrokenLinePaint.setPathEffect(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        canvas.drawPath(mPath,mBrokenLinePaint);
    }

    /**绘制边框线和边框文本*/
    private void DrawBorderLineAndText(Canvas canvas) {
        mBorderLinePaint.setColor(Color.BLACK);
        /**边框竖线*/
        canvas.drawLine(mBrokenLineLeft,mBrokenLineTop-DensityUtil.dp2px(mContext,10),mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mBorderLinePaint);
        /**边框横线*/
        canvas.drawLine(mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mViewWidth,mViewHeight-mBrokenLineBottom,mBorderLinePaint);

        float averageHeight=mNeedDrawHeight/(mYAxisText.length-1);
        mBorderLinePaint.setTextAlign(Paint.Align.CENTER);
        mBorderLinePaint.setColor(Color.GRAY);
        mBorderLinePaint.setTextSize(DensityUtil.sp2px(mContext,12));
        Path mDottedPath = new Path();
        for (int i = 0; i < mYAxisText.length; i++) {
            float nowadaysHeight= averageHeight*i;
            mDottedPath.moveTo(mBrokenLineLeft,nowadaysHeight+mBrokenLineTop);
            mDottedPath.lineTo(mViewWidth-mBrokenLinerRight,nowadaysHeight+mBrokenLineTop);
            DashPathEffect pathEffect = new DashPathEffect(new float[] { 4,4 }, 1);
            mBorderLinePaint.setPathEffect(pathEffect);
            canvas.drawPath(mDottedPath,mBorderLinePaint);
            mBorderLinePaint.setPathEffect(null);
            canvas.drawText(mYAxisText[i],mBrokenLineLeft-DensityUtil.dp2px(mContext,15),nowadaysHeight+mBrokenLineTop+DensityUtil.dp2px(mContext,5),mBorderLinePaint);
        }

        float averageWidth = mNeedDrawWidth/(mXAxisText.length-1);
        mBorderLinePaint.setColor(Color.GRAY);
        for (int i = 0; i < mXAxisText.length; i++) {
            float nowadaysWidth= averageWidth*i;
            canvas.drawText(mXAxisText[i],mBrokenLineLeft+nowadaysWidth,mViewHeight-mBrokenLineBottom+DensityUtil.dp2px(mContext,20),mBorderLinePaint);
        }
    }

    /**初始化画笔*/
    private void initPaint() {

        /**初始化文本画笔*/
        if(mTextPaint==null){
            mTextPaint=new Paint();
        }
        initPaint(mTextPaint);

        /**初始化边框线画笔*/
        if(mBorderLinePaint==null){
            mBorderLinePaint=new Paint();
        }
        initPaint(mBorderLinePaint);

        /**初始化折线画笔*/
        if(mBrokenLinePaint==null){
            mBrokenLinePaint=new Paint();
        }
        initPaint(mBrokenLinePaint);

        if(mCirclePaint==null){
            mCirclePaint=new Paint();
        }
        initPaint(mCirclePaint);

        /**初始化阴影画笔*/
        if(mShaderPaint ==null){
            mShaderPaint =new Paint();
        }
        initPaint(mShaderPaint);
    }


    /**初始化画笔默认属性*/
    private void initPaint(Paint paint){
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
    }


    /**初始化绘制折线图的宽高*/
    private void initNeedDrawWidthAndHeight(){
        mNeedDrawWidth = mViewWidth-mBrokenLineLeft-mBrokenLinerRight;
        mNeedDrawHeight = mViewHeight-mBrokenLineTop-mBrokenLineBottom;
    }

    /**根据值计算在该值的 x，y坐标*/
    public Point[] getPoints(List<Float> values, float height, float width, int max , float left, float top) {
        float leftPadding = width / (values.size()-1);//绘制边距
        Point[] points = new Point[values.size()];
        for (int i = 0; i < values.size(); i++) {
            float value = values.get(i);
            double mean = (double)(max-minValue)/height;
            float drawHeight = (float) ((value - minValue) / mean);
            int pointY = (int) (height+top-drawHeight);
            int pointX = (int) (leftPadding * i + left);
            Point point = new Point(pointX, pointY);
            points[i] = point;
        }
        return points;
    }
}
