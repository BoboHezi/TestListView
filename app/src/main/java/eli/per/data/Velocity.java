package eli.per.data;

public class Velocity {

    public enum Direction {
        FRONT,
        LEFT,
        RIGHT,
        BACK
    }

    private int speed;
    private Direction direction;

    public Velocity(int speed, Direction direction) {
        this.speed = speed;
        this.direction = direction;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getSpeed() {
        return this.speed;
    }

    public Direction getDirection() {
        return this.direction;
    }
}