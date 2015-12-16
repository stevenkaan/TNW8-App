package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.LauncherCity;

public class LauncherAdapter extends ArrayAdapter<LauncherCity> implements ListAdapter {

    private final Activity activity;

    public LauncherAdapter(Activity activity, int resource, ArrayList<LauncherCity> items){
        super(activity.getApplicationContext(), resource, items);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_launcher_list, null, true);

        LauncherCity city = getItem(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.CityName);
        TextView countryView = (TextView) rowView.findViewById(R.id.CountryName);

        nameView.setText(city.getName());
        countryView.setText(city.getCountry());

        rowView.setTag(city);

        return rowView;
    }
}
