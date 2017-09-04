package eli.per.testlistview;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import eli.per.thread.ReadInfoThread;
import eli.per.view.ControlDialog;
import eli.per.view.NetWorkSpeedView;
import eli.per.view.VideoLoadingView;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ControlActivity";
    public static final int HANDLER_INFO = 1;

    private Button controlButton;
    private Button controlLoadButton;
    private ControlDialog controlDialog;
    private VideoLoadingView videoLoadingView;
    private NetWorkSpeedView netWorkSpeedView;

    private RefreshInfoHandler refreshInfoHandler;

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
    }

    private void initView() {
        controlButton = (Button) findViewById(R.id.control_show);
        controlButton.setOnClickListener(this);
        controlLoadButton = (Button) findViewById(R.id.control_loading_button);
        controlLoadButton.setOnClickListener(this);

        videoLoadingView = (VideoLoadingView) findViewById(R.id.control_loading);

        netWorkSpeedView = (NetWorkSpeedView) findViewById(R.id.control_rate);
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
        }
    }

    /**
     * 更新显示信息的Handler
     */
    public class RefreshInfoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_INFO) {
                float rate = msg.getData().getFloat("RATE");
                int rssi = msg.getData().getInt("RSSI");
                //Log.i(TAG, "Speed: " + rate + "\tRSSI: " + rssi);
                netWorkSpeedView.setSpeed((int) rate);
            }
        }
    }
}