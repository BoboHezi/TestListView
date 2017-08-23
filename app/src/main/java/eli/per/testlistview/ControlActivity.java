package eli.per.testlistview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import eli.per.view.MoveControlView;
import eli.per.view.OnVelocityStateChangeListener;
import eli.per.view.Velocity;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener, OnVelocityStateChangeListener {

    private static final String TAG = "ControlActivity";
    private MoveControlView controlView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initView();
    }

    private void initView() {
        controlView = (MoveControlView) findViewById(R.id.control_view);
        controlView.setOnClickListener(this);
        controlView.setOnVelocityStateChangedListener(this);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onVelocityStateChanged(Velocity velocity) {
        Log.i(TAG, "\nSpeed: " + velocity.getSpeed() + "\tDirection: " + velocity.getDirection());
    }
}
