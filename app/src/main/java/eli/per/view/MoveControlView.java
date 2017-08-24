package eli.per.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import eli.per.data.OnVelocityStateChangeListener;
import eli.per.data.Velocity;

public class MoveControlView extends View {

    private static final String TAG = "MoveControlView";
    //背景颜色
    private static final int BACK_COLOR = 0x77000000;
    //描边颜色
    private static final int BORDER_LINE_COLOR = 0xff000000;
    //原点颜色
    private static final int CIRCLE_COLOR = 0xff000000;
    //字体颜色
    private static final int TEXT_COLOR = 0xff000000;
    //画笔宽度
    private static final int STROKE_WIDTH = 3;
    //圆点半径
    private static final int POINT_RADIUS = 30;
    //偏移距离
    private static final float OFFSET_PIX = 5;

    //画笔
    private Paint paint;
    //绘制区域宽度
    private float width;
    //绘制区域高度
    private float height;
    //默认圆心坐标X值
    private float defaultRadiusX;
    //默认圆心坐标Y值
    private float defaultRadiusY;
    //圆心坐标X值
    private float radiusX;
    //圆心坐标Y值
    private float radiusY;
    //圆点和中心点的距离
    private float offset;
    //圆点角度
    private float angle;
    //方向
    private Velocity velocity;
    //速度值改变的接口
    private OnVelocityStateChangeListener velocityStateChangedListener;
    //退回中心点的线程
    private BackToPoint backThread;

    public MoveControlView(Context context) {
        this(context, null);
    }

    public MoveControlView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MoveControlView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);
        velocity = new Velocity(0, Velocity.Direction.front);
    }

    /**
     * 设置速度变化监听
     *
     * @param velocityStateChangedListener
     */
    public void setOnVelocityStateChangedListener(OnVelocityStateChangeListener velocityStateChangedListener) {
        this.velocityStateChangedListener = velocityStateChangedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取当前组件宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //重置宽高比为2:1
        if (width > height * 2) {
            width = height * 2;
        } else if (width < height * 2) {
            height = width / 2;
        }
        //计算中心点的坐标
        defaultRadiusX = width / 2;
        defaultRadiusY = height;
        //设置起始圆点的位置
        radiusX = defaultRadiusX;
        radiusY = defaultRadiusY;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(TEXT_COLOR);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        //绘制文字
        canvas.drawText(calculate(), 0, 30, paint);

        paint.setColor(CIRCLE_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);

        //绘制边线
        canvas.drawCircle(width / 2, height, height - STROKE_WIDTH, paint);
        canvas.drawLine(STROKE_WIDTH, height - STROKE_WIDTH / 2, width - STROKE_WIDTH, height - STROKE_WIDTH / 2, paint);

        //绘制圆点
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radiusX, radiusY, POINT_RADIUS, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //获得触点的坐标
        float touchX = event.getX();
        float touchY = event.getY();

        //及计算触点和中心点的距离
        float distance = (float) Math.sqrt((touchX - defaultRadiusX) * (touchX - defaultRadiusX) + (touchY - defaultRadiusY) * (touchY - defaultRadiusY));

        //当距离大于大圆的半径时，重新计算坐标
        if ((distance + POINT_RADIUS) > height) {

            //X，Y偏移
            float offsetX = height - touchX;
            float offsetY = height - touchY;

            //角度的正余弦
            float cos = offsetX / distance;
            float sin = offsetY / distance;

            //触点与圆心的连线，和圆弧的交点的位置
            float pointX = height - (height - (POINT_RADIUS + STROKE_WIDTH)) * cos;
            float pointY = height - (height - (POINT_RADIUS + STROKE_WIDTH)) * sin;

            //定义圆点的位置
            radiusX = pointX;
            radiusY = pointY;
        } else {
            //定义圆点的位置
            radiusX = touchX;
            radiusY = touchY;
        }

        //当垂直方向的距离过大时，将Y轴设为高度，防止溢出
        if (radiusY > height) {
            radiusY = height;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当手指点击时
                if (backThread != null) {
                    //取消正在运行的返回中心点的任务
                    backThread.interrupt();
                    backThread = null;
                }
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
                //当手指抬起后，让小球回到中心点
                backThread = new BackToPoint();
                backThread.start();
                break;

            case MotionEvent.ACTION_MOVE:
                //更新视图
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 计算位移和角度
     */
    private String calculate() {
        //及计算触点和中心点的距离
        offset = (float) Math.sqrt((radiusX - defaultRadiusX) * (radiusX - defaultRadiusX) + (radiusY - defaultRadiusY) * (radiusY - defaultRadiusY));
        //计算正弦值
        float cos = (height - radiusX) / offset;
        //计算角度
        if (offset == 0) {
            angle = 0;
        } else {
            angle = (float) ((Math.PI / 2 - Math.asin(cos)) / Math.PI * 180);
        }
        //计算对应的速度
        int speed = (int) (offset / (height - POINT_RADIUS) * 15);
        Velocity.Direction direction = Velocity.Direction.front;
        //计算方向
        if (angle > 0 && angle < 70) {
            direction = Velocity.Direction.left;
        } else if (angle >= 70 && angle < 110) {
            direction = Velocity.Direction.front;
        } else if (angle >= 110 && angle <= 180) {
            direction = Velocity.Direction.right;
        }

        //当速度或者方向发生变化时，调用接口
        if ((Math.abs(velocity.getSpeed() - speed)) >= 1 || velocity.getDirection() != direction) {
            velocity.setDirection(direction);
            velocity.setSpeed(speed);

            if (backThread == null && velocityStateChangedListener != null)
                velocityStateChangedListener.onVelocityStateChanged(velocity);
        }

        return "Angle: " + angle;
    }

    /**
     * 返回中心点的线程
     */
    private class BackToPoint extends Thread {
        @Override
        public void run() {
            while (true) {
                //当圆点和中心点距离小于10.或者线程被中断时，退出循环
                if (offset <= OFFSET_PIX * 2 || this.isInterrupted()) {
                    //退出循环之前调用状态改变的接口
                    if (velocityStateChangedListener != null) {
                        velocityStateChangedListener.onVelocityStateChanged(new Velocity(0, Velocity.Direction.front));
                    }
                    break;
                }
                //计算角度对应的弧度
                float radius = (float) Math.toRadians(angle);
                //计算正余弦值
                float cos = (float) Math.cos(radius);
                float sin = (float) Math.sin(radius);
                //计算对应角度下的X，Y轴偏移
                float offsetX = OFFSET_PIX * cos;
                float offsetY = OFFSET_PIX * sin;
                //设置对应的偏移
                radiusX += offsetX;
                radiusY += offsetY;

                //当偏移的X轴或者Y轴接近中心点时，将位置设置到中心点
                if (Math.abs(radiusX - defaultRadiusX) < OFFSET_PIX * 2)
                    radiusX = defaultRadiusX;
                if (Math.abs(radiusY - defaultRadiusY) < OFFSET_PIX * 2)
                    radiusY = defaultRadiusY;

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
                postInvalidate();
            }
        }
    }
}
