package eli.per.testlistview;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import eli.per.view.ControlDialog;
import eli.per.view.VideoLoadingView;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ControlActivity";

    private Button controlButton;
    private Button controlLoadButton;
    private ControlDialog controlDialog;
    private VideoLoadingView videoLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        setContentView(R.layout.activity_control);
        initView();
    }

    private void initView() {
        controlButton = (Button) findViewById(R.id.control_show);
        controlButton.setOnClickListener(this);
        controlLoadButton = (Button) findViewById(R.id.control_loading_button);
        controlLoadButton.setOnClickListener(this);

        videoLoadingView = (VideoLoadingView) findViewById(R.id.control_loading);
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
}