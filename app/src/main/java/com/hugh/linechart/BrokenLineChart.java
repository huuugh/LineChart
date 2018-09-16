package com.hugh.linechart;

/**
 * Created by 60352 on 2018/9/16.
 *
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;


public class BrokenLineChart extends View {
    private static final String TAG = "BrokenLineChart";
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
    private float radius=5;
    /**边框的左边距*/
    private float mBrokenLineLeft=40;
    /**边框的上边距*/
    private float mBrokenLineTop=40;
    /**边框的下边距*/
    private float mBrokenLineBottom=40;
    /**边框的右边距*/
    private float mBrokenLinerRight=20;
    /**需要绘制的宽度*/
    private float mNeedDrawWidth;
    /**需要绘制的高度*/
    private float mNeedDrawHeight;
    /**边框文本*/
    private int[] HorizonValue =new int[]{40,30,20,10,0};
    /**数据值*/
    private int[] VerticalValue =new int[]{11,10,15,12,34,12,22,23,33,13};
    private int[] VerticalValue1 =new int[]{13,12,15,16,36,15,25,26,35,16};
    int[] shadeColors = new int[]{Color.rgb(0, 255, 0),Color.rgb(255, 255, 255)};
    int[] shadeColors1 = new int[]{Color.rgb(255, 0, 0),Color.rgb(255, 255, 255)};
    private String[] Vertical =new String[]{"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月"};

    /**图表的最大值*/
    private int maxValue = 40;
    private Paint mShaderPaint;
    private Point[] points;

    public BrokenLineChart(Context context) {
        super(context);
    }

    public BrokenLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        initNeedDrawWidthAndHeight();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**绘制边框线和边框文本*/
        DrawBorderLineAndText(canvas);

        /**根据数据绘制线*/
        DrawBrokenLine(canvas,VerticalValue1);
        DrawLineCircle(canvas,VerticalValue1);
        DrawShader(canvas,shadeColors);
        DrawBrokenLine(canvas,VerticalValue);
        DrawLineCircle(canvas,VerticalValue);
        DrawShader(canvas,shadeColors1);
    }

    private void DrawShader(Canvas canvas,int[] colors){
        mShaderPaint.setStyle(Paint.Style.FILL);
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
        Shader mShader = new LinearGradient(0, 0, 0, getHeight(), colors, new float[]{0.3f,0.7f}, Shader.TileMode.CLAMP);
        mShaderPaint.setShader(mShader);
        canvas.drawPath(mShaderPath, mShaderPaint);
    }

    /**绘制线上的圆*/
    private void DrawLineCircle(Canvas canvas,int[] values) {
        Point[] points= getPoints(values,mNeedDrawHeight,mNeedDrawWidth, maxValue,mBrokenLineLeft,mBrokenLineTop);
        for (int i = 0; i <points.length ; i++) {
            Point point=points[i];
            mCirclePaint.setColor(Color.WHITE);
            mCirclePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x,point.y,radius,mCirclePaint);
            mCirclePaint.setColor(Color.BLUE);
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setStrokeWidth(3);
            /**
             * drawCircle(float cx, float cy, float radius, Paint paint)
             * cx 中间x坐标
             * xy 中间y坐标
             * radius 圆的半径
             * paint 绘制圆的画笔
             * */
            canvas.drawCircle(point.x,point.y,radius,mCirclePaint);
        }
    }

    /**根据值绘制折线*/
    private void DrawBrokenLine(Canvas canvas,int[] values) {
        Path mPath=new Path();
        mBrokenLinePaint.setColor(Color.BLUE);
        mBrokenLinePaint.setStrokeWidth(2);
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
        /**对应的属性
         * drawLine(float startX, float startY, float stopX, float stopY, Paint paint);
         * startX   开始的x坐标
         * startY   开始的y坐标
         * stopX    结束的x坐标
         * stopY    结束的y坐标
         * paint    绘制该线的画笔
         * */
        mBorderLinePaint.setColor(Color.BLACK);
        /**绘制边框竖线*/
        canvas.drawLine(mBrokenLineLeft,mBrokenLineTop-10,mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mBorderLinePaint);
        /**绘制边框横线*/
        canvas.drawLine(mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mViewWidth,mViewHeight-mBrokenLineBottom,mBorderLinePaint);


        /**绘制边框分段横线与分段文本*/
        float averageHeight=mNeedDrawHeight/(HorizonValue.length-1);
        mBorderLinePaint.setTextAlign(Paint.Align.RIGHT);
        mBorderLinePaint.setColor(Color.GRAY);
        for (int i = 0; i < HorizonValue.length; i++) {
            float nowadayHeight= averageHeight*i;
            canvas.drawLine(mBrokenLineLeft,nowadayHeight+mBrokenLineTop,mViewWidth-mBrokenLinerRight,nowadayHeight+mBrokenLineTop,mBorderLinePaint);
            canvas.drawText(HorizonValue[i]+"",mBrokenLineLeft-5,nowadayHeight+mBrokenLineTop,mBorderLinePaint);
        }

        float averageWidth = mNeedDrawWidth/(Vertical.length-1);
        mBorderLinePaint.setTextAlign(Paint.Align.CENTER);
        mBorderLinePaint.setColor(Color.GRAY);
        mBorderLinePaint.setTextSize(20);
        for (int i = 0; i < Vertical.length; i++) {
            float nowadaywidth= averageWidth*i;
            canvas.drawText(Vertical[i]+"",mBrokenLineLeft+nowadaywidth,mViewHeight-mBrokenLineBottom+20,mBorderLinePaint);
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
            mBorderLinePaint.setTextSize(20);
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
    public Point[] getPoints(int[] values, float height, float width, int max , float left,float top) {
        float leftPading = width / (values.length-1);//绘制边距
        Point[] points = new Point[values.length];
        for (int i = 0; i < values.length; i++) {
            float value = values[i];
            //计算每点高度所以对应的值
            double mean = (double) max/height;
            //获取要绘制的高度
            float drawHeight = (float) (value / mean);
            int pointY = (int) (height+top  - drawHeight);
            int pointX = (int) (leftPading * i + left);
            Point point = new Point(pointX, pointY);
            points[i] = point;
        }
        return points;
    }
}
