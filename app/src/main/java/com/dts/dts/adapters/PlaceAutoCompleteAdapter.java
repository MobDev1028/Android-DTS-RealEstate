package com.dts.dts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dts.dts.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaceAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private static final String TAG = PlaceAutoCompleteAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> resultList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private Gson gson = new GsonBuilder().create();

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_cities, parent, false);
        }
        ((TextView) convertView).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> places = findPlaces(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = places;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<String> findPlaces(String s) {
        List<String> places = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
                            "key=AIzaSyCOd0Y4CQdO05VWv6k3wZwZvq9RkfMlFgE&input=" + s +
                            "&types=(cities)&components=country:usa")
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JsonArray respObj = gson.fromJson(response.body().string(), JsonObject.class).getAsJsonArray("predictions");
            places.add("Current Location");
            for (int i = 0; i < respObj.size(); i++) {
                places.add(respObj.get(i).getAsJsonObject().get("description").getAsString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return places;
    }
}
