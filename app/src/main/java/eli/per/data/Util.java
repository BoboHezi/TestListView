package eli.per.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

public class Util {

    /**
     * Dip转像素
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 像素转Dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断屏幕方向
     *
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static float getWindowWidth(Context context) {
        float width = 0;
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
        } catch (Exception e) {
        }

        return width;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static float getWindowHeight(Context context) {
        float height = 0;
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            height = displayMetrics.heightPixels;
        } catch (Exception e) {
        }

        return height;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static float getStatusBarHeight(Context context) {
        float statusBarHeight = 0;
        try {
            int resourceID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceID > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceID);
            }
        } catch (Exception e) {
        }
        return statusBarHeight;
    }

    /**
     * 获取存储的位置
     *
     * @param context
     * @return
     */
    public static final Coordinate readPosition(Context context) {
        Coordinate position = null;
        float defaultRawX = 100;
        float defaultRawY = getWindowHeight(context) - getStatusBarHeight(context) - 200;
        try {
            SharedPreferences preferences = context.getSharedPreferences("ControlBallPosition", Context.MODE_PRIVATE);
            if (preferences != null) {
                float rawX = preferences.getFloat("rawX", defaultRawX);
                float rawY = preferences.getFloat("rawY", defaultRawY);
                position = new Coordinate(rawX, rawY);
            }
        } catch (Exception e) {
            position = new Coordinate(defaultRawX, defaultRawY);
        }
        return position;
    }

    /**
     * 写入当前位置
     *
     * @param context
     * @param rawX
     * @param rawY
     */
    public static final void writePosition(Context context, float rawX, float rawY) {
        try {
            SharedPreferences preferences = context.getSharedPreferences("ControlBallPosition", Context.MODE_PRIVATE);
            if (preferences != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("rawX", rawX);
                editor.putFloat("rawY", rawY);
                editor.commit();
            }
        } catch (Exception e) {
        }
    }
}