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

public class RouteLauncher extends AppCompatActivity {

    private String routeName;
    private TextView routeInfo;
    private DatabaseHelper mydb ;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_launcher);


        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        routeName = intent.getStringExtra("name");
        setTitle(routeName);;



    }
    public void startTour(View v){

        Intent intent = new Intent(RouteLauncher.this, Route.class);
        intent.putExtra("name", routeName);
        startActivity(intent);
    }
    public void viewRoute(View v){

        Intent intent = new Intent(RouteLauncher.this, RouteTimeline.class);
        intent.putExtra("name", routeName);
        startActivity(intent);
    }
}
