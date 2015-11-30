package nl.sightguide.sightguide.adapters;

import android.app.Activity;
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
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;

public class RouteAdapter extends RealmBaseAdapter<Route> implements ListAdapter {

    private final Activity activity;

    public RouteAdapter(Activity activity, RealmResults<Route> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.route_list, null, true);

        Route route = realmResults.get(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.Itemname);
        TextView informationView = (TextView) rowView.findViewById(R.id.ItemInfo);
        TextView distanceView = (TextView) rowView.findViewById(R.id.distance);

        nameView.setText(route.getName());
        informationView.setText(route.getInfomation());
        distanceView.setText(Double.toString(route.getDistance()));

        return rowView;
    }

}