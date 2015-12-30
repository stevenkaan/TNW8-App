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

    public AttractionAdapter(Activity activity, RealmResults<Marker> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_attraction_list, null, true);

        marker = realmResults.get(position);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.Itemname);
        TextView informationView = (TextView) rowView.findViewById(R.id.ItemInfo);

        if (marker.getImage_1() != null && !marker.getImage_1().isEmpty()) {
            Bitmap bitmap = ImageHelper.getImage(marker.getImage_1(), "marker");
            imageView.setImageBitmap(bitmap);
        }

        nameView.setText(marker.getName());
        informationView.setText(marker.getInformation());

        return rowView;
    }

}