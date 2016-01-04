package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import io.realm.RealmList;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;

public class RouteLauncher extends AppCompatActivity implements OnMapReadyCallback {

    private String routeName;
    private int routeId;
    private Route route;
    private City city;
    private GoogleMap mMap;
    private LatLng cityLatLng;
    private GPSHelper gps;
    private int markerIcon;
    private int currentIcon;
    private LatLng startingPoint;
    private LatLngBounds presetBounds;
    private LatLngBounds bounds;
    private CameraUpdate cu;

    private LatLng lastMarker;
    private boolean firstMarker = true;

//    private String[] split(String regex);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_launcher);

        Intent intent = getIntent();
        routeId = intent.getIntExtra("id", 0);
        routeName = intent.getStringExtra("name");

        setTitle(routeName);;

        route = Utils.realm.where(Route.class).equalTo("id", routeId).findFirst();
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

        gps = new GPSHelper(this);
        gps.getMyLocation();

        RealmList<Marker> markers = route.getMarkers();
        double closest = 99999999999.0;
        int closestMarker = 0;
        int markerId = 0;
        int markerIcon = 0;

        // Check if route has starting point
        Log.e("start",""+route.getStart());
        if(!route.getStart()){
            for(int i = 0; i < markers.size(); i++) {
                Marker thisMarker = markers.get(i);

                Location loc1 = new Location("");
                loc1.setLatitude(thisMarker.getLatitude());
                loc1.setLongitude(thisMarker.getLongitude());

                Location loc2 = new Location("");
                loc2.setLatitude(gps.getLatitude());
                loc2.setLongitude(gps.getLongitude());

                // set Id for closest marker
                float distanceInMeters = loc1.distanceTo(loc2);
                Log.e("distance",""+distanceInMeters+ " < "+ closest);
                if(distanceInMeters < closest){
                    closest = distanceInMeters;
                    closestMarker = thisMarker.getId();
                    markerIcon = i;
                }
                Log.e("closest",""+closest);
                markerId = closestMarker;
            }
        }else{
            markerId = markers.get(0).getId();
            markerIcon = 0;
        }
        Log.e("go to",""+ markerId +"marker icon: "+ markerIcon);


        Intent intent = new Intent(RouteLauncher.this, RouteInfo.class);
        intent.putExtra("id", routeId);
        intent.putExtra("marker", markerId);
        intent.putExtra("markerIcon", markerIcon);
        startActivity(intent);
    }
    public void viewRoute(View v){

        Intent intent = new Intent(RouteLauncher.this, RouteTimeline.class);
        intent.putExtra("id", routeId);
        intent.putExtra("name", routeName);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        cityLatLng = new LatLng(3.14, 3.14);
        presetBounds  = new LatLngBounds(cityLatLng,cityLatLng);
        bounds = presetBounds;

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


        ArrayList<LatLng> locList = new ArrayList<LatLng>();

        for(int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            LatLng markerLatLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            locList.add(markerLatLng);
            int SetIcon;
            if(i == markerIcon){

                startingPoint = new LatLng(marker.getLatitude(), marker.getLongitude());



            }
            switch (i+1) {
                case 1:  SetIcon = R.drawable.marker_1;
                    break;
                case 2:  SetIcon = R.drawable.marker_2;
                    break;
                case 3:  SetIcon = R.drawable.marker_3;
                    break;
                case 4:  SetIcon = R.drawable.marker_4;
                    break;
                case 5:  SetIcon = R.drawable.marker_5;
                    break;
                case 6:  SetIcon = R.drawable.marker_6;
                    break;
                case 7:  SetIcon = R.drawable.marker_7;
                    break;
                case 8:  SetIcon = R.drawable.marker_8;
                    break;
                case 9:  SetIcon = R.drawable.marker_9;
                    break;
                case 10: SetIcon = R.drawable.marker_10;
                    break;
                case 11: SetIcon = R.drawable.marker_11;
                    break;
                case 12: SetIcon = R.drawable.marker_12;
                    break;
                case 13: SetIcon = R.drawable.marker_13;
                    break;
                case 14: SetIcon = R.drawable.marker_14;
                    break;
                case 15: SetIcon = R.drawable.marker_15;
                    break;
                case 16: SetIcon = R.drawable.marker_16;
                    break;
                case 17: SetIcon = R.drawable.marker_17;
                    break;
                case 18: SetIcon = R.drawable.marker_18;
                    break;
                case 19: SetIcon = R.drawable.marker_19;
                    break;
                case 20: SetIcon = R.drawable.marker_20;
                    break;
                default: SetIcon = R.drawable.marker_0;
                    break;
            }


            // add last marker to creat loop in route begin- is also end point
            Marker firstMarker = markers.get(0);
            lastMarker = new LatLng(firstMarker.getLatitude(), firstMarker.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(SetIcon))
                    .title(marker.getName()));

            builder.include(markerLatLng);
        }
        locList.add(lastMarker);

        // get path
        ArrayList<LatLng> pathList = new ArrayList<LatLng>();
        String path = route.getPath();
        String delimiters = "\\]\\,\\[|\\[|\\]";
        String[] tokensVal = path.split(delimiters);

        for(int i = 1; i < tokensVal.length; i++) {
            String fullLatLong = tokensVal[i];
            String delimiter = "\\,";
            String[] coordinates = fullLatLong.split(delimiter);
            Double thisLat = Double.valueOf(coordinates[0]);
            Double thisLng = Double.valueOf(coordinates[coordinates.length - 1]);

            LatLng pathPoint = new LatLng(thisLat,thisLng);
            pathList.add(pathPoint);
        }
        mMap.addPolyline((new PolylineOptions()).addAll(pathList)
                .width(7)
                .color(0xFFe96745)
                .geodesic(false));

        ArrayList<LatLng> toStart = new ArrayList<LatLng>();

        // add my location
        gps = new GPSHelper(this);
        gps.getMyLocation();
        LatLng myLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());


        mMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                .title(getString(R.string.location)));


        bounds = builder.build();

        Log.e("bounds", "" + bounds);
        int padding = 50;
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < Utils.maxZoom)
                            mMap.moveCamera(cu);
                    }
                }
        );
    }
}
