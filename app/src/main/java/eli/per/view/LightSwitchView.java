package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
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
        bannerColor = getBannerColor(openColor, closeColor, scale);
        paint.setColor(bannerColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius + offset, baseLine, radius - borderWidth - 2, paint);

        //绘制灯
        drawLight(canvas, scale);

        //绘制射线
        if (scale >= 1) {
            drawRay(canvas);
        }
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
     * 绘制灯
     * @param canvas
     */
    private void drawLight(Canvas canvas, float scale) {
        int bannerColor = getBannerColor(closeColor, openColor, scale);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(bannerColor);
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
    }

    /**
     * 绘制光线
     * @param canvas
     */
    private void drawRay(Canvas canvas) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(openColor);

        //ray 1
        canvas.drawLine((float) (leftBorder + offset + radius * (1 - 1 / 1.732)), baseLine + radius / 6, (float)(leftBorder + offset + radius * (1 - 2 / (3 * 1.732))), baseLine + radius / 18, paint);
        //ray 2
        canvas.drawLine((float) (leftBorder + offset + radius * (1 - 1 / 1.732)), baseLine - radius / 3, (float)(leftBorder + offset + radius * (1 - 2 / (3 * 1.732))), baseLine - radius * 5 / 18, paint);
        //ray 3
        canvas.drawLine(leftBorder + offset + radius, baseLine - radius * 5 / 6, leftBorder + offset + radius, baseLine - radius * 11 / 18, paint);
        //ray 4
        canvas.drawLine((float) (leftBorder + offset + radius * (1 + 1 / 1.732)), baseLine - radius / 3, (float)(leftBorder + offset + radius * (1 + 2 / (3 * 1.732))), baseLine - radius * 5 / 18, paint);
        //ray 5
        canvas.drawLine((float) (leftBorder + offset + radius * (1 + 1 / 1.732)), baseLine + radius / 6, (float)(leftBorder + offset + radius * (1 + 2 / (3 * 1.732))), baseLine + radius / 18, paint);
    }

    /**
     * 切换开关
     */
    public void switchLight() {
        isOpen = !isOpen;
        new SwitchAnimationThread().start();
    }

    /**
     * 判断是否处于开启状态
     * @return
     */
    public boolean isOpen() {
        if (offset <= 5) {
            return false;
        } else if ((radius * 2 - offset) <= 5) {
            return true;
        } else {
            return isOpen;
        }
    }

    /**
     * 设置开关状态
     * @param isOpen
     */
    public void setSwitch(boolean isOpen) {
        if (isOpen) {
            offset = (int) (radius * 2);
        } else {
            offset = 0;
        }
        this.isOpen = isOpen;
        postInvalidate();
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
