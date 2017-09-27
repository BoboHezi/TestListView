package eli.per.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import eli.per.data.OnControlStateChangeListener;
import eli.per.data.Velocity;
import eli.per.view.FloatingControlBall;

/**
 * 控制服务类
 * <p>
 * 1.构造控制组件
 * <p>
 * 2.响应控制接口
 * <p>
 * 3.发送控制命令
 *
 * @author eli chang
 */
public class ControlService extends Service implements OnControlStateChangeListener {
    private static final String TAG = "ControlService";

    //速度控制器管理工具
    private FloatingBallManager manager;
    //速度控制器
    private FloatingControlBall floatBall;

    @Override
    public void onCreate() {
        super.onCreate();
        //显示悬浮球
        manager = FloatingBallManager.getInstance(this);

        if (manager != null) {
            manager.show();
            floatBall = manager.getFloatBall();
            if (floatBall != null) {
                floatBall.setOnControlStateChangedListener(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        //隐藏悬浮球
        if (manager != null) {
            manager.close();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onItemSelectedChanged(int index) {
        Log.i(TAG, "Selected Index: " + index);
    }

    @Override
    public void onSwitchStateChanged(boolean isOpen) {
        Log.i(TAG, "LED: " + (isOpen ? "open" : "close"));
    }

    @Override
    public void onVelocityStateChanged(Velocity velocity) {
        Log.i(TAG, "Speed: " + velocity.getSpeed() + "\tDirection: " + velocity.getDirection());
    }
}
