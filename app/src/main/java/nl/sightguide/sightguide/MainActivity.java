package nl.sightguide.sightguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.stevenkaan.sightguide.MESSAGE";
    private String[] monthsArray = { "Amsterdam", "Groningen", "Leeuwarden", "Steenwijk", "Hoogeveen", "Emmen", "Zwolle", "Utrecht", "Maastricht", "Alkmaar", "Arnhem" };

    private ListView monthsListView;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthsListView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, monthsArray);
        monthsListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "Pressed search");
                return true;
            case R.id.action_settings:
                Log.d(TAG, "Pressed settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}