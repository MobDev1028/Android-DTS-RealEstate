package com.dts.dts.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dts.dts.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Dev E5550 on 11/24/2016.
 */

public class AmenityGridAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<String> list;
    private List<TextView> textViews = new ArrayList<>();
    private SparseBooleanArray selectedItems;

    public AmenityGridAdapter(Context context, List<String> amenities) {
        list = amenities;
        inflater = LayoutInflater.from(context);
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (contentView == null){
            contentView = inflater.inflate(R.layout.griditem_amenities, viewGroup, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        }
        else{
            holder = (ViewHolder) contentView.getTag();
        }


        holder.amenity.setText(list.get(position));
        if (selectedItems.get(position)) {
            holder.amenity.setSelected(true);
        } else {
            holder.amenity.setSelected(false);
        }

        final ViewHolder finalHolder = holder;
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    finalHolder.amenity.setSelected(false);
                } else {
                    selectedItems.put(position, true);
                    finalHolder.amenity.setSelected(true);
                }

            }
        });

        textViews.add(holder.amenity);
        return contentView;
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
//            TextView tv = textViews.get(pos);
//            tv.setSelected(false);
        } else {
            selectedItems.put(pos, true);
//            TextView tv = textViews.get(pos);I
//            tv.setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void setSelection(int pos) {
        selectedItems.put(pos, true);
    }

    public class ViewHolder{
        TextView amenity;

        public ViewHolder(View view) {
            amenity = (TextView) view.findViewById(R.id.btn_amenity_item);


        }
    }
}
