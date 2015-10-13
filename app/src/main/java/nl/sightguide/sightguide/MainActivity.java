package nl.sightguide.sightguide;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    JSONObject json;

    private PopupWindow pwindo;
    Button btnClosePopup;
    Button btnCreatePopup;

    private OnClickListener cancel_button_click_listener = new OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new DownloadCityTask().execute("http://www.stevenkaan.com/api/get_city.php");


        EditText inputSearch = (EditText) findViewById(R.id.itemSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        final ListView listView = (ListView) findViewById(R.id.view_cities);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                String name = o.toString();
                try {
                    JSONObject obj = MainActivity.this.json.getJSONObject(name);
                    Intent intent = new Intent(MainActivity.this, SelectLanguage.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("ID", obj.getInt("id"));
                    intent.putExtra("LanguageData", obj.getString("lang"));
                    startActivity(intent);

                    //initiatePopupWindow(obj.getJSONObject("lang"));
                    //Log.d("JSON", obj.getString("lang"));
                } catch (JSONException e){
                    Log.d("JSONException", e.toString());
                }


            }
        });
    }

    private void initiatePopupWindow(JSONObject json) {
        try {
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popop_language, (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 300, 370, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.update(300, 370);
            ArrayList<String> items = new ArrayList<String>();

            for(Iterator<String> keys = json.keys(); keys.hasNext(); ){
                String lang = keys.next();
                items.add(lang);
            }

            ListView listViewLang = (ListView) findViewById(R.id.listViewLang);
            adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, items);
            listViewLang.setAdapter(adapter);



            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadCityTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            JSONObject jsonObject;
            try {
                JSONObject parent = new JSONObject(result);

                jsonObject = parent.getJSONObject("cities");
                MainActivity.this.json = jsonObject;

                ArrayList<String> items = new ArrayList<String>();
                for(Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ){
                    String name = keys.next();
                    items.add(name);
                }

                ListView monthsListView = (ListView) findViewById(R.id.view_cities);
                adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, items);
                monthsListView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String downloadContent(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            int length = conn.getContentLength();

            // Convert the InputStream into a string
            String contentAsString = MainActivity.convertInputStreamToString(is, length);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[1024];
        reader.read(buffer);
        return new String(buffer);
    }
}