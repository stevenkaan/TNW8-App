package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.RouteHelper;
import nl.sightguide.sightguide.helpers.DatabaseHelper;

public class RouteList extends AppCompatActivity {
    private String attractionName;
    private int typeID;
    private int langID;
    private DatabaseHelper mydb ;
    private Attraction attr ;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();
        attractionName = intent.getStringExtra("attractionName");
        typeID = intent.getIntExtra("typeID", 0);

        RouteHelper adapter = new RouteHelper(this, mydb.getAttractions());

        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView val = (TextView) view.findViewById(R.id.Itemname);
                String title = val.getText().toString();

                Intent intent = new Intent(RouteList.this, RouteLauncher.class);
                intent.putExtra("name", title);
                startActivity(intent);
            }
        });
    }

}
