package eli.per.testlistview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import eli.per.server.HelpLayer;
import eli.per.server.LocateListener;
import eli.per.thread.ReadInfoThread;
import eli.per.view.ControlDialog;
import eli.per.view.VideoLoadingView;
import eli.per.view.WifiStatusDialog;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Activity activity;

    private static final String TAG = "ControlActivity";
    public static final int HANDLER_WIFI_AND_NET_INFO = 1;
    public static final int HANDLER_GET_POSITION = 2;

    private Button controlButton;
    private Button controlLoadButton;
    private Button wifiButton;
    private ControlDialog controlDialog;
    private VideoLoadingView videoLoadingView;
    private TextView locationInfoText;

    private RefreshInfoHandler refreshInfoHandler;

    private boolean isFirstLauncher = true;
    private HelpLayer helpLayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        setContentView(R.layout.activity_control);
        initView();

        refreshInfoHandler = new RefreshInfoHandler();
        new ReadInfoThread(this, refreshInfoHandler).start();
        new LocateListener(this, refreshInfoHandler);

        context = this;
        activity = this;
        if (isFirstLauncher) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    helpLayer = new HelpLayer(context, activity);
                }
            }, 1000);
        }
    }

    private void initView() {
        controlButton = (Button) findViewById(R.id.control_show);
        controlButton.setOnClickListener(this);
        controlLoadButton = (Button) findViewById(R.id.control_loading_button);
        controlLoadButton.setOnClickListener(this);

        videoLoadingView = (VideoLoadingView) findViewById(R.id.control_loading);

        wifiButton = (Button) findViewById(R.id.control_wifi_button);
        wifiButton.setOnClickListener(this);

        locationInfoText = (TextView) findViewById(R.id.control_location_info);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.control_show) {
            controlDialog = new ControlDialog(this);
            controlDialog.show();
        } else if (view.getId() == R.id.control_loading_button) {
            if (controlLoadButton.getText().equals("开始")) {
                controlLoadButton.setText("结束");
                videoLoadingView.startLoading();
            } else if (controlLoadButton.getText().equals("结束")) {
                controlLoadButton.setText("开始");
                videoLoadingView.cancelLoading();
            }
        } else if (view.getId() == R.id.control_wifi_button) {
            new WifiStatusDialog(this).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && helpLayer != null && helpLayer.isAssisting()) {
            helpLayer.stopAssist();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 更新显示信息的Handler
     */
    public class RefreshInfoHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_WIFI_AND_NET_INFO) {
                float rate = msg.getData().getFloat("RATE");
                int rssi = msg.getData().getInt("RSSI");
                //Log.i(TAG, "Speed: " + rate + "\tRSSI: " + rssi);
            } else if (msg.what == HANDLER_GET_POSITION) {
                double latitude = msg.getData().getDouble("latitude");
                double longitude = msg.getData().getDouble("longitude");

                setLocationInfoText(latitude, longitude);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setLocationInfoText(double latitude, double longitude) {
        if (locationInfoText != null) {
            String text;
            if (latitude >= 0) {
                text = "北纬: ";
            } else {
                text = "南纬: ";
                latitude = Math.abs(latitude);
            }

            text += new DecimalFormat("0.00").format(latitude);
            text += "    ";

            if (longitude >= 0) {
                text += "东经: ";
            } else {
                text += "西经: ";
                longitude = Math.abs(longitude);
            }

            text += new DecimalFormat("0.00").format(longitude);
            //locationInfoText.setText(text);
        }
    }
}