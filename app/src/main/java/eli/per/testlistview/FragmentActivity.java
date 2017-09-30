package eli.per.testlistview;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import eli.per.fragment.AllFilesFragment;
import eli.per.fragment.ImageFilesFragment;
import eli.per.fragment.VideoFilesFragment;

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private Button allButton;
    private Button imgButton;
    private Button vidButton;

    private AllFilesFragment allFilesFragment;
    private ImageFilesFragment imageFilesFragment;
    private VideoFilesFragment videoFilesFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private int fragmentIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initView();
        initFragments();
    }

    private void initView() {
        allButton = (Button) findViewById(R.id.fragment_button_all);
        imgButton = (Button) findViewById(R.id.fragment_button_img);
        vidButton = (Button) findViewById(R.id.fragment_button_vid);
        allButton.setOnClickListener(this);
        imgButton.setOnClickListener(this);
        vidButton.setOnClickListener(this);
        allButton.setTextColor(0xffff0000);
    }

    private void initFragments() {
        allFilesFragment = new AllFilesFragment();
        imageFilesFragment = new ImageFilesFragment();
        videoFilesFragment = new VideoFilesFragment();

        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_left_in, R.anim.anim_fragment_left_out);
        transaction.replace(R.id.fragment_content, allFilesFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        transaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.fragment_button_all:
                transaction.setCustomAnimations(R.anim.anim_fragment_right_in, R.anim.anim_fragment_right_out);
                transaction.replace(R.id.fragment_content, allFilesFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentIndex = 1;
                break;

            case R.id.fragment_button_img:
                if (fragmentIndex > 2) {
                    transaction.setCustomAnimations(R.anim.anim_fragment_right_in, R.anim.anim_fragment_right_out);
                } else {
                    transaction.setCustomAnimations(R.anim.anim_fragment_left_in, R.anim.anim_fragment_left_out);
                }
                transaction.replace(R.id.fragment_content, imageFilesFragment);
                fragmentIndex = 2;
                break;

            case R.id.fragment_button_vid:
                transaction.setCustomAnimations(R.anim.anim_fragment_left_in, R.anim.anim_fragment_left_out);
                transaction.replace(R.id.fragment_content, videoFilesFragment);
                fragmentIndex = 3;
                break;

            default:
                break;
        }
        transaction.commit();
        if (fragmentIndex == 1) {
            allButton.setTextColor(0xffff0000);
            imgButton.setTextColor(0xff777777);
            vidButton.setTextColor(0xff777777);
        } else if (fragmentIndex == 2) {
            imgButton.setTextColor(0xffff0000);
            allButton.setTextColor(0xff777777);
            vidButton.setTextColor(0xff777777);
        } else if (fragmentIndex == 3) {
            vidButton.setTextColor(0xffff0000);
            imgButton.setTextColor(0xff777777);
            allButton.setTextColor(0xff777777);
        }
    }
}
