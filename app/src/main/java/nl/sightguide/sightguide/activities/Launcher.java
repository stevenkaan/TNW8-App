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

    public JSONArray cityJSON;
    public JSONArray lang;

    int cityID;
    private SwipeRefreshLayout swipeView;

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

        @Override
        protected String doInBackground(String... params) {
            try {
                this.city_id = params[0];
                this.lang = params[1];

                String url = String.format("getcity/%s/%s", this.city_id, this.lang);
//                String url = "getcity/1/nld";
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

                RequestQueue rq = Volley.newRequestQueue(Launcher.this);

                JSONObject parent = new JSONObject(result).getJSONObject("city");

                String name = parent.getString("name");
                String information = parent.getString("info");
                String country = parent.getString("country");
                int city_id = parent.getInt("id");

                City city = Utils.realm.where(City.class).equalTo("id", city_id).findFirst();
                if(city == null){

                    Log.e("Creating", "NEW");
                    city = new City();
                    city.setId(city_id);
                }


                city.setName(name);
                city.setInformation(information);
                city.setCountry(country);

                String city_img1 = parent.getString("image_1");
                String city_img1Name = "";
                if(!city_img1.equals("null")) {
                    city_img1Name = city_img1.substring(city_img1.lastIndexOf('/') + 1);
                    String city_img1Url = city_img1.substring(0, city_img1.lastIndexOf('/'));

                    ImageRequest city_ir1 = new ImageDownloader(city_img1Name, city_img1Url, "city").execute();
                    rq.add(city_ir1);
                }
                city.setImage_1(city_img1Name);

                String city_img2 = parent.getString("image_2");
                String city_img2Name = "";
                if(!city_img2.equals("null")) {
                    city_img2Name = city_img2.substring(city_img2.lastIndexOf('/') + 1);
                    String city_img2Url = city_img2.substring(0, city_img2.lastIndexOf('/'));

                    ImageRequest city_ir2 = new ImageDownloader(city_img2Name, city_img2Url, "city").execute();
                    rq.add(city_ir2);
                }
                city.setImage_2(city_img2Name);

                String city_img3 = parent.getString("image_3");
                String city_img3Name = "";
                if(!city_img3.equals("null")) {
                    city_img3Name = city_img3.substring(city_img3.lastIndexOf('/') + 1);
                    String city_img3Url = city_img3.substring(0, city_img3.lastIndexOf('/'));

                    ImageRequest city_ir3 = new ImageDownloader(city_img3Name, city_img3Url, "city").execute();
                    rq.add(city_ir3);
                }
                city.setImage_3(city_img3Name);

                String city_img4 = parent.getString("image_4");
                String city_img4Name = "";
                if(!city_img4.equals("null")) {
                    city_img4Name = city_img4.substring(city_img4.lastIndexOf('/') + 1);
                    String city_img4Url = city_img4.substring(0, city_img4.lastIndexOf('/'));

                    ImageRequest city_ir4 = new ImageDownloader(city_img4Name, city_img4Url, "city").execute();
                    rq.add(city_ir4);
                }
                city.setImage_4(city_img4Name);

                Utils.realm.copyToRealmOrUpdate(city);

                JSONArray markers = parent.getJSONArray("markers");
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);


                    int id = obj.getInt("id");
                    int type = obj.getInt("type");
                    String marker_name = obj.getString("name");
                    String info = obj.getString("info");
                    double markerLat = obj.getDouble("latitude");
                    double markerLong = obj.getDouble("longitude");

                    Marker marker = Utils.realm.where(Marker.class).equalTo("id", id).findFirst();
                    if(marker == null){
                        marker = new Marker();
                        marker.setId(id);
                    }
                    marker.setCity(city);
                    marker.setType(type);
                    marker.setName(marker_name);
                    marker.setInformation(info);
                    marker.setLatitude(markerLat);
                    marker.setLongitude(markerLong);

                    String img1 = obj.getString("image_1");
                    if(!img1.equals("null")) {
                        String img1Name = img1.substring(img1.lastIndexOf('/') + 1);
                        String img1Url = img1.substring(0, img1.lastIndexOf('/'));

                        ImageRequest ir1 = new ImageDownloader(img1Name, img1Url, "marker").execute();
                        rq.add(ir1);

                        marker.setImage_1(img1Name);
                    }

                    String img2 = obj.getString("image_2");
                    if(!img2.equals("null")) {
                        String img2Name = img2.substring(img2.lastIndexOf('/') + 1);
                        String img2Url = img2.substring(0, img2.lastIndexOf('/'));

                        ImageRequest ir2 = new ImageDownloader(img2Name, img2Url, "marker").execute();
                        rq.add(ir2);

                        marker.setImage_2(img2Name);
                    }

                    String img3 = obj.getString("image_3");
                    if(!img3.equals("null")) {
                        String img3Name = img3.substring(img3.lastIndexOf('/') + 1);
                        String img3Url = img3.substring(0, img3.lastIndexOf('/'));

                        ImageRequest ir3 = new ImageDownloader(img3Name, img3Url, "marker").execute();
                        rq.add(ir3);

                        marker.setImage_3(img3Name);
                    }

                    String img4 = obj.getString("image_4");
                    if(!img4.equals("null")) {
                        String img4Name = img4.substring(img4.lastIndexOf('/') + 1);
                        String img4Url = img4.substring(0, img4.lastIndexOf('/'));

                        ImageRequest ir4 = new ImageDownloader(img4Name, img4Url, "marker").execute();
                        rq.add(ir4);

                        marker.setImage_4(img4Name);
                    }

//                    String audioFullUrl = obj.getString("audio");
//                    String audioName = audioFullUrl.substring(audioFullUrl.lastIndexOf('/') + 1);
//                    String audioUrl = audioFullUrl.substring(0, audioFullUrl.lastIndexOf('/'));
//
//                    AudioRequest ar = new AudioDownloader(audioName, audioUrl).execute();
//                    rq.add(ar);


//                    marker.setAudio(audioName);

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
                String result = Utils.run(Launcher.this, "getcities");

                try {
                    JSONArray parent = new JSONArray(result);

                    Launcher.this.cityJSON = parent;

                    ArrayList<LauncherCity> items = new ArrayList<>();

                    for(int i = 0; i < parent.length(); i++) {
                        JSONObject obj = parent.getJSONObject(i);

                        if(!obj.getString("languages").equals("none")) {

                            LauncherCity city = new LauncherCity();

                            city.setId(obj.getInt("id"));
                            city.setPosition(i);
                            city.setCountry(obj.getString("country"));
                            city.setName(obj.getString("name"));

                            city.setLanguages(new JSONArray(obj.getString("languages")));

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