package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.LauncherCity;
import nl.sightguide.sightguide.models.Marker;


public class CityListAdapter extends RealmBaseAdapter<City> implements ListAdapter {
    private final Activity activity;

    public CityListAdapter(Activity activity, RealmResults<City> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_launcher_list, null, true);

        City city = realmResults.get(position);

        TextView idView = (TextView) rowView.findViewById(R.id.CityId);
        TextView nameView = (TextView) rowView.findViewById(R.id.CityName);
        TextView countryView = (TextView) rowView.findViewById(R.id.CountryName);

        idView.setText(String.format("%d", city.getId()));
        nameView.setText(city.getName());
        countryView.setText(city.getCountry());

        rowView.setTag(city);

        return rowView;
    }
}
