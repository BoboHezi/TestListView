package eli.per.testlistview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import eli.per.server.FloatBallService;

public class FloatingBallActivity extends AppCompatActivity {
    private static final String TAG = "FloatingBallActivity";

    private ImageView backgroundImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_ball);

        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        initView();
    }

    private void initView() {
        backgroundImage = (ImageView) findViewById(R.id.float_back);
    }

    @Override
    protected void onResume() {
        FloatingBallActivity.this.startService(new Intent(FloatingBallActivity.this, FloatBallService.class));
        super.onResume();
    }

    @Override
    protected void onStop() {
        FloatingBallActivity.this.stopService(new Intent(FloatingBallActivity.this, FloatBallService.class));
        super.onStop();
    }
}
