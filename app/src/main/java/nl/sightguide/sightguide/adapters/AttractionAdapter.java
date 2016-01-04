package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
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
import nl.sightguide.sightguide.models.Marker;

public class AttractionAdapter extends RealmBaseAdapter<Marker> implements ListAdapter {

    private final Activity activity;
    private Marker marker;
    private View rowView;

    public AttractionAdapter(Activity activity, RealmResults<Marker> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        marker = realmResults.get(position);
        LayoutInflater inflater= activity.getLayoutInflater();


        // check if attraction has a description, if so show in list
        if(!marker.getInformation().isEmpty()){

            rowView = inflater.inflate(R.layout.custom_attraction_list, null, true);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            TextView nameView = (TextView) rowView.findViewById(R.id.Itemname);
            TextView informationView = (TextView) rowView.findViewById(R.id.ItemInfo);

            nameView.setText(marker.getName());
            informationView.setText(marker.getInformation());
            if (marker.getImage_1() != null && !marker.getImage_1().isEmpty()) {
                Bitmap bitmap = ImageHelper.getImage(marker.getImage_1(), "marker");
                imageView.setImageBitmap(bitmap);
            }

        }else{
            // else return empty view
            rowView = inflater.inflate(R.layout.custom_empty_list, null, true);
        }
        return rowView;
    }

}