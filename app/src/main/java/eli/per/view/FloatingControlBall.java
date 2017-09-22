package eli.per.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import eli.per.data.Util;
import eli.per.testlistview.R;

public class FloatingControlBall extends View {

    private static final String TAG = "FloatingControlBall";
    private Context context;
    //画笔
    private Paint paint;
    //画笔透明度
    private int alpha = 200;
    //该组件的布局参数
    private FrameLayout.LayoutParams layoutParams;
    //手机屏幕宽高
    private float windowWidth;
    private float windowHeight;
    //手机状态栏高度
    private float statusBarHeight;
    //组件宽高
    private float viewWidth;
    //组件相对于手机的边距
    private static final float BORDER = 30;
    //触摸组件按下的时间
    private long touchDownTime;
    //触摸组件按下的位置
    private float touchDownX;
    private float touchDownY;
    //拖拽状态标记
    private boolean isDragging = false;
    //船舵位图
    private Bitmap helmBitmap;
    //船舵位图位置
    private RectF helmRect;

    public FloatingControlBall(Context context) {
        this(context, null);
    }

    public FloatingControlBall(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingControlBall(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        //设置组件的点击事件
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //初始化组件的位置参数
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //获取手机的尺寸参数
        windowWidth = Util.getWindowWidth(context);
        windowHeight = Util.getWindowHeight(context);
        statusBarHeight = Util.getStatusBarHeight(context);
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);

        helmBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.helm);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取组件窗口模式和大小
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //当含有自适应的尺寸时，将宽高都设置为130像素
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            widthSpecSize = 140;
            heightSpecSize = 140;
        } else if (widthSpecMode != MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = widthSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            widthSpecSize = heightSpecSize;
        }
        //当宽高不相等时，将宽高设置为最小的一条边
        if (widthSpecSize != heightSpecSize) {
            widthSpecSize = Math.min(widthSpecSize, heightSpecSize);
            heightSpecSize = widthSpecSize;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        //获取组件宽高
        viewWidth = widthSpecSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(0xff000000);
        paint.setAlpha(alpha);
        canvas.drawCircle(viewWidth / 2, viewWidth / 2, viewWidth / 2, paint);

        float helmSize = viewWidth * 3 / 4;
        helmRect = new RectF((viewWidth - helmSize) / 2, (viewWidth - helmSize) / 2, (viewWidth + helmSize) / 2, (viewWidth + helmSize) / 2);
        canvas.drawBitmap(helmBitmap, null, helmRect, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸的位置
        float x = event.getRawX();
        float y = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下时，记录当前时间和位置
            touchDownTime = System.currentTimeMillis();
            touchDownX = x;
            touchDownY = y;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            /**
             * 手指离开时，同时满足以下两个条件：
             *
             * 1.距离手指按下的时间小于500毫秒
             *
             * 2.相对于手指按下的位置没有变化
             *
             * 触发点击事件
             */
            if ((System.currentTimeMillis() - touchDownTime) < 500 && (x == touchDownX) && (y == touchDownY)) {
                Log.i(TAG, "Click...");
            }
            //设置状态为停止拖拽
            if (isDragging) {
                isDragging = false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //当手指移动距离相对于按下的位置偏移10个像素以上时，进一步判断
            if ((Math.sqrt(((x - touchDownX) * (x - touchDownX) + (y - touchDownY) * (y - touchDownY)))) > 10) {
                //当按下并且静止的时间大于500毫秒时，移动组件的位置。否则，将手指按下的时间更新，以取消点击和移动事件
                if ((System.currentTimeMillis() - touchDownTime) > 500) {
                    setPosition((int) x, (int) y);
                    //设置状态为拖拽中
                    if (!isDragging) {
                        isDragging = true;
                    }
                } else {
                    touchDownTime = System.currentTimeMillis();
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置空间的位置
     * @param x
     * @param y
     */
    public void setPosition(int x, int y) {
        //判断是否成功获取到手机屏幕的宽高
        if (windowWidth * windowHeight > 100) {
            //限定组件的X轴坐标
            if ((x - viewWidth / 2) < BORDER) {
                x = (int) (viewWidth / 2 + BORDER);
            } else if ((x + viewWidth / 2) > windowWidth - BORDER) {
                x = (int) (windowWidth - viewWidth / 2 - BORDER);
            }
            //限定组件的Y轴坐标
            if ((y - viewWidth / 2) < statusBarHeight + BORDER + 10) {
                y = (int) (viewWidth / 2 + statusBarHeight + BORDER + 10);
            } else if ((y + viewWidth / 2) > windowHeight - BORDER + 10) {
                y = (int) (windowHeight - viewWidth / 2 - BORDER + 10);
            }
            //设置组件坐标
            layoutParams.setMargins((int) (x - viewWidth / 2), (int) (y - viewWidth), 0, 0);
            this.setLayoutParams(layoutParams);
        }
    }
}