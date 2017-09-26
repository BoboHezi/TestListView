package eli.per.server;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import eli.per.data.Util;
import eli.per.view.FloatingControlBall;

/**
 * 悬浮球控制类
 *
 * @author eli chang
 */
public class FloatingBallManager implements View.OnTouchListener {
    private static final String TAG = "FloatingBallManager";
    private static FloatingBallManager floatBallManager;

    private Context context;
    private WindowManager windowManager;
    private LayoutParams layoutParams;
    //悬浮球
    private FloatingControlBall floatBall;

    private static final int minWidth = 120;
    private static final int maxWidth = 500;

    private float statusBarHeight;
    //触摸组件按下的位置
    private float touchDownX;
    private float touchDownY;
    private long touchDownTime;
    //控制状态标记
    private boolean isControlMode = false;

    private FloatingBallManager(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        floatBall = new FloatingControlBall(context, minWidth);
        floatBall.setOnTouchListener(this);

        statusBarHeight = Util.getStatusBarHeight(context);
    }

    public static FloatingBallManager getFloatBallManagerInstance(Context context) {
        if (floatBallManager == null)
            floatBallManager = new FloatingBallManager(context);
        return floatBallManager;
    }

    public void show() {
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = (int) floatBall.getViewWidth();
            layoutParams.height = (int) floatBall.getViewWidth();
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.type = LayoutParams.TYPE_TOAST;
            layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.format = PixelFormat.TRANSPARENT;
        }
        windowManager.addView(floatBall, layoutParams);
    }

    public void close() {
        if (windowManager != null && floatBall != null) {
            windowManager.removeView(floatBall);
        }
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
                    setPosition((int) x - 50, (int) y - 50);
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

    private void setControlMode(boolean isControlMode) {
        if (isControlMode) {
            layoutParams.width = maxWidth;
            layoutParams.height = maxWidth;
            windowManager.updateViewLayout(floatBall, layoutParams);
        } else {
            layoutParams.width = minWidth;
            layoutParams.height = minWidth;
            windowManager.updateViewLayout(floatBall, layoutParams);
        }
        floatBall.setControlMode(isControlMode);
    }
}
