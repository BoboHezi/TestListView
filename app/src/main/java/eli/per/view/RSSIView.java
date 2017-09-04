package eli.per.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import eli.per.data.Util;

public class RSSIView extends View {

    private static final String TAG = "RSSIView";
    private static final int positiveColor = 0xffffffff;
    private static final int negativeColor = 0x44000000;

    private Context context;
    private Paint paint;

    private float viewWidth;
    private float viewHeight;

    public RSSIView(Context context) {
        this(context, null);
    }

    public RSSIView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RSSIView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取组件窗口模式和大小
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //当宽高设置为wrap_content，重新定义其大小
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            widthSpecSize = Util.dip2px(context, 60);
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = Util.dip2px(context, 20);
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);

        //获取组件宽高度
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(negativeColor);

        float radius1 = (float) (viewHeight * 0.25);
        float radius2 = (float) (viewHeight * 0.35);
        float radius3 = viewHeight / 2;

        float lineWidth = viewHeight / 30;

        //绘制最外面的弧线
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(positiveColor);

        //radian out left
        canvas.drawArc(lineWidth / 2, -(float)(viewHeight * 0.15), radius3 * 2 - lineWidth / 2, (float) (viewHeight * 0.85 - lineWidth), 133, 90, false, paint);
        //radian out right
        canvas.drawArc(lineWidth / 2, -(float)(viewHeight * 0.15), radius3 * 2 - lineWidth / 2, (float) (viewHeight * 0.85 - lineWidth), 317, 90, false, paint);
        //radian center left
        canvas.drawArc(radius3 - radius2 - lineWidth / 2, radius3 - radius2 - lineWidth / 2 -(float)(viewHeight * 0.15), radius3 * 2 - radius3 + radius2, (float) (viewHeight * 0.85 - lineWidth) - radius3 + radius2 + lineWidth / 2, 135, 90, false, paint);
        //radian center right
        canvas.drawArc(radius3 - radius2 - lineWidth / 2, radius3 - radius2 - lineWidth / 2 -(float)(viewHeight * 0.15), radius3 * 2 - radius3 + radius2, (float) (viewHeight * 0.85 - lineWidth) - radius3 + radius2 + lineWidth / 2, 315, 90, false, paint);
        //radian inner left
        canvas.drawArc(radius3 - radius1, radius3 - radius1 -(float)(viewHeight * 0.15), radius3 * 2 - lineWidth / 2 - radius3 + radius1, (float) (viewHeight * 0.85 - lineWidth) - radius3 + radius1, 135, 90, false, paint);
        //radian inner right
        canvas.drawArc(radius3 - radius1, radius3 - radius1 -(float)(viewHeight * 0.15), radius3 * 2 - lineWidth / 2 - radius3 + radius1, (float) (viewHeight * 0.85 - lineWidth) - radius3 + radius1, 315, 90, false, paint);

        //绘制竖线
        canvas.drawLine(radius3, viewHeight / 2, radius3, viewHeight, paint);

        //绘制中心点
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radius3, (float) (viewHeight * 0.35), 5, paint);

    }
}
