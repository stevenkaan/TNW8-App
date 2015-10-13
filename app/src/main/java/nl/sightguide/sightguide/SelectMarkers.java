package nl.sightguide.sightguide;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SelectMarkers extends AppCompatActivity {

    private String cityName;
    private int cityID;
    private int langID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_markers);

        Intent intent = getIntent();

        cityName = intent.getStringExtra("cityName");
        cityID = intent.getIntExtra("cityID", 0);
        langID = intent.getIntExtra("langID", 0);

        setTitle("List markers: "+cityName);

        String url = String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%d&lang_id=%d", 10, langID);
        new DownloadMarkersTask().execute(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_markers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_city) {
            startActivity(new Intent(SelectMarkers.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadMarkersTask extends AsyncTask<String, Void, String> {

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
                JSONArray markers = parent.getJSONArray("markers");

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);
                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("name", obj.getString("name"));
                    datum.put("desc", obj.getString("desc"));
                    data.add(datum);
                }
                SimpleAdapter adapter = new SimpleAdapter(SelectMarkers.this, data,
                        android.R.layout.simple_list_item_1,
                        new String[] {"name", "desc"},
                        new int[] {android.R.id.text1,
                                android.R.id.text2});

                ListView languageSelect = (ListView) findViewById(R.id.lvMarkers);
                languageSelect.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
