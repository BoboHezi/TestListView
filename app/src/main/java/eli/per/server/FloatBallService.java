package eli.per.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatBallService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        FloatingBallManager.getFloatBallManagerInstance(this).show();
    }

    @Override
    public void onDestroy() {
        FloatingBallManager.getFloatBallManagerInstance(this).close();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
