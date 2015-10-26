package nl.sightguide.sightguide;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    private String cityName;
    private int cityID;
    private int langID;
    private DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        cityName = intent.getStringExtra("cityName");
        cityID = intent.getIntExtra("cityID", 0);
        langID = intent.getIntExtra("langID", 0);

        /// to do: insert if statement to see if user already has downloaded a city
        String url = String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%d&lang_id=%d", 10, langID);
        new DownloadAllData().execute(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    /// Download all data related to city
    private class DownloadAllData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return MainActivity.downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            try {
                JSONObject parent = new JSONObject(result).getJSONObject("city");
                String country = parent.getString("country");
                double latitude = parent.getDouble("latitude");
                double longitude = parent.getDouble("longitude");
                String name = parent.getString("name");
                int population = parent.getInt("population");
//                int city_id = parent.getInt("city_id");

                if(mydb.insertCity(country, latitude, longitude, population)){
                    Log.d("db", "success");
                }else{
                    Log.d("db", "failed");
                }


                JSONArray markers = parent.getJSONArray("markers");

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);

//                    int type_id = obj.getInt("type_id");
//                    double markerLat = obj.getDouble("latitude");
//                    double markerLong = obj.getDouble("longitude");
                    String markerName = obj.getString("name");
//                    String markerInfo = obj.getString("information");
                    String markerInfo = obj.getString("desc");

                    if (mydb.insertMarker(markerName, markerInfo)){
                        Log.d("db","successfully inserted marker");
                    }else{
                        Log.d("db", "failed to insert marker");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void ShowTypes(MenuItem item){
        Intent intent = new Intent(this, Attractions.class);

        startActivity(intent);
    }
}
