package eli.per.testlistview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import eli.per.view.FloatingControlBall;

public class FloatingBallActivity extends AppCompatActivity {
    private static final String TAG = "FloatingBallActivity";

    private ImageView backgroundImage;
    private FloatingControlBall floatBall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_ball);

        initView();
    }

    private void initView() {
        backgroundImage = (ImageView) findViewById(R.id.float_back);
        floatBall = (FloatingControlBall) findViewById(R.id.float_ball);

        rotateBack();
    }

    private void rotateBack() {
        if (backgroundImage != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            backgroundImage.setImageBitmap(bitmap);
        }
    }
}
