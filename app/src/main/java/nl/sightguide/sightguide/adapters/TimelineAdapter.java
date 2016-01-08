package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;

public class TimelineAdapter extends RealmBaseAdapter<Marker> implements ListAdapter {

    private final Activity activity;
    private Route route;
    private int routeId;
    private View rowView;

    public TimelineAdapter(Activity activity, RealmResults<Marker> results, boolean automaticUpdate, int id) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
        routeId = id;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        Marker marker = realmResults.get(position);
        int currentMarker = marker.getId();

        route = Utils.realm.where(Route.class).equalTo("id", routeId).findFirst();
        RealmList<Marker> markers = route.getMarkers();

        rowView = inflater.inflate(R.layout.custom_empty_list, null, true);

        for(int i = 0; i < markers.size(); i++) {
            Marker thisMarker = markers.get(i);
            int thisMarkerId = thisMarker.getId();

            if(thisMarkerId == currentMarker ){
                Log.e("equal?","this marker: "+ thisMarkerId +" = currentmarker: "+currentMarker);
                rowView = inflater.inflate(R.layout.custom_timeline_list, null, true);
                TextView nameView = (TextView) rowView.findViewById(R.id.Itemname);
                TextView informationView = (TextView) rowView.findViewById(R.id.ItemInfo);
                ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

                if (marker.getImage_1() != null && !marker.getImage_1().isEmpty()) {
                    Bitmap bitmap = ImageHelper.getImage(marker.getImage_1(), "marker");
                    icon.setImageBitmap(bitmap);
                }

                nameView.setText(marker.getName());
                informationView.setText(marker.getInformation());
            }
        }
        return rowView;


    }

}
