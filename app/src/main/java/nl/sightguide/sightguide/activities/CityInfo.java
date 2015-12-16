package nl.sightguide.sightguide.activities;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.City;

public class CityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        City city = Utils.realm.where(City.class).equalTo("id", Utils.city_id).findFirst();


        TextView informationView = (TextView)findViewById(R.id.city_info);
        ImageView image1 = (ImageView)findViewById(R.id.mainImage1);
        ImageView image2 = (ImageView)findViewById(R.id.mainImage2);
        ImageView image3 = (ImageView)findViewById(R.id.mainImage3);
        ImageView image4 = (ImageView)findViewById(R.id.mainImage4);

        int totalImages = 0;

        if(!city.getImage_1().equals("none") && !city.getImage_1().equals("")) {
            image1.setImageBitmap(ImageHelper.getImage(city.getImage_1(), "city"));
            image1.setVisibility(View.VISIBLE);
            totalImages++;
        }
        if(!city.getImage_2().equals("none") && !city.getImage_2().equals("")) {
            image2.setImageBitmap(ImageHelper.getImage(city.getImage_2(), "city"));
            image2.setVisibility(View.VISIBLE);
            totalImages++;
        }
        if(!city.getImage_3().equals("none") && !city.getImage_3().equals("")) {
            image3.setImageBitmap(ImageHelper.getImage(city.getImage_3(), "city"));
            image3.setVisibility(View.VISIBLE);
            totalImages++;
        }
        if(!city.getImage_4().equals("none") && !city.getImage_4().equals("")) {
            image4.setImageBitmap(ImageHelper.getImage(city.getImage_4(), "city"));
            image4.setVisibility(View.VISIBLE);
            totalImages++;
        }


        if(totalImages == 0){
            HorizontalScrollView hsv = (HorizontalScrollView)findViewById(R.id.horizontalView);
            hsv.setVisibility(View.INVISIBLE);
        }

        setTitle(city.getName());
        informationView.setText(city.getInformation());
    }
}
