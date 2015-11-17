package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.AttractionAdapter;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.Marker;

public class AttractionList extends AppCompatActivity {

    private String attractionName;
    private int city_id;

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
        city_id = intent.getIntExtra("city_id", 0);

        AttractionAdapter adapter = new AttractionAdapter(this, Utils.realm.where(Marker.class).equalTo("city.id", city_id).findAll(), true);

        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView val = (TextView)view.findViewById(R.id.Itemname);
                String title= val.getText().toString();

                Intent intent = new Intent(AttractionList.this, Attraction.class);
                intent.putExtra("name", title);
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
