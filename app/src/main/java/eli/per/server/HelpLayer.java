package eli.per.server;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import eli.per.testlistview.R;

public class HelpLayer implements View.OnClickListener {

    private static final String TAG = "HelpLayer";

    private Context context;
    private ImageView helpImageView;

    private boolean isAssisting;
    private int helpLayerIndex = 1;

    public HelpLayer(final Context context, Activity activity) {
        this.context = context;

        helpImageView = activity.findViewById(R.id.control_help_image);
        helpImageView.setClickable(true);
        helpImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        helpImageView.setOnClickListener(this);

        startAssist();
    }

    private void startAssist() {
        isAssisting = true;
        helpImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.help_layer_5));
    }

    @Override
    public void onClick(View view) {
        helpLayerIndex++;
        switch (helpLayerIndex) {
            case 1:
                helpImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.help_layer_5));
                break;

            case 2:
                helpImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.help_layer_6));
                break;

            case 3:
                helpImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.help_layer_7));
                break;

            default:
                stopAssist();
                break;
        }
    }

    public boolean isAssisting() {
        return isAssisting;
    }

    public void stopAssist() {
        isAssisting = false;
        helpImageView.setImageBitmap(null);
        helpImageView.setClickable(false);
    }
}
