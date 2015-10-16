package ru.suharev.imagedownloader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.suharev.imagedownloader.R;

/**
 * Activity, которая содержит в себе ViewPagerFragment
 **/
public class ImageActivity extends AppCompatActivity {


    public static final String EXTRA_POSITION = "extra_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        int position = intent.getIntExtra(EXTRA_POSITION, 0);
        ((ViewPagerFragment) getSupportFragmentManager().findFragmentById(R.id.pager_fragment))
                .setPosition(position);
    }


}
