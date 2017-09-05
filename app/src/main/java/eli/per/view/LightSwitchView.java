package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import eli.per.data.OnControlStateChangeListener;
import eli.per.testlistview.R;

public class LightSwitchView extends View {

    private static final String TAG = "LightSwitchView";
    private Context context;
    private Paint paint;

    //组件宽度
    private float windowWidth;
    //组件高度
    private float windowHeight;
    //边框宽度
    private float borderWidth;
    //边框颜色
    private int borderColor;
    //关闭时的颜色
    private int closeColor;
    //开启时的颜色
    private int openColor;
    //小球半径
    private float radius;
    //偏移
    private int offset;
    //组件基线（Y轴中心）
    private float baseLine;
    //绘制区域左边界
    private float leftBorder;
    //开关状态
    private boolean isOpen = false;
    //开关改变的接口
    private OnControlStateChangeListener changeListener;

    public LightSwitchView(Context context) {
        this(context, null);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LightSwitchView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        //获取配置值
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.styleable_light_switch);
        borderWidth = ta.getFloat(R.styleable.styleable_light_switch_switch_borderWidth, 2);
        borderColor = ta.getColor(R.styleable.styleable_light_switch_switch_borderColor, 0xffffffff);
        closeColor = ta.getColor(R.styleable.styleable_light_switch_switch_closeColor, 0xff888888);
        openColor = ta.getColor(R.styleable.styleable_light_switch_switch_openColor, 0x40a8cc);
        radius = ta.getFloat(R.styleable.styleable_light_switch_switch_circleRadius, 70);

        //设置点击事件
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //运行动画
                isOpen = !isOpen;
                new SwitchAnimationThread().start();

                if (changeListener != null) {
                    changeListener.onSwitchStateChanged(isOpen);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取组件窗口模式和大小
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //当宽高设置为wrap_content，重新定义其大小
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            widthSpecSize = (int) radius * 4;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = (int) radius * 2;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);

        //获取当前组件宽高
        windowWidth = getMeasuredWidth();
        windowHeight = getMeasuredHeight();

        //计算基线和左边界
        baseLine = windowHeight / 2;
        leftBorder = (windowWidth - radius * 4) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制描边
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
        //获取渐变色
        int bannerColor = getBannerColor(closeColor, openColor, scale);
        paint.setColor(bannerColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(leftBorder + radius, baseLine, radius - borderWidth, paint);
        canvas.drawCircle(leftBorder + radius * 3, baseLine, radius - borderWidth, paint);
        canvas.drawRect(leftBorder + radius, borderWidth, leftBorder + radius * 3, radius * 2 - borderWidth, paint);

        //绘制小球
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
     * 获取渐变色
     *
     * @param scale
     * @return
     */
    private static int getBannerColor(int startColor, int endColor, float scale) {
        int bannerColor = startColor;
        try {
            //开始色的三个通道
            int startBluePart = startColor - (startColor >>> 8 << 8);
            int startGreenPart = (startColor >>> 8) - (startColor >>> 16 << 8);
            int startRedPart = startColor >>> 16;

            //结束色的三个通道
            int endBluePart = endColor - (endColor >>> 8 << 8);
            int endGreenPart = (endColor >>> 8) - (endColor >>> 16 << 8);
            int endRedPart = endColor >>> 16;

            //计算对应比例下，各通道的取值
            Float bannerRedPart_float = new Float(startRedPart + (endRedPart - startRedPart) * scale);
            Float bannerGreenPart_float = new Float(startGreenPart + (endGreenPart - startGreenPart) * scale);
            Float bannerBluePart_float = new Float(startBluePart + (endBluePart - startBluePart) * scale);

            int bannerRedPart = bannerRedPart_float.intValue();
            int bannerGreenPart = bannerGreenPart_float.intValue();
            int bannerBluePart = bannerBluePart_float.intValue();

            //将三个通道合成
            bannerColor = (bannerRedPart << 16) + (bannerGreenPart << 8) + bannerBluePart;
        } catch (Exception e) {
        }
        return bannerColor;
    }

    /**
     * 绘制灯
     *
     * @param canvas
     */
    private void drawLight(Canvas canvas, float scale) {
        //计算渐变色
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
     *
     * @param canvas
     */
    private void drawRay(Canvas canvas) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(openColor);
        //ray 1
        canvas.drawLine((float) (leftBorder + offset + radius * (1 - 1 / 1.732)), baseLine + radius / 6, (float) (leftBorder + offset + radius * (1 - 2 / (3 * 1.732))), baseLine + radius / 18, paint);
        //ray 2
        canvas.drawLine((float) (leftBorder + offset + radius * (1 - 1 / 1.732)), baseLine - radius / 3, (float) (leftBorder + offset + radius * (1 - 2 / (3 * 1.732))), baseLine - radius * 5 / 18, paint);
        //ray 3
        canvas.drawLine(leftBorder + offset + radius, baseLine - radius * 5 / 6, leftBorder + offset + radius, baseLine - radius * 11 / 18, paint);
        //ray 4
        canvas.drawLine((float) (leftBorder + offset + radius * (1 + 1 / 1.732)), baseLine - radius / 3, (float) (leftBorder + offset + radius * (1 + 2 / (3 * 1.732))), baseLine - radius * 5 / 18, paint);
        //ray 5
        canvas.drawLine((float) (leftBorder + offset + radius * (1 + 1 / 1.732)), baseLine + radius / 6, (float) (leftBorder + offset + radius * (1 + 2 / (3 * 1.732))), baseLine + radius / 18, paint);
    }

    /**
     * 判断是否处于开启状态
     *
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
     *
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

    /**
     * 设置开关变化监听
     *
     * @param changedListener
     */
    public void setOnControlStateChangedListener(OnControlStateChangeListener changedListener) {
        this.changeListener = changedListener;
    }

    /**
     * 开关动画效果的实现线程
     */
    private class SwitchAnimationThread extends Thread {
        @Override
        public void run() {
            while (true) {
                postInvalidate();
                if (isOpen) {
                    //打开状态下，偏移逐渐增大
                    offset++;
                } else {
                    //关闭状态下，偏移逐渐减小
                    offset--;
                }

                //当偏移到达两端，结束线程
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
                } catch (InterruptedException e) {
                }
            }
        }
    }
}