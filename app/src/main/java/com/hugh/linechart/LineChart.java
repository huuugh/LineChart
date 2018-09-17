package com.hugh.linechart;

/**
 * Created by Hugh on 2018/9/16.
 *
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


public class LineChart extends View {
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
    private float[] mLineOneValues = new float[]{11,10,15,12,34,12,22,23,33,13};
    private float[] mLineTwoValues = new float[]{13,12,15,16,36,15,25,26,35,16};
    int[] mLineTwoShadeColors = new int[]{Color.parseColor("#87CEFA"),Color.parseColor("#ADD8E6")};
    int[] mLineOneShadeColors = new int[]{Color.parseColor("#EEE8AA"),Color.parseColor("#FFFAF0")};

    /**图表的最大值*/
    private int maxValue = 60;
    private int minValue = 10;
    private Paint mShaderPaint;
    private Point[] points;

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mBrokenLineLeft = DensityUtil.dp2px(mContext,35);
        mBrokenLineTop = DensityUtil.dp2px(mContext,35);
        mBrokenLineBottom = DensityUtil.dp2px(mContext,29);
        mBrokenLinerRight = DensityUtil.dp2px(mContext,17);
        mRadius = DensityUtil.dp2px(mContext,2);
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
    public LineChart setMaxAndMin(int max,int min){
        maxValue = max;
        minValue = min;
        return this;
    }

    /**设置点数据*/
    public LineChart setLineOneValues(float[] values){
        mLineOneValues = values;
        return this;
    }

    /**设置点数据*/
    public LineChart setLineTwoValues(float[] values){
        mLineTwoValues = values;
        return this;
    }

    /**设置线条1阴影颜色*/
    public LineChart setLineOneShaderColors(int[] values){
        mLineOneShadeColors = values;
        return this;
    }

    /**设置线条2阴影颜色*/
    public LineChart setLineTwoShaderColors(int[] values){
        mLineTwoShadeColors = values;
        return this;
    }

    /**设置坐标轴数据*/
    public LineChart setAxisValues(String[] X_axis,String[] Y_axis){
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
        DrawShader(canvas, mLineTwoShadeColors, mLineTwoValues);
        DrawLines(canvas, mLineTwoValues);
        DrawLineCircle(canvas, mLineTwoValues);
        DrawShader(canvas, mLineOneShadeColors, mLineOneValues);
        DrawLines(canvas, mLineOneValues);
        DrawLineCircle(canvas, mLineOneValues);
        /**绘制边框线和边框文本*/
        DrawBorderLineAndText(canvas);
    }

    private void DrawShader(Canvas canvas,int[] colors,float[] values){
        mShaderPaint.setStyle(Paint.Style.FILL);
        Point[] points= getPoints(values,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
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
        Point[] points= getPoints(values,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
        for (int i = 0; i <points.length ; i++) {
            Point point=points[i];
            mCirclePaint.setColor(Color.BLUE);
            mCirclePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x,point.y, mRadius,mCirclePaint);
        }
    }

    /**根据值绘制折线*/
    private void DrawLines(Canvas canvas, float[] values) {
        Path mPath=new Path();
        mBrokenLinePaint.setColor(Color.BLUE);
        mBrokenLinePaint.setStrokeWidth(DensityUtil.dp2px(mContext,1));
        points = getPoints(values,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
        for (int i = 0; i < points.length; i++) {
            Point point= points[i];
            if(i==0){
                mPath.moveTo(point.x,point.y);
            }else {
                mPath.lineTo(point.x,point.y);
            }

        }
        canvas.drawPath(mPath,mBrokenLinePaint);
    }

    /**绘制边框线和边框文本*/
    private void DrawBorderLineAndText(Canvas canvas) {
        mBorderLinePaint.setColor(Color.BLACK);
        float averageHeight=mNeedDrawHeight/(mYAxisText.length-1);
        /**边框竖线*/
        canvas.drawLine(mBrokenLineLeft,mBrokenLineTop-averageHeight,mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mBorderLinePaint);
        /**边框横线*/
        canvas.drawLine(mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mViewWidth,mViewHeight-mBrokenLineBottom,mBorderLinePaint);

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
        canvas.drawText("kg/%",mBrokenLineLeft-DensityUtil.dp2px(mContext,15),mBrokenLineTop-averageHeight+DensityUtil.dp2px(mContext,5),mBorderLinePaint);

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
    public Point[] getPoints(float[] values, float height, float width, int max , float left,float top) {
        float leftPadding = width / (values.length-1);//绘制边距
        Point[] points = new Point[values.length];
        for (int i = 0; i < values.length; i++) {
            float value = values[i];
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
