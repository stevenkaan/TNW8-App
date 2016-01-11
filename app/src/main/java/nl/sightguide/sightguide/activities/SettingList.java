package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.services.ProximitySensor;


public class SettingList extends AppCompatActivity {

    private TextView setCity;
    private City city;

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);

        if(getSharedPreferences("SightGuide", 0).getBoolean("proximitySensorActive", true)){
            stopService(new Intent(this, ProximitySensor.class));
        }

        setCity =  (TextView) findViewById(R.id.city_name);

        int cityID = Utils.preferences.getInt("lastCity", 0);
        city = Utils.realm.where(City.class).equalTo("id", cityID).findFirst();
        setCity.setText(city.getName());

        settings = getSharedPreferences("SightGuide", 0);
        editor = settings.edit();

        CheckBox sensor = (CheckBox) findViewById(R.id.sensor);
        if(Utils.preferences.getBoolean("proximitySensor", true)){
            sensor.setChecked(true);
        }
        CheckBox autoplay = (CheckBox) findViewById(R.id.autoPlay);
        if(Utils.preferences.getBoolean("autoPlay", true)){
            autoplay.setChecked(true);
        }

    }

    public void SelectCity (View v) {
        Intent intent = new Intent(this, CityList.class);
        startActivity(intent);
    }
    public void SetFilter (View v) {
        Intent intent = new Intent(this, FilterList.class);
        startActivity(intent);
    }
    public void ToggleSensor (View v) {
        if(Utils.preferences.getBoolean("proximitySensor", true)){
            editor.putBoolean("proximitySensor", false);
            stopService(new Intent(this, ProximitySensor.class));
        }else{
            editor.putBoolean("proximitySensor", true);
        }
        editor.commit();
    }
    public void ToggleAutoplay(View v) {
        if(Utils.preferences.getBoolean("autoPlay", true)){
            editor.putBoolean("autoPlay", false);
        }else{
            editor.putBoolean("autoPlay", true);
        }
        editor.commit();
    }
}
