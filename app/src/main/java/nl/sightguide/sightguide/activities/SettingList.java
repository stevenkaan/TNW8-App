package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.models.City;


public class SettingList extends AppCompatActivity {

    private TextView setCity;
    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        setCity =  (TextView) findViewById(R.id.city_name);

        int cityID = Utils.preferences.getInt("lastCity", 0);
        city = Utils.realm.where(City.class).equalTo("id", cityID).findFirst();
        setCity.setText(city.getName());

    }

    public void SelectCity (View v) {
        Intent intent = new Intent(this, CityList.class);
        startActivity(intent);
    }
    public void SetFilter (View v) {
        Intent intent = new Intent(this, FilterList.class);
        startActivity(intent);
    }
    public void ToggleSensor () {

    }
    public void ToggleAutoplay () {

    }
}
