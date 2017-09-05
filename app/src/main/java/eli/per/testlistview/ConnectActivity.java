package eli.per.testlistview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import eli.per.thread.TestCommand;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConnectActivity";
    private final int port = 15231;
    private final String host = "10.42.0.1";

    private Button startButton;
    private Button cleanButton;
    private EditText dataInput;
    private TextView infoText;
    private int commandData;

    private UIHandler uiHandler;
    private TestCommand testCommand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        uiHandler = new UIHandler();
        testCommand = new TestCommand(uiHandler);
        initView();
    }

    private void initView() {
        startButton = (Button) findViewById(R.id.connect_start_button);
        cleanButton = (Button) findViewById(R.id.connect_clean_button);
        dataInput = (EditText) findViewById(R.id.main_data_edittext);
        infoText = (TextView) findViewById(R.id.main_info_textview);

        startButton.setOnClickListener(this);
        cleanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_start_button) {
            //创建连接
            if (testCommand != null && !testCommand.isConnected()) {
                String value = (dataInput.getText().toString()).equals("") ? "154" : dataInput.getText().toString();
                commandData = Integer.parseInt(value);
                testCommand.connect(host, port, commandData);
            }
        } else if (view.getId() == R.id.connect_clean_button) {
            infoText.setText("信息：\n");
        }
    }

    public class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            infoText.setText(infoText.getText() + "\n" + bundle.getString("info"));
            super.handleMessage(msg);
        }
    }
}