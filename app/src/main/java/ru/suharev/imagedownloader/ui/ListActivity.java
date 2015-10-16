package ru.suharev.imagedownloader.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.suharev.imagedownloader.R;

public class ListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            ListActivityFragment fragment
                    = (ListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            fragment.loadAndParseData();
        }
        return super.onOptionsItemSelected(item);
    }


}
