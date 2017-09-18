package eli.per.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import eli.per.data.Util;
import eli.per.testlistview.R;

public class WifiStatusDialog extends Dialog implements View.OnClickListener{

    private static final String TAG = "WifiStatusDialog";

    private Context context;

    private TextView eCancelButton;
    private TextView eConnectButton;

    public WifiStatusDialog(Context context) {
        super(context, R.style.wifi_dialog);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_wifi);
        getWindow().setWindowAnimations(R.style.wifi_dialog_anim);
        setCanceledOnTouchOutside(true);

        eCancelButton = findViewById(R.id.dialog_wifi_cancel);
        eCancelButton.setOnClickListener(this);
        eConnectButton = findViewById(R.id.dialog_wifi_connect);
        eConnectButton.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = Util.dip2px(context, 240);
        layoutParams.height = Util.dip2px(context, 120);
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_wifi_cancel:
                break;

            case R.id.dialog_wifi_connect:
                if (context != null) {
                    try {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                break;
        }
        dismiss();
    }
}
