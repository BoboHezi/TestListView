package eli.per.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import eli.per.testlistview.R;

public class FloatingControlBall extends View {

    private static final String TAG = "FloatingControlBall";
    private Context context;
    //圆点颜色
    private static final int pointColor = 0xff40a8cc;
    //字体颜色
    private int textColor;
    //圆点半径
    private float pointRadius;
    //偏移距离
    private static final float OFFSET_PIX = 5;
    //画笔
    private Paint paint;
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
    //组件宽高
    private float viewWidth;
    //状态标记
    private boolean isControlMode = false;
    //退回中心点的线程
    private BackToPointThread backThread;
    //箭头位图
    private Bitmap arrowHead;
    //位图显示区域
    private Rect rectF;

    public FloatingControlBall(Context context, float viewWidth) {
        super(context);
        this.context = context;
        this.viewWidth = viewWidth;
        this.pointRadius = viewWidth / 2;
        //设置组件的点击事件
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);

        arrowHead = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowhead);
        rectF = new Rect(0, 0, 80, 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取组件宽高
        viewWidth = getMeasuredWidth();

        defaultRadiusX = viewWidth / 2;
        defaultRadiusY = viewWidth / 2;

        radiusX = defaultRadiusX;
        radiusY = defaultRadiusY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isControlMode) {

            //绘制边线
            paint.setColor(0x5553AEBA);
            canvas.drawCircle(viewWidth / 2, viewWidth / 2, viewWidth / 2, paint);
            paint.setColor(0x77085660);
            canvas.drawCircle(viewWidth / 2, viewWidth / 2, viewWidth / 2 - pointRadius, paint);

            //绘制圆点
            paint.setColor(pointColor);
            canvas.drawCircle(radiusX, radiusY, pointRadius, paint);

            //绘制箭头
            canvas.translate(viewWidth / 2 - 40, 5);
            canvas.drawBitmap(arrowHead, null, rectF, paint);
            //复位
            canvas.translate(40 - viewWidth / 2, -5);
            //绘制箭头
            canvas.translate(5, viewWidth / 2 + 40);
            canvas.rotate(-90);
            canvas.drawBitmap(arrowHead, null, rectF, paint);
            //复位
            canvas.rotate(90);
            canvas.translate(-5, -(40 + viewWidth / 2));
            //绘制箭头
            canvas.translate(viewWidth - 5, viewWidth / 2 - 40);
            canvas.rotate(90);
            canvas.drawBitmap(arrowHead, null, rectF, paint);
            //复位
            canvas.rotate(-90);
            canvas.translate(5 - viewWidth, 40 - viewWidth / 2);
            //绘制箭头
            canvas.translate(viewWidth / 2 + 40, viewWidth - 5);
            canvas.rotate(180);
            canvas.drawBitmap(arrowHead, null, rectF, paint);
        } else {
            int unfocusedColor = pointColor & 0xccffffff;
            paint.setColor(unfocusedColor);
            canvas.drawCircle(viewWidth / 2, viewWidth / 2, viewWidth / 2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isControlMode) {
            return false;
        }
        //获得触点的坐标
        float touchX = event.getX();
        float touchY = event.getY();

        //计算触点和中心点的距离
        float distance = (float) Math.sqrt((touchX - defaultRadiusX) * (touchX - defaultRadiusX) + (touchY - defaultRadiusY) * (touchY - defaultRadiusY));

        //当距离大于大圆的半径时，重新计算坐标
        if ((distance + pointRadius * 2) > viewWidth / 2) {

            //X，Y偏移
            float offsetX = viewWidth / 2 - touchX;
            float offsetY = viewWidth / 2 - touchY;

            //角度的正余弦
            float cos = offsetX / distance;
            float sin = offsetY / distance;

            //触点与圆心的连线，和圆弧的交点的位置
            float pointX = viewWidth / 2 - (viewWidth / 2 - (pointRadius * 2)) * cos;
            float pointY = viewWidth / 2 - (viewWidth / 2 - (pointRadius * 2)) * sin;

            //定义圆点的位置
            radiusX = pointX;
            radiusY = pointY;
        } else {
            //定义圆点的位置
            radiusX = touchX;
            radiusY = touchY;
        }

        //当垂直方向的距离过大时，将Y轴设为高度，防止溢出
        if (radiusY > viewWidth) {
            radiusY = viewWidth;
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
                backThread = new BackToPointThread();
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
     * 获取组件尺寸
     *
     * @return
     */
    public float getViewWidth() {
        return viewWidth;
    }

    /**
     * 设置当前是否处于控制模式
     *
     * @param isControlMode
     */
    public void setControlMode(boolean isControlMode) {
        this.isControlMode = isControlMode;
        if (isControlMode) {
            pointRadius = 40;
        }
        postInvalidate();
    }

    /**
     * 计算位移和角度
     */
    private void calculate() {
        //及计算触点和中心点的距离
        offset = (float) Math.sqrt((radiusX - defaultRadiusX) * (radiusX - defaultRadiusX) + (radiusY - defaultRadiusY) * (radiusY - defaultRadiusY));
        //计算正弦值
        float cos = (viewWidth / 2 - radiusX) / offset;
        //计算角度
        if (offset == 0) {
            angle = 0;
        } else {
            angle = (float) ((Math.PI / 2 - Math.asin(cos)) / Math.PI * 180);
            if (radiusY > viewWidth / 2) {
                angle = -angle;
            }
        }
    }

    /**
     * 返回中心点的线程
     */
    private class BackToPointThread extends Thread {
        @Override
        public void run() {
            while (true) {
                //计算方向和速度
                calculate();
                //当圆点和中心点距离小于10.或者线程被中断时，退出循环
                if (offset <= OFFSET_PIX * 2 || this.isInterrupted()) {
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