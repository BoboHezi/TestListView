package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import eli.per.testlistview.R;

public class ResourceLoadingView extends View {

    private static final String TAG = "ResourceLoadingView";
    private Context context;
    private Paint paint;

    //进度
    private int progress;
    //圆颜色
    private int circleColor;
    //圆半径
    private float circleRadius;

    //组件宽度
    private float windowWidth;
    //组件高度
    private float windowHeight;
    //显示区域左边界
    private float leftBorder;
    //显示区域右边界
    private float rightBorder;

    //第一个圆圆心X位置
    private float circle1RadiusX;
    //第二个圆圆心X位置
    private float circle2RadiusX;
    //第三个圆圆心X位置
    private float circle3RadiusX;
    //圆心Y位置
    private float circleRadiusY;

    public ResourceLoadingView(Context context) {
        this(context, null);
    }

    public ResourceLoadingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ResourceLoadingView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.styleable_progress_loading);
        circleColor = ta.getColor(R.styleable.styleable_progress_loading_circleColor, 0xaa40a8cc);
        circleRadius = ta.getFloat(R.styleable.styleable_progress_loading_circleRadius, 50);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        windowWidth = getMeasuredWidth();
        windowHeight = getMeasuredHeight();

        circleRadiusY = windowHeight / 2;
        leftBorder = (windowWidth - circleRadius * 12) / 2;
        rightBorder = (windowWidth + circleRadius * 12) / 2;

        Log.i(TAG, "windowWidth: " + windowWidth + "\tcircleRadius: " + circleRadius + "\tleftBorder: " + leftBorder);
        new CalculatePositionThread().start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);

        //Draw Circle2
        paint.setAlpha(255);
        canvas.drawCircle(circle2RadiusX, circleRadiusY, circleRadius, paint);

        //Draw Circle1
        int alpha = (int) (((circle1RadiusX - (leftBorder + circleRadius)) / (circleRadius * 4)) * 255);
        paint.setAlpha(alpha);
        canvas.drawCircle(circle1RadiusX, circleRadiusY, circleRadius, paint);


        //Draw Circle3
        paint.setAlpha(255 - alpha);
        canvas.drawCircle(circle3RadiusX, circleRadiusY, circleRadius, paint);

    }

    private class CalculatePositionThread extends Thread {
        @Override
        public void run() {

            circle1RadiusX = leftBorder + circleRadius;
            circle2RadiusX = leftBorder + circleRadius * 5;
            circle3RadiusX = leftBorder + circleRadius * 7;

            int offset1 = 4;
            int offset2 = 2;

            postInvalidate();
            while(true) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
                circle1RadiusX += offset1;
                circle2RadiusX += offset2;
                circle3RadiusX += offset1;

                postInvalidate();

                if (circle1RadiusX >= leftBorder + circleRadius * 5) {
                    circle1RadiusX = leftBorder + circleRadius;
                    circle2RadiusX = leftBorder + circleRadius * 5;
                    circle3RadiusX = leftBorder + circleRadius * 7;
                }
            }
        }
    }
}
