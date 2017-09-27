package eli.per.data;

/**
 * 坐标类
 *
 * @author eli chang
 */
public class Coordinate {

    private float rawX;
    private float rawY;

    public Coordinate(float rawX, float rawY) {
        this.rawX = rawX;
        this.rawY = rawY;
    }

    public void setRawX(float rawX) {
        this.rawX = rawX;
    }

    public void setRawY(float rawY) {
        this.rawY = rawY;
    }

    public float getRawX() {
        return this.rawX;
    }

    public float getRawY() {
        return this.rawY;
    }
}
