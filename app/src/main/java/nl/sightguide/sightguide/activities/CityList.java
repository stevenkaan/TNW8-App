package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.CityListAdapter;
import nl.sightguide.sightguide.models.City;

public class CityList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        CityListAdapter adapter = new CityListAdapter(this, Utils.realm.where(City.class).findAll(), true);

        ListView listView = (ListView) findViewById(R.id.city_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView val = (TextView) view.findViewById(R.id.Itemname);
                String title = val.getText().toString();

                Intent intent = new Intent(CityList.this, Attraction.class);
                intent.putExtra("name", title);
                startActivity(intent);
            }
        });
    }

}
