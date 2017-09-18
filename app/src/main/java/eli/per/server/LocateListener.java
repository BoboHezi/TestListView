package eli.per.server;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import eli.per.testlistview.ControlActivity;

/**
 * 定位服务
 *
 * @author eli chang
 */
public class LocateListener implements LocationListener {

    private static final String TAG = "LocateListener";

    private Context context;
    private LocationManager eLocationManager;
    private ControlActivity.RefreshInfoHandler eRefreshInfoHandler;

    public LocateListener(Context context, ControlActivity.RefreshInfoHandler refreshInfoHandler) {
        this.context = context;
        this.eRefreshInfoHandler = refreshInfoHandler;
        initLocationManager();
    }

    /**
     * 初始化定位服务
     */
    private void initLocationManager() {
        eLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = eLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                sendLocationInfo(location);
                Log.i(TAG, "Latitude: " + location.getLatitude() + "\tLongitude: " + location.getLongitude());
            }
            String provider = getProvider();
            eLocationManager.requestLocationUpdates(provider, 1000, 10, this);
        }
    }

    /**
     * 获取数据配置字符串
     *
     * @return
     */
    private String getProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = eLocationManager.getBestProvider(criteria, true);
        return provider;
    }

    /**
     * 通过Handler，post位置信息
     *
     * @param location
     */
    public void sendLocationInfo(Location location) {
        if (eRefreshInfoHandler != null && location != null) {
            final Message msg = eRefreshInfoHandler.obtainMessage();
            msg.what = ControlActivity.HANDLER_GET_POSITION;
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", location.getLatitude());
            bundle.putDouble("longitude", location.getLongitude());

            msg.setData(bundle);
            eRefreshInfoHandler.sendMessage(msg);
        }
    }

    /**
     * 重新获取到位置信息
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Latitude: " + location.getLatitude() + "\tLongitude: " + location.getLongitude());
        sendLocationInfo(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}