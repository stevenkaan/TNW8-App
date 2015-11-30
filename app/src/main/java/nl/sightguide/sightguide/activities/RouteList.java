package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.RouteAdapter;
import nl.sightguide.sightguide.models.Route;

public class RouteList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        setTitle("Route overzicht");

        RealmResults<Route> results = Utils.realm.where(Route.class).equalTo("city.id", Utils.city_id).findAll();

        RouteAdapter adapter = new RouteAdapter(this, results, true);

        ListView listView = (ListView) findViewById(android.R.id.list);
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
