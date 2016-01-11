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
import nl.sightguide.sightguide.services.ProximitySensor;

public class RouteList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        if(getSharedPreferences("SightGuide", 0).getBoolean("proximitySensorActive", true)){
            stopService(new Intent(this, ProximitySensor.class));
        }

        RealmResults<Route> results = Utils.realm.where(Route.class).equalTo("city.id", Utils.city_id).findAll();

        RouteAdapter adapter = new RouteAdapter(this, results, true);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView val = (TextView) view.findViewById(R.id.Itemname);
                String title = val.getText().toString();

                TextView routeId = (TextView) view.findViewById(R.id.RouteId);
                int route = Integer.parseInt(routeId.getText().toString());

                Intent intent = new Intent(RouteList.this, RouteLauncher.class);
                intent.putExtra("name", title);
                intent.putExtra("id", route);
                startActivity(intent);


            }
        });
    }

}
