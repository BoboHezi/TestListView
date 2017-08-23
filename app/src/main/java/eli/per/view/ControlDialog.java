package eli.per.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import eli.per.data.Util;
import eli.per.testlistview.R;

public class ControlDialog extends Dialog implements View.OnClickListener{

    private Context context;

    public ControlDialog(Context context) {
        super(context, R.style.control_dialog);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_control);
        getWindow().setWindowAnimations(R.style.control_dialog_anim);

        findViewById(R.id.control_view).setOnClickListener(this);
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
    public void onClick(View view) {

    }
}
