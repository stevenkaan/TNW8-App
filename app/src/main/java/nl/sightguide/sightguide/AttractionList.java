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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class AttractionList extends AppCompatActivity {


    private String attractionName;
    private int typeID;
    private int langID;
    private DatabaseHelper mydb ;
    private Attraction attr ;
    private String[][] attractions ;
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

        attractions = mydb.getAttractions();
        ArrayList<String> attractionList = new ArrayList<String>();
        for (int i = 0; i < attractions.length; i++) {
            attractionList.add(attractions[i][1]);
        }


        listView = (ListView) findViewById(android.R.id.list);

        listView.setAdapter(new ArrayAdapter<String>(
                this, R.layout.image_list,
                R.id.Itemname,attractionList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String title = listView.getItemAtPosition(position).toString();
                Intent intent = new Intent(AttractionList.this, Attraction.class);
                intent.putExtra("name", title);

                startActivity(intent);
            }
        });
    }
}
