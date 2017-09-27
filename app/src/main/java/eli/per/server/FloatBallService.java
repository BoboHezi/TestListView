package eli.per.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 悬浮球的构造服务
 *
 * @author eli chang
 */
public class FloatBallService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //显示悬浮球
        FloatingBallManager.getInstance(this).show();
    }

    @Override
    public void onDestroy() {
        //隐藏悬浮球
        FloatingBallManager.getInstance(this).close();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
