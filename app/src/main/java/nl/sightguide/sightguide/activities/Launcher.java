package nl.sightguide.sightguide.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import nl.sightguide.sightguide.adapters.LauncherAdapter;
import nl.sightguide.sightguide.helpers.AudioDownloader;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DownloadHelper;
import nl.sightguide.sightguide.helpers.ImageDownloader;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.LauncherCity;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;
import nl.sightguide.sightguide.requests.AudioRequest;

public class Launcher extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    public JSONArray cityJSON;
    public JSONArray lang;
    String cityName;
    int cityID;
    private SwipeRefreshLayout swipeView;

    private DatabaseHelper mydb ;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        setTitle("Select a city");

        settings = getSharedPreferences("SightGuide", 0);
        editor = settings.edit();


        mydb = new DatabaseHelper(this);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new DownloadCityTask().execute();
                    }
                }, 3000);
            }
        });

        new DownloadCityTask().execute();


        final ListView listView = (ListView) findViewById(R.id.view_cities);

        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LauncherCity city = (LauncherCity) view.getTag();
                Launcher.this.lang = city.getLanguages();
                Launcher.this.cityID = city.getId();

                openContextMenu(view);
            }
        });
    }

    private class DownloadMarkers extends AsyncTask<String, Void, String> {
        private String city_id;
        private String lang;

        private RequestQueue rq;

        @Override
        protected String doInBackground(String... params) {
            try {
                this.city_id = "20";

                if(params[0].equals("10")) {
                    this.city_id = "10";
                }
                this.lang = params[1];

                String url = String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%s&lang=%s", this.city_id, this.lang);
                Log.e("URL", url);
                return Utils.run(Launcher.this, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Utils.realm.beginTransaction();

                JSONObject parent = new JSONObject(result).getJSONObject("city");
                String name = parent.getString("name");
                String information = parent.getString("information");
                String country = parent.getString("country");
                double latitude = parent.getDouble("latitude");
                double longitude = parent.getDouble("longitude");
                int population = parent.getInt("population");
                int city_id = parent.getInt("id");

                City city = Utils.realm.where(City.class).equalTo("id", city_id).findFirst();
                if(city == null){
                    city = new City();
                    city.setId(city_id);
                }

                city.setName(name);
                city.setInformation(information);
                city.setCountry(country);
                city.setLatitude(latitude);
                city.setLongitude(longitude);
                city.setPopulation(population);

                Utils.realm.copyToRealmOrUpdate(city);


                JSONArray markers = parent.getJSONArray("markers");
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);


                    String imgFullUrl = obj.getString("image");
                    String imgName = imgFullUrl.substring(imgFullUrl.lastIndexOf('/') + 1);
                    String imgUrl = imgFullUrl.substring(0, imgFullUrl.lastIndexOf('/'));

                    String audioFullUrl = obj.getString("audio");
                    String audioName = audioFullUrl.substring(audioFullUrl.lastIndexOf('/') + 1);
                    String audioUrl = audioFullUrl.substring(0, audioFullUrl.lastIndexOf('/'));

                    RequestQueue rq = Volley.newRequestQueue(Launcher.this);

                    ImageRequest ir = new ImageDownloader(imgName, imgUrl).execute();
                    AudioRequest ar = new AudioDownloader(audioName, audioUrl).execute();

                    rq.add(ir);
                    rq.add(ar);

                    int id = obj.getInt("id");
                    int type_id = obj.getInt("type_id");
                    String marker_name = obj.getString("name");
                    String info = obj.getString("information");
                    double markerLat = obj.getDouble("latitude");
                    double markerLong = obj.getDouble("longitude");

                    Marker marker = Utils.realm.where(Marker.class).equalTo("id", id).findFirst();
                    if(marker == null){
                        marker = new Marker();
                        marker.setId(id);
                    }
                    marker.setCity(city);
                    marker.setType_id(type_id);
                    marker.setName(marker_name);
                    marker.setInformation(info);
                    marker.setLatitude(markerLat);
                    marker.setLongitude(markerLong);
                    marker.setImage(imgName);
                    marker.setAudio(audioName);

                    Utils.realm.copyToRealmOrUpdate(marker);
                }

                JSONArray routes = parent.getJSONArray("routes");
                for(int i = 0; i < routes.length(); i++) {
                    JSONObject obj = routes.getJSONObject(i);

                    int id = obj.getInt("id");
                    String route_name = obj.getString("name");
                    String route_information = obj.getString("information");
                    double distance = obj.getDouble("distance");
                    JSONArray route_markers = obj.getJSONArray("markers");

                    Route route = Utils.realm.where(Route.class).equalTo("id", id).findFirst();
                    if(route == null){
                        route = new Route();
                        route.setId(city_id);
                    }

                    Log.e("Info", route_name);
                    Log.e("Info", route_information);

                    route.setName(route_name);
                    route.setInfomation(route_information);
                    route.setDistance(distance);
                    route.setCity(city);


                    RealmList<Marker> list = new RealmList<>();

                    for(int x = 0; x < route_markers.length(); x++) {
                        int marker_id = route_markers.getInt(x);
                        Marker marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();
                        list.add(marker);
                    }

                    route.setMarkers(list);
                    Utils.realm.copyToRealmOrUpdate(route);

                }
                Utils.city_id = Integer.parseInt(this.city_id);


                editor.putInt("lastCity", Integer.parseInt(this.city_id));
                editor.commit();

                Utils.realm.commitTransaction();
                Intent intent = new Intent(Launcher.this, Home.class);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    private class DownloadCityTask extends AsyncTask<String, Void, String> {

        private LauncherAdapter adapter;

        @Override
        protected String doInBackground(String... params) {
            try {
                String result = Utils.run(Launcher.this, "getcities/2");

                try {
                    JSONArray parent = new JSONArray(result);

                    Launcher.this.cityJSON = parent;

                    ArrayList<LauncherCity> items = new ArrayList<>();

                    for(int i = 0; i < parent.length(); i++) {
                        JSONObject obj = parent.getJSONObject(i);

                        if(!obj.getString("get_languages").equals("none")) {

                            LauncherCity city = new LauncherCity();

                            city.setId(obj.getInt("id"));
                            city.setPosition(i);
                            city.setCountry(obj.getString("country_id"));
                            city.setName(obj.getString("city_name"));


                            //city.setLanguages(obj.getJSONArray("get_languages"));
                            city.setLanguages(new JSONArray(obj.getString("get_languages")));

                            items.add(city);
                        }
                    }
                    adapter = new LauncherAdapter(Launcher.this, 1, items);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeView.setRefreshing(false);
            ListView monthsListView = (ListView) findViewById(R.id.view_cities);
            monthsListView.setAdapter(adapter);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.view_cities) {

            try {
                for (int i = 0; i < this.lang.length(); i++) {
                    String language = this.lang.getString(i);
                    menu.add(language);
                }
            } catch (JSONException e) {
                Log.e("JSONExceoption", e.toString());
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String lang = item.getTitle().toString();
        new DownloadMarkers().execute(String.format("%d", cityID), lang);
        return true;
    }
}