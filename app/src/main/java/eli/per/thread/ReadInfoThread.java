package eli.per.thread;

import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.util.Random;

import eli.per.testlistview.ControlActivity;

/**
 * 获取显示信息的线程
 *
 * @author eli chang
 */
public class ReadInfoThread extends Thread {

    private static final String TAG = "ReadInfoThread";
    private Context context;
    private ControlActivity.RefreshInfoHandler eRefreshInfoHandler;

    public ReadInfoThread(Context context, ControlActivity.RefreshInfoHandler refreshInfoHandler) {
        this.context = context;
        this.eRefreshInfoHandler = refreshInfoHandler;
    }

    @Override
    public void run() {
        //当前数据总量和时间戳
        long lastTotalRXBytes = getNowRXBytes();
        long lastTimeStamp = System.currentTimeMillis();

        WifiManager wifiService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        while (true) {
            try {
                //暂停1秒
                Thread.sleep(3000);

                //当前数据总量和时间戳
                long nowTotalRXBytes = getNowRXBytes();
                long nowTimeStamp = System.currentTimeMillis();
                //计算网速
                float rate = (nowTotalRXBytes - lastTotalRXBytes) * 1000 / (nowTimeStamp - lastTimeStamp);
                lastTotalRXBytes = nowTotalRXBytes;
                lastTimeStamp = nowTimeStamp;

                rate = new Random().nextFloat() * 1000;

                //获取信号强度
                int rssi = wifiService.getConnectionInfo().getRssi();
                //发送信息
                sendInfo(rate, rssi);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取当前下行数据总量
     *
     * @return
     */
    private long getNowRXBytes() {
        if (context != null)
            return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
        return 0;
    }

    /**
     * 发送信息数据
     *
     * @param rate 网速
     * @param rssi WIFI信号强度
     */
    private void sendInfo(float rate, int rssi) {
        if (eRefreshInfoHandler != null) {
            final Message msg = eRefreshInfoHandler.obtainMessage();
            msg.what = ControlActivity.HANDLER_INFO;
            Bundle bundle = new Bundle();
            bundle.putFloat("RATE", rate);
            bundle.putInt("RSSI", rssi);
            msg.setData(bundle);

            eRefreshInfoHandler.sendMessage(msg);
        }
    }
}
