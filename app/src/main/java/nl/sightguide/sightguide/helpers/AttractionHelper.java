package nl.sightguide.sightguide.helpers;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.activities.AttractionList;

public class AttractionHelper extends BaseAdapter {
    private final Activity context;
    private final String[][] items;
    private ImageView imageView;

    public AttractionHelper(Activity context, String[][] items) {
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
        View rowView=inflater.inflate(R.layout.image_list, null, true);

        imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.ItemInfo);

        String photoPath = Environment.getExternalStorageDirectory()+"/sightguide_images/"+items[position][3];
        Log.e("Path", photoPath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        imageView.setImageBitmap(bitmap);

        txtTitle.setText(items[position][1]);
        extratxt.setText("Description "+items[position][2]);
        return rowView;

    };

}