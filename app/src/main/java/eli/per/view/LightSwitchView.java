package eli.per.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LightSwitchView extends View {

    private static final String TAG = "LightSwitchView";
    private Context context;
    private Paint paint;

    private float windowWidth;
    private float windowHeight;
    private float baseLine;
    private float leftBorder;
    private float rightBorder;

    private final int closeColor = 0xff888888;
    private final int openColor = 0xaa40a8cc;
    private float radius = 70;
    private int offset;
    private boolean isOpen = false;

    public LightSwitchView(Context context) {
        this(context, null);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST) {
            widthSpecSize = (int) radius * 4;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = (int) radius * 2;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);

        windowWidth = getMeasuredWidth();
        windowHeight = getMeasuredHeight();

        baseLine = windowHeight / 2;
        leftBorder = (windowWidth - radius * 4) / 2;
        rightBorder = (windowWidth + radius * 4) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*paint.setColor(0xffaaaaaa);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, windowWidth, windowHeight, paint);*/

        //绘制边界线
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(6);
        canvas.drawArc(leftBorder + 3, 3, leftBorder + radius * 2 - 3, radius * 2 - 3, 90, 180, false, paint);
        canvas.drawLine(leftBorder + radius, 3, leftBorder + radius * 3, 3, paint);
        canvas.drawLine(leftBorder + radius, radius * 2 - 3, leftBorder + radius * 3, radius * 2 - 3, paint);
        canvas.drawArc(leftBorder + radius * 2 + 3, 3, leftBorder + radius * 4 - 3, radius * 2 - 3, -90, 180, false, paint);

        //绘制中间区域
        float scale = offset / radius * 2;
        int bannerColor;

        paint.setColor(0xff888888);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius, baseLine, radius - 6, paint);
        canvas.drawCircle(leftBorder + radius * 3, baseLine, radius - 6, paint);
        canvas.drawRect(leftBorder + radius, 6, leftBorder + radius * 3, radius * 2 - 6, paint);

        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius + offset, baseLine, radius - 10, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isOpen = !isOpen;
            new SwitchAnimationThread().start();
        }
        return super.onTouchEvent(event);
    }

    private class SwitchAnimationThread extends Thread {
        @Override
        public void run() {
            while(true) {
                postInvalidate();
                if (isOpen)
                    offset ++ ;
                else
                    offset --;
                if (offset < 0) {
                    offset = 0;
                    break;
                }
                if (offset > radius * 2) {
                    offset = (int) radius * 2;
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) { }
            }
        }
    }
}
