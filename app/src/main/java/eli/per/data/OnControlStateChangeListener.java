package eli.per.data;

/**
 * 控制数据改变的接口
 */
public interface OnControlStateChangeListener {

    void onItemSelectedChanged(int index);

    void onVelocityStateChanged(Velocity velocity);

    void onSwitchStateChanged(boolean isOpen);
}
