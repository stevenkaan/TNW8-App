package nl.sightguide.sightguide;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectMarkers extends AppCompatActivity {

    private String cityName;
    private int cityID;
    private int langID;
    private DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_markers);
        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        cityName = intent.getStringExtra("cityName");
        cityID = intent.getIntExtra("cityID", 0);
        langID = intent.getIntExtra("langID", 0);

        setTitle("List markers: "+cityName);

//        new SetMarkerList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_markers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_city) {
            startActivity(new Intent(SelectMarkers.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//    private class SetMarkerList () {
//        SimpleAdapter adapter = new SimpleAdapter(SelectMarkers.this, data,
//                android.R.layout.simple_list_item_1,
//                new String[] {"name", "desc"},
//                new int[] {android.R.id.text1,
//                        android.R.id.text2});
//
//        ListView languageSelect = (ListView) findViewById(R.id.lvMarkers);
//        languageSelect.setAdapter(adapter);
//    }

}
