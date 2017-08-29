package eli.per.testlistview;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private MyAdapter adapter;
    private List<Integer> selectedItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        adapter = new MyAdapter(this, getData());
        listView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", R.mipmap.ic_launcher_round);
            map.put("title", "bobo" + i);
            map.put("info", "详细信息");
            list.add(map);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (selectedItem == null) {
            selectedItem = new ArrayList<>();
        }
        selectedItem.add(i);
        adapter.setSelectedItem(selectedItem);
        adapter.notifyDataSetChanged();
    }
}