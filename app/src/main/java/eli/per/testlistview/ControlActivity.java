package eli.per.testlistview;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import eli.per.view.ControlDialog;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ControlActivity";

    private Button controlButton;
    private ControlDialog controlDialog;

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
    }

    @Override
    public void onClick(View view) {
        controlDialog = new ControlDialog(this);
        controlDialog.setCanceledOnTouchOutside(true);
        controlDialog.show();
    }
}
