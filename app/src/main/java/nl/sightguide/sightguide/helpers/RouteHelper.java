package nl.sightguide.sightguide.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import nl.sightguide.sightguide.R;

public class RouteHelper extends BaseAdapter {
    private final Activity context;
    private final String[][] items;
    private ImageView imageView;

    public RouteHelper(Activity context, String[][] items) {
        this.context=context;
        this.items = items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String[] getItem(int position) {
        return this.items[position];
    }

    @Override
    public int getCount() {
        return this.items.length;
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.route_list, null, true);

        imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.ItemInfo);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);


        txtTitle.setText(items[position][1]);
        extratxt.setText(items[position][2]);
        distance.setText("2.4");
        return rowView;

    };

}