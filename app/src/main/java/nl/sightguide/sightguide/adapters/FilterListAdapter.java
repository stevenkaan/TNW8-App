package nl.sightguide.sightguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.Type;


public class FilterListAdapter extends RealmBaseAdapter<Type> implements ListAdapter {
    private final Activity activity;

    public FilterListAdapter(Activity activity, RealmResults<Type> results, boolean automaticUpdate) {
        super(activity.getApplicationContext(), results, automaticUpdate);
        this.activity = activity;
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_filter_list, null, true);

        Type type = realmResults.get(position);

        TextView filterName = (TextView) rowView.findViewById(R.id.FilterName);
        ImageView filterIcon = (ImageView) rowView.findViewById(R.id.TypeIcon);
        Switch filterDisplay = (Switch) rowView.findViewById(R.id.DisplayToggle);


        filterName.setText(type.getName_nl());


//        Context context = filterIcon.getContext();
//        int id = context.getResources().getIdentifier(type.getImage(), "drawable", context.getPackageName());
//        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), id);
//        filterIcon.setImageBitmap(icon);


        filterDisplay.setChecked(type.getDisplay());

        rowView.setTag(type);

        return rowView;
    }

}
