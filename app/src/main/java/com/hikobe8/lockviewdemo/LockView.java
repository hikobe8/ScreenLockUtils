package com.hikobe8.lockviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;


/**
 * TODO: document your custom view class.
 */
public class LockView extends View {

    private boolean isDraw = false;
    private boolean inited = false;
    //九个点
    private Point[][] points = new Point[3][3];
    private Bitmap bitmapNormal;
    private Bitmap bitmapSelected;
    private Bitmap bitmapError;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //图片半径
    private float bitmapR;

    private float mouseX;
    private float mouseY;
    private int[] spointXY;
    private int sX, sY;
    private List<Point> selectedPoints = new ArrayList<Point>();
    private List<Integer> passPoints = new ArrayList<Integer>();
    private Paint selectedPaint;
    private Paint errorPaint;
    private OnDrawFinishListener onDrawFinishListener;
    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoints();
                isDraw = true;
                spointXY = getSelectedPoint();
                if (spointXY != null) {
                    sX = spointXY[0];
                    sY = spointXY[1];
                    if(!selectedPoints.contains(points[sX][sY])){
                        passPoints.add(sX*3 + sY);
                        points[sX][sY].state = Point.SELECTED;
                        selectedPoints.add(points[sX][sY]);
                        points[sX][sY].state = Point.SELECTED;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDraw) {
                    spointXY = getSelectedPoint();
                    if (spointXY != null) {
                        sX = spointXY[0];
                        sY = spointXY[1];
                        if(!selectedPoints.contains(points[sX][sY])){
                            passPoints.add(sX*3 + sY);
                            points[sX][sY].state = Point.SELECTED;
                            selectedPoints.add(points[sX][sY]);
                            points[sX][sY].state = Point.SELECTED;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int valid;
                //回调绘制完成监听接口
                if(onDrawFinishListener != null && isDraw) {
                    valid = onDrawFinishListener.onDrawFinished(passPoints);
                    switch (valid){
                        case OnDrawFinishListener.PWD_WRONG:
                            for(Point p : selectedPoints) {
                                p.state = Point.ERROR;
                            }
                            onDrawFinishListener.onCheckPwdFailed();
                            break;
                        case OnDrawFinishListener.PWD_CORRECT:
                            onDrawFinishListener.onCheckSuccess();
                            break;
                        case OnDrawFinishListener.PWD_SET:
                            onDrawFinishListener.onSetPwdSuccess();
                            break;
                    }
                }
                isDraw = false;
                break;
        }
        this.postInvalidate();
        return true;
    }

    /**
     * getSelectedPoint 得到选中的点
     * @return 若选中返回选中的点，反之null
     */
    private int[] getSelectedPoint() {
        for (int  i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                if(points[i][j].distance(mouseX, mouseY) < bitmapR) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private void init() {
        selectedPaint = new Paint();
        selectedPaint.setColor(BLUE);
        selectedPaint.setStrokeWidth(5);
        errorPaint = new Paint();
        errorPaint.setColor(RED);
        errorPaint.setStrokeWidth(5);
        bitmapNormal = BitmapFactory.decodeResource(getResources(), R.drawable.normal);
        bitmapSelected = BitmapFactory.decodeResource(getResources(), R.drawable.press);
        bitmapError = BitmapFactory.decodeResource(getResources(), R.drawable.error);
        bitmapR = bitmapNormal.getHeight() / 2;
        //屏幕宽度
        int width = getWidth();
        //屏幕高度
        int height = getHeight();
        //偏移量 = (宽度高度之差) /2
        float offset = Math.abs(width - height);
        float offsetX,offsetY,space; //每个格子的宽度为宽高较短一边的1/4
        if(width < height){
            space = width / 4;
            offsetY = offset;
            offsetX = 0;
        } else {
            space = height / 4;
            offsetX = offset;
            offsetY = 0;
        }
        for(int i = 0; i < 3; i ++){
            for ( int j = 0; j < 3; j ++){
                points[i][j] = new Point(offsetX+space*(j + 1),offsetY+space*i);
            }
        }
        inited = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!inited) {
            init();
        }
        drawPoints(canvas);
        if(selectedPoints != null && selectedPoints.size() > 1) {
            Point startPoint = selectedPoints.get(0);
            for (int i = 1; i < selectedPoints.size(); i++) {
                Point endPoint = selectedPoints.get(i);
                drawLine(canvas, startPoint, endPoint);
                startPoint = endPoint;
            }
            if(isDraw) {
                drawLine(canvas, selectedPoints.get(selectedPoints.size() - 1), new Point(mouseX, mouseY));
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        for(int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                Bitmap bitmap = null;
                switch (points[i][j].state){
                    case Point.NORMAL:
                        bitmap = bitmapNormal;
                        break;
                    case Point.SELECTED:
                        bitmap = bitmapSelected;
                        break;
                    case Point.ERROR:
                        bitmap = bitmapError;
                        break;
                }
                canvas.drawBitmap(bitmap, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
            }
        }
    }

    private void drawLine(Canvas canvas, Point startPoint, Point endPoint) {
        if (startPoint.state == Point.SELECTED) {
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, selectedPaint);
        } else if (startPoint.state == Point.ERROR){
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, errorPaint);
        }
    }

    public interface OnDrawFinishListener{
        int PWD_CORRECT = 1;
        int PWD_WRONG = 2;
        int PWD_SET = 3;
        int onDrawFinished(List<Integer> passPoints);
        void onCheckSuccess();
        void onSetPwdSuccess();
        void onCheckPwdFailed();
    }

    public void setOnDrawFinishedListener(OnDrawFinishListener onDrawFinishListener){
        this.onDrawFinishListener = onDrawFinishListener;
    }

    /**
     * resetPoints 重置锁屏页面
     */
    public void resetPoints(){
        if(passPoints != null) {
            passPoints.clear();
        }

        if(selectedPoints != null){
            selectedPoints.clear();
        }
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                points[i][j].state = Point.NORMAL;
            }
        }
        isDraw = false;
        inited = false;
        this.postInvalidate();
    }
}

