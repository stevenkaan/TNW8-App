package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.services.ProximitySensor;

public class CityInfo extends AppCompatActivity {
    private City city;
    private ImageView image;
    private int img = 1;
    int totalImages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        if(getSharedPreferences("SightGuide", 0).getBoolean("proximitySensorActive", true)){
            stopService(new Intent(this, ProximitySensor.class));
        }

        city = Utils.realm.where(City.class).equalTo("id", Utils.city_id).findFirst();

        TextView cityName = (TextView) findViewById(R.id.cityName);
        TextView informationView = (TextView) findViewById(R.id.cityInfo);
        image = (ImageView) findViewById(R.id.mainImage);

        // set first image and count nr of images
        if (!city.getImage_1().equals("none") && !city.getImage_1().equals("")) {
            image.setImageBitmap(ImageHelper.getImage(city.getImage_1(), "city"));
            totalImages++;
            if (!city.getImage_2().equals("none") && !city.getImage_2().equals("")) {
                totalImages++;
                if (!city.getImage_3().equals("none") && !city.getImage_3().equals("")) {
                    totalImages++;
                    if (!city.getImage_4().equals("none") && !city.getImage_4().equals("")) {
                        totalImages++;
                    }
                }
            }
        }

        setTitle(city.getName());
        cityName.setText(city.getName());
        informationView.setText(city.getInformation());

        // show nav btns if more than 1 img
        if(totalImages >  1) {
            ImageView left = (ImageView) findViewById(R.id.left);
            ImageView right = (ImageView) findViewById(R.id.right);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
        }
    }

    // show next img
    public void nextImg(View v){
        img++;
        if(img > totalImages){
            img = 1;
        }
        setImage(img);
    }
    // show previous img
    public void prevImg(View v){
        img--;
        if(img < 1){
            img = totalImages;
        }
        setImage(img);
    }
    public void setImage (int img){
        switch (img) {
            case 1:
                image.setImageBitmap(ImageHelper.getImage(city.getImage_1(), "city"));
                break;
            case 2:
                image.setImageBitmap(ImageHelper.getImage(city.getImage_2(), "city"));
                break;
            case 3:
                image.setImageBitmap(ImageHelper.getImage(city.getImage_3(), "city"));
                break;
            case 4:
                image.setImageBitmap(ImageHelper.getImage(city.getImage_4(), "city"));
                break;
            default:
                image.setImageBitmap(ImageHelper.getImage(city.getImage_1(), "city"));
                break;
        }
    }

}
