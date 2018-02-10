package fmplus.com.fmplusforstations;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

/**
 * Created by uomini on 2/6/2018.
 */

public class StationListAdapter extends ArrayAdapter<Stations> {
    private final Activity context;
    private final Stations[] nameObj;

    private boolean[] itemToggled;

    public StationListAdapter(Activity context, Stations[] nameObj) {
        super(context, R.layout.station_element, nameObj);

        this.context = context;
        this.nameObj = nameObj;
    }

    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        Stations stations = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        StationsHolder viewHolder; // view lookup cache stored in tag
        if (view == null) {
            viewHolder = new StationsHolder();
            LayoutInflater inflater = context.getLayoutInflater();

            view = inflater.inflate(R.layout.station_element, parent, false);
            viewHolder.favorite = view.findViewById(R.id.favorite);
            viewHolder.logo = view.findViewById(R.id.logo);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.description = view.findViewById(R.id.description);
            view.setTag(viewHolder);
        } else {
            viewHolder = (StationsHolder) view.getTag();
        }
        // Replace this with star if a favorite
        viewHolder.favorite.setImageDrawable(stations.favorite.getDrawable());
        viewHolder.logo.setImageDrawable(stations.logo.getDrawable());
        viewHolder.name.setText(stations.name);
        viewHolder.description.setText(stations.description);
        return view;
    }
}
