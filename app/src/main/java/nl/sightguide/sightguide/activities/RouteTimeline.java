package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.AttractionAdapter;
import nl.sightguide.sightguide.adapters.RouteAdapter;
import nl.sightguide.sightguide.adapters.TimelineAdapter;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;

public class RouteTimeline extends AppCompatActivity {

    private String routeName;
    private int routeId;
    private Route route;
    private TextView routeInfo;
    private ListView listView;
    private Marker thisMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_timeline);


        Intent intent = getIntent();
        routeId = intent.getIntExtra("id", 0);
        routeName = intent.getStringExtra("name");

        TextView title = (TextView) findViewById(R.id.RouteName);
        title.setText(routeName);



        TimelineAdapter adapter = new TimelineAdapter(this, Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll(), false, routeId);

        ListView listView = (ListView) findViewById(android.R.id.list);
        View v = getLayoutInflater().inflate(R.layout.bottom_timeline, null);

        listView.addFooterView(v);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 TextView val = (TextView) view.findViewById(R.id.Itemname);

                if (!val.getText().toString().equals("")) {
                    String title = val.getText().toString();

                    Intent intent = new Intent(RouteTimeline.this, Attraction.class);
                    intent.putExtra("name", title);
                    startActivity(intent);
                }

            }
        });

    }
}
