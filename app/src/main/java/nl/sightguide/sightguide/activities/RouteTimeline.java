package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.helpers.TimelineHelper;
import nl.sightguide.sightguide.helpers.SwipeDetector;

public class RouteTimeline extends AppCompatActivity {

    private String routeName;
    private TextView routeInfo;
    private DatabaseHelper mydb ;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_timeline);


        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        routeName = intent.getStringExtra("name");
        setTitle("Route");

        TimelineHelper adapter = new TimelineHelper(this, mydb.getAttractions());

        View v = getLayoutInflater().inflate(R.layout.bottom_timeline, null);
        listView = (ListView) findViewById(android.R.id.list);

        listView.addFooterView(v);
        listView.setAdapter(adapter);



    }
}
