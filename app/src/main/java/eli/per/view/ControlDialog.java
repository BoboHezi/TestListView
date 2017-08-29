package eli.per.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import eli.per.data.OnSelectedItemChangeListener;
import eli.per.data.OnVelocityStateChangeListener;
import eli.per.data.Util;
import eli.per.data.Velocity;
import eli.per.testlistview.R;

public class ControlDialog extends Dialog implements OnSelectedItemChangeListener, OnVelocityStateChangeListener {

    private static final String TAG = "ControlDialog";
    private Context context;

    private ItemSelectView itemSelectView;
    private LightSwitchView lightSwitchView;
    private MoveControlView moveControlView;

    List<String> items;

    public ControlDialog(Context context) {
        super(context, R.style.control_dialog);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_control);
        getWindow().setWindowAnimations(R.style.control_dialog_anim);
        setCanceledOnTouchOutside(true);

        moveControlView = findViewById(R.id.control_view);
        moveControlView.setOnVelocityStateChangedListener(this);

        itemSelectView = findViewById(R.id.control_resolution);
        itemSelectView.setOnSelectedItemChangeListener(this);

        items = new ArrayList<>();
        items.add("0");
        items.add("480");
        items.add("720");
        items.add("1080");
        itemSelectView.setSelectItems(items);

        lightSwitchView = findViewById(R.id.control_switch);
        lightSwitchView.setSwitch(true);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        //设置全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.width = Util.dip2px(context, 300);
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.LEFT;
        window.setAttributes(lp);
    }

    @Override
    public void onItemSelectedChanged(int index) {
        Log.i(TAG, "ItemSelectedChanged:\tItem: " + items.get(index));
    }

    @Override
    public void onVelocityStateChanged(Velocity velocity) {
        Log.i(TAG, "VelocityStateChanged:\tSpeed: " + velocity.getSpeed() + "\tDirection: " + velocity.getDirection());
    }
}