package eli.per.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eli.per.data.OnControlStateChangeListener;
import eli.per.testlistview.R;

public class ItemSeekBar extends View {
    private static final String TAG = "ItemSeekBar";

    //线条颜色
    private int lineColor;
    //圆点颜色
    private int pointColor;
    //字体颜色
    private int textColor;
    //线条高度
    private float lineHeight;
    //圆点半径
    private float pointRadius;
    //选择条目
    private List<String> selectItems;
    //组件宽度
    private float windowWidth;
    //组件高度
    private float windowHeight;
    //内部向下偏移值
    private float offset;
    //圆点位置X值
    private float pointRadiusX;
    //圆点位置Y值
    private float pointRadiusY;
    //被选择的index
    private int selectedIndex;
    //选择条目改变接口
    private OnControlStateChangeListener changeListener;

    private Context context;
    private Paint paint;

    public ItemSeekBar(Context context) {
        this(context, null);
    }

    public ItemSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ItemSeekBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;

        paint = new Paint();
        paint.setAntiAlias(true);

        selectItems = new ArrayList<>();
        selectItems.add("480");
        selectItems.add("720");
        selectItems.add("1080");

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.styleable_item_seek);
        lineColor = typedArray.getColor(R.styleable.styleable_item_seek_item_seek_lineColor, 0xff777777);
        pointColor = typedArray.getColor(R.styleable.styleable_item_seek_item_seek_circleColor, 0xff000000);
        textColor = typedArray.getColor(R.styleable.styleable_item_seek_item_seek_textColor, 0xff000000);
        lineHeight = typedArray.getInt(R.styleable.styleable_item_seek_item_seek_lineHeight, 5);
        pointRadius = typedArray.getInt(R.styleable.styleable_item_seek_item_seek_circleRadius, 10);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取组件宽高
        windowWidth = getMeasuredWidth();
        windowHeight = getMeasuredHeight();
        //设置内部向左偏移值
        offset = windowWidth - pointRadius * 3 / 2;
        //设置圆点坐标的Y值，固定
        pointRadiusX = offset;
        //重置坐标
        float pieceHeight = windowHeight / selectItems.size();
        pointRadiusY = selectedIndex * pieceHeight + pieceHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(lineColor);
        paint.setStrokeWidth(lineHeight);
        canvas.drawLine(offset, 0, offset, windowHeight, paint);

        float itemHeight = windowHeight / selectItems.size();

        //绘制条目
        paint.setTextSize(26);
        for (int i = 0; i < selectItems.size(); i++) {
            paint.setColor(textColor);
            String itemText = selectItems.get(i);
            float itemTextWidth = paint.measureText(itemText);
            float itemTextHeight = -(paint.descent() + paint.ascent());
            float start = i * itemHeight + itemHeight / 2;
            //绘制条目文本
            canvas.drawText(itemText, (pointRadiusX - pointRadius - itemTextWidth) / 2, start + itemTextHeight / 2, paint);
            //绘制对应小点
            paint.setColor(pointColor);
            canvas.drawCircle(offset, start, (float) (lineHeight * 1.5), paint);
        }

        //绘制圆点
        paint.setColor(pointColor);
        if (pointRadiusY == 0) {
            pointRadiusY = itemHeight / 2;
        }
        canvas.drawCircle(pointRadiusX, pointRadiusY, pointRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //获得触点的X值
        pointRadiusY = event.getY();

        //控制圆点坐标不会溢出
        if (pointRadiusY < pointRadius)
            pointRadiusY = pointRadius;
        if (pointRadiusY > windowHeight - pointRadius)
            pointRadiusY = windowHeight - pointRadius;

        //当手指抬起时，计算并定位到最近的条目
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float pieceHeight = windowHeight / selectItems.size();
            int index = calculateIndex();
            pointRadiusY = index * pieceHeight + pieceHeight / 2;
            if (changeListener != null) {
                int nowIndex = calculateIndex();
                if (nowIndex != selectedIndex) {
                    selectedIndex = nowIndex;
                    changeListener.onItemSelectedChanged(selectedIndex);
                }
            }
        }

        postInvalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 设置选择条目变化监听
     *
     * @param changeListener
     */
    public void setOnControlStateChangeListener(OnControlStateChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * 设置可以选择的项目
     *
     * @param items
     */
    public void setSelectItems(List<String> items) {
        this.selectItems = items;
    }

    /**
     * 计算索引
     *
     * @return
     */
    private int calculateIndex() {
        float pieceHeight = windowHeight / selectItems.size();
        int index = (int) (pointRadiusY / pieceHeight);
        return index;
    }

    /**
     * 获取当前索引
     *
     * @return
     */
    public int getIndex() {
        return calculateIndex();
    }

    /**
     * 设置当前索引
     *
     * @param index
     */
    public void setIndex(int index) {
        selectedIndex = index;
        float pieceWidth = windowWidth / selectItems.size();
        pointRadiusX = selectedIndex * pieceWidth + pieceWidth / 2;
        postInvalidate();
    }
}
