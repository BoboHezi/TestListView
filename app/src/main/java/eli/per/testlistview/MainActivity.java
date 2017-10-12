package eli.per.testlistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button connectButton;
    private Button controlButton;
    private Button floatBallButton;
    private Button fragmentButton;
    private Button listViewButton;
    private Button viewPagerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        connectButton = (Button) findViewById(R.id.main_connect);
        connectButton.setOnClickListener(this);
        controlButton = (Button) findViewById(R.id.main_control);
        controlButton.setOnClickListener(this);
        floatBallButton = (Button) findViewById(R.id.main_float_ball);
        floatBallButton.setOnClickListener(this);
        fragmentButton = (Button) findViewById(R.id.main_fragments);
        fragmentButton.setOnClickListener(this);
        listViewButton = (Button) findViewById(R.id.main_list_view);
        listViewButton.setOnClickListener(this);
        viewPagerButton = (Button) findViewById(R.id.main_view_pager);
        viewPagerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_connect:
                startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                break;

            case R.id.main_control:
                startActivity(new Intent(MainActivity.this, ControlActivity.class));
                break;

            case R.id.main_float_ball:
                startActivity(new Intent(MainActivity.this, FloatingBallActivity.class));
                break;

            case R.id.main_fragments:
                startActivity(new Intent(MainActivity.this, FragmentActivity.class));
                break;

            case R.id.main_list_view:
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
                break;

            case R.id.main_view_pager:
                startActivity(new Intent(MainActivity.this, ViewPagerActivity.class));
                break;
        }
    }
}