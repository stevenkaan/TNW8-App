package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.RealmList;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;

public class RouteLauncher extends AppCompatActivity implements OnMapReadyCallback {

    private String routeName;
    private Route route;
    private City city;
    private GoogleMap mMap;
    private LatLng cityLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_launcher);

        Intent intent = getIntent();

        routeName = intent.getStringExtra("name");
        setTitle(routeName);;

        route = Utils.realm.where(Route.class).equalTo("name", routeName).findFirst();
        city = route.getCity();

        TextView routeName = (TextView) findViewById(R.id.routeName);
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView information = (TextView) findViewById(R.id.infoText);

        routeName.setText(route.getName());
        distance.setText(Double.toString(route.getDistance()));
        information.setText(route.getInfomation());

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }
    }
    public void startTour(View v){

        Intent intent = new Intent(RouteLauncher.this, RouteInfo.class);
        intent.putExtra("name", routeName);
        startActivity(intent);
    }
    public void viewRoute(View v){

        Intent intent = new Intent(RouteLauncher.this, RouteTimeline.class);
        intent.putExtra("name", routeName);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        cityLatLng = new LatLng(3.14, 3.14);

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < Utils.maxZoom)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, Utils.maxZoom));
                    }
                }
        );

        RealmList<Marker> markers = route.getMarkers();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            LatLng markerLatLng = new LatLng(marker.getLatitude(), marker.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(marker.getName()));

            builder.include(markerLatLng);
        }

        LatLngBounds bounds = builder.build();

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, Utils.startingZoom));
    }
}
