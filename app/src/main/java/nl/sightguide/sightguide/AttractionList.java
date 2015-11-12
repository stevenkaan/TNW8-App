package nl.sightguide.sightguide;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

        String[][] attractions = mydb.getAttractions();
        AttractionAdapter adapter = new AttractionAdapter(this, attractions);


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
