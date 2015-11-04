package nl.sightguide.sightguide;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttractionAdapter extends BaseAdapter {
    private final Activity context;
    private final String[][] items;

    public AttractionAdapter(Activity context, String[][] items) {
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
        Log.e("Test", "getView");

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.image_list, null, true);

        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.ItemInfo);

        //imageView.setImageResource(imgid[position]);
        txtTitle.setText(items[position][1]);
        extratxt.setText("Description "+items[position][2]);
        return rowView;

    };

}