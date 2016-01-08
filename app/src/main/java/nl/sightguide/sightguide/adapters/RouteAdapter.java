package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.Route;

public class RouteAdapter extends RealmBaseAdapter<Route> implements ListAdapter {

    private final Activity activity;
    private View rowView;

    public RouteAdapter(Activity activity, RealmResults<Route> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Route route = realmResults.get(position);

        if(!route.getName().isEmpty()) {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.custom_route_list, null, true);

            TextView nameView = (TextView) rowView.findViewById(R.id.Itemname);
            TextView informationView = (TextView) rowView.findViewById(R.id.ItemInfo);
            TextView distanceView = (TextView) rowView.findViewById(R.id.distance);
            TextView routeId = (TextView) rowView.findViewById(R.id.RouteId);

            nameView.setText(route.getName());
            informationView.setText(route.getInfomation());
            routeId.setText("" + route.getId());
            distanceView.setText(Double.toString(route.getDistance()));


        }else{
            // else return empty view
            rowView = inflater.inflate(R.layout.custom_empty_list, null, true);
        }
        return rowView;
    }

}