package eli.per.testlistview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Integer> selectedItem;

    public MyAdapter(Context context, List<Map<String, Object>> data) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.selectedItem = new ArrayList<>();
    }

    public final class ZuJian {
        public ImageView image;
        public TextView title;
        public TextView info;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        ZuJian zuJian;
        final int ID = position;
        if (convertview == null) {
            zuJian = new ZuJian();
            convertview = layoutInflater.inflate(R.layout.list_item, null);
            zuJian.image = convertview.findViewById(R.id.image);
            zuJian.title = convertview.findViewById(R.id.title);
            zuJian.info = convertview.findViewById(R.id.info);
            convertview.setTag(zuJian);
        } else {
            zuJian = (ZuJian) convertview.getTag();
        }
        zuJian.image.setBackgroundResource((Integer) data.get(position).get("image"));
        zuJian.title.setText((String) data.get(position).get("title"));
        zuJian.info.setText((String) data.get(position).get("info"));

        if (selectedItem.contains(position)) {
            zuJian.title.setTextColor(Color.RED);
        } else {
            zuJian.title.setTextColor(Color.GRAY);
        }

        return convertview;
    }

    public void setSelectedItem(List<Integer> items) {
        this.selectedItem = items;
    }
}
