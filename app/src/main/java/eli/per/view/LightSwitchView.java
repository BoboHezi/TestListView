package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import eli.per.testlistview.R;

public class LightSwitchView extends View {

    private static final String TAG = "LightSwitchView";
    private Context context;
    private Paint paint;

    private float windowWidth;
    private float windowHeight;

    private float borderWidth;
    private int borderColor;
    private int closeColor;
    private int openColor;
    private float radius;
    private int circleColor;
    private int offset;

    private float baseLine;
    private float leftBorder;
    private boolean isOpen = false;

    public LightSwitchView(Context context) {
        this(context, null);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.styleable_light_switch);
        borderWidth = ta.getFloat(R.styleable.styleable_light_switch_borderWidth, 2);
        borderColor = ta.getColor(R.styleable.styleable_light_switch_borderColor, 0xffffffff);
        closeColor = ta.getColor(R.styleable.styleable_light_switch_closeColor, 0xff888888);
        openColor = ta.getColor(R.styleable.styleable_light_switch_openColor, 0x40a8cc);
        radius = ta.getFloat(R.styleable.styleable_light_switch_switch_circleRadius, 70);
        circleColor = ta.getColor(R.styleable.styleable_light_switch_switch_circleColor, 0xffffffff);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*paint.setColor(0xffaaaaaa);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, windowWidth, windowHeight, paint);*/

        //绘制边界线
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        if (borderWidth != 0) {
            canvas.drawArc(leftBorder + borderWidth / 2, borderWidth / 2, leftBorder + radius * 2 - borderWidth / 2, radius * 2 - borderWidth / 2, 90, 180, false, paint);
            canvas.drawLine(leftBorder + radius, borderWidth / 2, leftBorder + radius * 3, borderWidth / 2, paint);
            canvas.drawLine(leftBorder + radius, radius * 2 - borderWidth / 2, leftBorder + radius * 3, radius * 2 - borderWidth / 2, paint);
            canvas.drawArc(leftBorder + radius * 2 + borderWidth / 2, borderWidth / 2, leftBorder + radius * 4 - borderWidth / 2, radius * 2 - borderWidth / 2, -90, 180, false, paint);
        }

        //绘制中间区域
        float scale = offset / (radius * 2);
        int bannerColor = getBannerColor(closeColor, openColor, scale);
        paint.setColor(bannerColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius, baseLine, radius - borderWidth, paint);
        canvas.drawCircle(leftBorder + radius * 3, baseLine, radius - borderWidth, paint);
        canvas.drawRect(leftBorder + radius, borderWidth, leftBorder + radius * 3, radius * 2 - borderWidth, paint);

        //绘制中间的圆
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius + offset, baseLine, radius - borderWidth - 2, paint);

        //绘制灯
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(0xffffffff);
        //circle
        canvas.drawArc(leftBorder + offset + radius * 2 / 3, baseLine - radius / 2, leftBorder + offset + radius * 4 / 3, baseLine + radius / 6, 120, 300, false, paint);
        //Line 1
        canvas.drawLine(leftBorder + offset + radius * 8 / 9, baseLine - radius / 6, leftBorder + offset + radius * 10 / 9, baseLine - radius / 6, paint);
        //Line 2
        canvas.drawLine(leftBorder + offset + radius, baseLine - radius / 6, leftBorder + offset + radius, baseLine + radius / 2, paint);
        //Line 3
        canvas.drawLine(leftBorder + offset + radius * 7 / 6, (float) (baseLine + radius * 0.732 / 6), leftBorder + offset + radius * 7 / 6, baseLine + radius / 2, paint);
        //Line 4
        canvas.drawLine(leftBorder + offset + radius * 5 / 6, (float) (baseLine + radius * 0.732 / 6), leftBorder + offset + radius * 5 / 6, baseLine + radius / 2, paint);
        //Line 5
        canvas.drawLine(leftBorder + offset + radius * 5 / 6, baseLine + radius / 3, leftBorder + offset + radius * 7 / 6, baseLine + radius / 3, paint);
        //Line 6
        canvas.drawLine(leftBorder + offset + radius * 5 / 6, baseLine + radius / 2, leftBorder + offset + radius * 7 / 6, baseLine + radius / 2, paint);
        //ray 1
    }

    /**
     * 获取过度色
     * @param scale
     * @return
     */
    private static int getBannerColor(int startColor, int endColor, float scale) {
        int bannerColor = startColor;
        try {
            int closeBluePart = startColor - (startColor >>> 8 << 8);
            int closeGreenPart = (startColor >>> 8) - (startColor >>> 16 << 8);
            int closeRedPart = startColor >>> 16;

            int openBluePart = endColor - (endColor >>> 8 << 8);
            int openGreenPart = (endColor >>> 8) - (endColor >>> 16 << 8);
            int openRedPart = endColor >>> 16;

            Float bannerRedPart_float = new Float(closeRedPart + (openRedPart - closeRedPart) * scale);
            Float bannerGreenPart_float = new Float(closeGreenPart + (openGreenPart - closeGreenPart) * scale);
            Float bannerBluePart_float = new Float(closeBluePart + (openBluePart - closeBluePart) * scale);

            int bannerRedPart = bannerRedPart_float.intValue();
            int bannerGreenPart = bannerGreenPart_float.intValue();
            int bannerBluePart = bannerBluePart_float.intValue();

            bannerColor = (bannerRedPart << 16) + (bannerGreenPart << 8) + bannerBluePart;
        } catch (Exception e) {
        }
        return bannerColor;
    }

    /**
     * 切换开关
     */
    public void switchLight() {
        isOpen = !isOpen;
        new SwitchAnimationThread().start();
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
