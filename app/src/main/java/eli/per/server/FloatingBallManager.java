package eli.per.server;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import eli.per.data.Coordinate;
import eli.per.data.Util;
import eli.per.view.FloatingControlBall;

/**
 * 悬浮球控制类
 *
 * @author eli chang
 */
public class FloatingBallManager implements View.OnTouchListener {
    private static final String TAG = "FloatingBallManager";

    private Context context;
    private WindowManager windowManager;
    private LayoutParams layoutParams;
    private static FloatingBallManager floatBallManager;
    //悬浮球
    private static FloatingControlBall floatBall;
    //悬浮球最小化的尺寸
    private static final int minWidth = 120;
    //悬浮球最大化的尺寸
    private static final int maxWidth = 500;
    //手机状态栏高度
    private float statusBarHeight;
    private float windowWidth;
    private float windowHeight;
    //触摸组件按下的位置
    private float touchDownX;
    private float touchDownY;
    //触摸组件按下的时间
    private long touchDownTime;
    //控制状态标记
    private boolean isControlMode = false;

    /**
     * 私有化构造方法
     *
     * @param context
     */
    private FloatingBallManager(Context context) {
        this.context = context;
        //获取系统窗体服务
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //初始化悬浮球
        floatBall = new FloatingControlBall(context, minWidth);
        //设置悬浮球的触摸事件
        floatBall.setOnTouchListener(this);
        //获取手机窗体像素值
        windowWidth = Util.getWindowWidth(context);
        windowHeight = Util.getWindowHeight(context);
        statusBarHeight = Util.getStatusBarHeight(context);
    }

    /**
     * 获取本类的实例化对象
     *
     * @param context
     * @return
     */
    public static FloatingBallManager getInstance(Context context) {
        if (floatBallManager == null)
            floatBallManager = new FloatingBallManager(context);
        return floatBallManager;
    }

    /**
     * 初始化并显示悬浮球
     */
    public void show() {
        if (layoutParams == null) {
            //设置悬浮球布局信息
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = (int) floatBall.getViewWidth();
            layoutParams.height = (int) floatBall.getViewWidth();
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.type = LayoutParams.TYPE_TOAST;
            layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.format = PixelFormat.TRANSPARENT;
            //读取并设置存储的位置信息
            Coordinate position = Util.readPosition(context);
            layoutParams.x = (int) (position.getRawX() - 60);
            layoutParams.y = (int) (position.getRawY() - 60);
        }
        try {
            //将悬浮球添加到窗口
            windowManager.addView(floatBall, layoutParams);
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏悬浮球
     */
    public void close() {
        if (windowManager != null && floatBall != null) {
            try {
                windowManager.removeView(floatBall);
            } catch (Exception e) {
            }
        }
    }

    public FloatingControlBall getFloatBall() {
        return this.floatBall;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        //获取触摸的位置
        float x = event.getRawX();
        float y = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下时，记录当前时间和位置
            touchDownTime = System.currentTimeMillis();
            touchDownX = x;
            touchDownY = y;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //时，进一步判断
            /**
             * 满足以下两个条件时，进入拖拽状态
             *
             * 1.当手指移动距离相对于按下的位置偏移10个像素以上
             *
             * 2.当按下并且静止的时间大于500毫秒
             */
            float offset = (float) Math.sqrt(((x - touchDownX) * (x - touchDownX) + (y - touchDownY) * (y - touchDownY)));
            if (offset > 10 && (System.currentTimeMillis() - touchDownTime) > 200) {
                if (!isControlMode) {
                    //重新更新悬浮球的位置
                    setPosition((int) x - 60, (int) y - 60);
                }
            } else if ((System.currentTimeMillis() - touchDownTime) <= 200) {
                touchDownTime = System.currentTimeMillis();
            }
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
            if ((System.currentTimeMillis() - touchDownTime) <= 200 && (x == touchDownX) && (y == touchDownY)) {
                isControlMode = !isControlMode;
                if (isControlMode) {
                    setPosition(x - maxWidth / 2, y - maxWidth / 2);
                } else {
                    setPosition(x - minWidth / 2, y - minWidth / 2);
                }
                setControlMode(isControlMode);
            }

            if (!isControlMode) {
                //存储当前悬浮球的位置
                writePosition((int) x, (int) y);
            }
        }
        return false;
    }

    /**
     * 设置组件的位置
     *
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        layoutParams.x = (int) x;
        layoutParams.y = (int) (y - statusBarHeight);
        windowManager.updateViewLayout(floatBall, layoutParams);
    }

    /**
     * 设置悬浮球是否处于控制状态
     *
     * @param isControlMode
     */
    private void setControlMode(boolean isControlMode) {
        if (isControlMode) {
            //控制状态下最大化悬浮球
            layoutParams.width = maxWidth;
            layoutParams.height = maxWidth;
            windowManager.updateViewLayout(floatBall, layoutParams);
        } else {
            //移动状态下最小化悬浮球
            layoutParams.width = minWidth;
            layoutParams.height = minWidth;
            windowManager.updateViewLayout(floatBall, layoutParams);
        }
        floatBall.setControlMode(isControlMode);
    }

    /**
     * 计算并存储悬浮球当前位置
     *
     * @param x
     * @param y
     */
    private void writePosition(float x, float y) {
        //根据当前状态，选择当前组件的大小
        float width = isControlMode ? maxWidth : minWidth;
        //将坐标偏移到左上角
        x = (int) (x - width / 2);
        y = (int) (y - width / 2);
        //计算坐标是否溢出窗口，溢出则重新计算位置
        if (x < 0) {
            x = 0;
        } else if (x + width > windowWidth) {
            x = (int) (windowWidth - width);
        }

        if (y - width < 0) {
            y = 0;
        } else if (y + width > windowHeight) {
            y = (int) (windowHeight - width);
        }

        //将欧标偏移到组件中间
        x = (int) (x + width / 2);
        y = (int) (y + width / 2);
        Util.writePosition(context, x, y);
    }
}