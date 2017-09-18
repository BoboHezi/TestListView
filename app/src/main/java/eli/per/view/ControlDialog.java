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

import eli.per.data.OnControlStateChangeListener;
import eli.per.data.Util;
import eli.per.data.Velocity;
import eli.per.testlistview.R;

public class ControlDialog extends Dialog implements OnControlStateChangeListener {

    private static final String TAG = "ControlDialog";
    private Context context;

    //分辨率选择视图
    private ItemSelectView itemSelectView;
    //LED开关视图
    private LightSwitchView lightSwitchView;
    //移动控制视图
    private MoveControlView moveControlView;
    //分辨率的可选条目
    List<String> items;

    public ControlDialog(Context context) {
        super(context, R.style.control_dialog);
        this.context = context;
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.dialog_control);
        getWindow().setWindowAnimations(R.style.control_dialog_anim);
        setCanceledOnTouchOutside(true);

        //初始化移动控制视图
        moveControlView = findViewById(R.id.control_view);
        moveControlView.setOnControlStateChangedListener(this);
        //初始化分辨率选择视图
        itemSelectView = findViewById(R.id.control_resolution);
        itemSelectView.setOnControlStateChangeListener(this);
        //设置可选的分辨率
        items = new ArrayList<>();
        items.add("0");
        items.add("480");
        items.add("720");
        items.add("1080");
        itemSelectView.setSelectItems(items);
        //LED开关控制视图
        lightSwitchView = findViewById(R.id.control_switch);
        lightSwitchView.setOnControlStateChangedListener(this);
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

    /**
     * 条目选中时的接口
     *
     * @param index 选中条目的索引
     */
    @Override
    public void onItemSelectedChanged(int index) {
        Log.i(TAG, "ItemSelectedChanged:\tItem: " + items.get(index));
    }

    /**
     * 速度改变时的接口
     *
     * @param velocity 速度
     */
    @Override
    public void onVelocityStateChanged(Velocity velocity) {
        Log.i(TAG, "VelocityStateChanged:\tSpeed: " + velocity.getSpeed() + "\tDirection: " + velocity.getDirection());
    }

    /**
     * 开关状态改变的接口
     *
     * @param isOpen 开关状态
     */
    @Override
    public void onSwitchStateChanged(boolean isOpen) {
        Log.i(TAG, "onSwitchStateChanged: \tLight State: " + (isOpen ? "Open" : "Close"));
    }
}