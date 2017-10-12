package eli.per.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LedBrightnessView extends View {

    private static final String TAG = "LedBrightnessView";
    private static final int COUNT = 100;
    private Context context;
    private Paint paint;
    //线条颜色
    private final int positiveLineColor = 0xff09938c;
    private final int negativeLineColor = 0xff888888;
    //线条高度
    private final float lineHeight = 3;
    private float lineWidth;
    //圆点半径
    private final float pointRadius = 15;
    //圆点颜色
    private int pointColor = 0xffffffff;
    //小点偏移距离
    private final int offset = 16;
    //偏移对应的正弦值
    private float sin;
    //太阳对应的X坐标
    private float sunX;
    //组件宽度
    private float viewWidth;
    //组件高度
    private float viewHeight;
    //圆点位置X值
    private float pointRadiusX;
    //圆点位置Y值
    private float pointRadiusY;
    //亮度
    private int brightness = 30;

    public LedBrightnessView(Context context) {
        this(context, null);
    }

    public LedBrightnessView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LedBrightnessView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取组件宽高
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        pointRadiusY = viewHeight / 2;
        //初始化线条的长度
        lineWidth = viewWidth - viewHeight * 3;
        //初始化圆点的位置
        pointRadiusX = brightness * lineWidth / COUNT + viewHeight * 3 / 2;

        sunX = viewWidth - viewHeight * 3 / 4;
        sin = (float) (13 * Math.sin(45));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制小圆
        paint.setColor(positiveLineColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawCircle(viewHeight * 3 / 4, pointRadiusY, 8, paint);
        //绘制太阳
        canvas.drawCircle(sunX, pointRadiusY, 9, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(sunX, pointRadiusY - offset, 2, paint);
        canvas.drawCircle(sunX, pointRadiusY + offset, 2, paint);
        canvas.drawCircle(sunX - offset, pointRadiusY, 2, paint);
        canvas.drawCircle(sunX + offset, pointRadiusY, 2, paint);
        canvas.drawCircle(sunX + sin, pointRadiusY - sin, 2, paint);
        canvas.drawCircle(sunX - sin, pointRadiusY - sin, 2, paint);
        canvas.drawCircle(sunX - sin, pointRadiusY + sin, 2, paint);
        canvas.drawCircle(sunX + sin, pointRadiusY + sin, 2, paint);

        //绘制线条
        paint.setStrokeWidth(lineHeight);
        canvas.drawLine(viewHeight * 3 / 2 + 1, pointRadiusY, pointRadiusX, pointRadiusY, paint);
        paint.setColor(negativeLineColor);
        canvas.drawLine(pointRadiusX, pointRadiusY, (viewWidth + lineWidth) / 2 - 1, pointRadiusY, paint);

        //绘制小球
        paint.setColor(pointColor);
        canvas.drawCircle(pointRadiusX, pointRadiusY, pointRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //得到触摸的X坐标
        pointRadiusX = event.getX();
        //防止小球位置溢出
        if (pointRadiusX < viewHeight * 3 / 2 + pointRadius) {
            pointRadiusX = viewHeight * 3 / 2 + pointRadius;
        } else if (pointRadiusX > viewHeight * 3 / 2 + lineWidth - pointRadius) {
            pointRadiusX = viewHeight * 3 / 2 + lineWidth - pointRadius;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            pointColor = 0xffffffff;
            getBrightness();
        } else {
            pointColor = 0xff095954;
        }

        postInvalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 设置亮度
     *
     * @param brightness
     */
    public void setBrightness(int brightness) {
        this.brightness = brightness;
        pointRadiusX = brightness * lineWidth / COUNT + viewHeight * 3 / 2;
        postInvalidate();
    }

    /**
     * 获取当前亮度值
     *
     * @return
     */
    public int getBrightness() {
        brightness = (int) ((pointRadiusX - viewHeight * 3 / 2 - pointRadius) * COUNT / (lineWidth - pointRadius * 2));
        return brightness;
    }
}
