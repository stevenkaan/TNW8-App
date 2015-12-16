package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

        Utils.preferences = getSharedPreferences("SightGuide", 0);
        Utils.editor = Utils.preferences.edit();

        CityListAdapter adapter = new CityListAdapter(this, Utils.realm.where(City.class).findAll(), true);

        ListView listView = (ListView) findViewById(R.id.city_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView cityId = (TextView) view.findViewById(R.id.CityId);
                int newId  = Integer.parseInt(cityId.getText().toString());

                Log.e("set city: ", " set id: " + newId);

                Utils.editor.putInt("lastCity", newId);
                Utils.editor.commit();

                Utils.city_id = newId;

                Intent intent = new Intent(CityList.this, Home.class);
                startActivity(intent);

            }
        });

    }
    public void addNew (View v){
        Intent intent = new Intent(CityList.this, Launcher.class);
        startActivity(intent);
    }

}
