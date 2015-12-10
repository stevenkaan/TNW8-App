package nl.sightguide.sightguide.activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.models.City;

public class CityInfo extends AppCompatActivity {

    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        TextView informationView = (TextView)findViewById(R.id.city_info);
        int cityID = Utils.preferences.getInt("lastCity", 0);

        city = Utils.realm.where(City.class).equalTo("id", cityID).findFirst();

        setTitle(city.getName());

        informationView.setText(city.getInformation());
    }
}
