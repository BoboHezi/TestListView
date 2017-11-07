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

import eli.per.thread.CommandServer;
import eli.per.thread.TestCommand;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConnectActivity";
    private final int port = 15231;
    private final String host = "192.168.2.200";

    private Button connectButton;
    private Button sendButton;
    private Button disconnectButton;
    private Button cleanButton;
    private EditText dataInput;
    private TextView infoText;
    private int commandData;
    private CommandServer commandServer;

    private UIHandler uiHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        uiHandler = new UIHandler();
        commandServer = new CommandServer(uiHandler);
        initView();
    }

    private void initView() {
        connectButton = (Button) findViewById(R.id.connect_connect_button);
        sendButton = (Button) findViewById(R.id.connect_send_button);
        disconnectButton = (Button) findViewById(R.id.connect_disconnect_button);
        cleanButton = (Button) findViewById(R.id.connect_clean_button);
        dataInput = (EditText) findViewById(R.id.main_data_edittext);
        infoText = (TextView) findViewById(R.id.main_info_textview);

        connectButton.setOnClickListener(this);
        cleanButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_connect_button:
                if (commandServer != null) {
                    commandServer.connect();
                }
                break;

            case R.id.connect_send_button:
                if (commandServer != null && commandServer.isConnected()) {
                    String value = (dataInput.getText().toString()).equals("") ? "0" : dataInput.getText().toString();
                    try {
                        commandData = Integer.parseInt(value);
                    } catch (Exception e) {
                    }
                    commandServer.sendData(commandData);
                }
                break;

            case R.id.connect_disconnect_button:
                if (commandServer != null) {
                    commandServer.disconnect();
                }
                break;

            case R.id.connect_clean_button:
                infoText.setText("信息：\n");
                break;
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