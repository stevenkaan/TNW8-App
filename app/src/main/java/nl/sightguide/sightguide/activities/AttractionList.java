package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import nl.sightguide.sightguide.helpers.AttractionHelper;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;

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

        AttractionHelper adapter = new AttractionHelper(this, mydb.getAttractions());

        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);
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
