package nl.sightguide.sightguide;


import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AttractionList extends AppCompatActivity {


    private String attractionName;
    private int typeID;
    private int langID;
    private DatabaseHelper mydb ;
    private Attraction attr ;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_list);

        setTitle("Tourist attractions");

        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();
        attractionName = intent.getStringExtra("attractionName");
        typeID = intent.getIntExtra("typeID", 0);

        List<Map> attractions = mydb.getAttractions();
        Log.e("values","dit dus:"+attractions);

        listView = (ListView) findViewById(android.R.id.list);

        for(int k = 0; k < attractions.size(); k++){
            Map map = attractions.get(k);
            SimpleAdapter adapter = new SimpleAdapter(this, map,
                    R.layout.image_list, new String[] { "name", "info"},
                    new int[] { R.id.Itemname, R.id.ItemInfo });
            listView.setAdapter(adapter);
        }
//        https://shenhengbin.wordpress.com/2012/03/17/listview-simpleadapter/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String title = listView.getItemAtPosition(position).toString();
                Intent intent = new Intent(AttractionList.this, Attraction.class);
                intent.putExtra("name", title);
                Log.e("dit","click");
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attractionslist, menu);
        return true;
    }
}