package com.dts.dts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.models.Property;
import com.dts.dts.utils.LocalPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyDitchRecycleAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;

    private List<Property> moviesList;
    private Context mContext;
    private OnItemClickListener listener;

    public class ViewHolder{
        ImageView ivProperty;
        TextView tv_address;
        TextView tv_status;
        RelativeLayout btnAction;

        Button deleteButton;
        public ViewHolder(View view) {
            ivProperty = (ImageView) view.findViewById(R.id.iv_property);
            btnAction = (RelativeLayout) view.findViewById(R.id.action_layout);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            tv_status = (TextView) view.findViewById(R.id.status);
            deleteButton = (Button) view.findViewById(R.id.delete);

        }
    }

    public MyDitchRecycleAdapter(Context context, List<Property> moviesList, OnItemClickListener listener) {
        this.moviesList = moviesList;
        mContext = context;
        this.listener = listener;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public Property getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        String url = null;

        if(convertView!=null) {
            convertView = null;
        }

        convertView = inflater.inflate(R.layout.listitem_ditch, null);

        holder = new ViewHolder(convertView);

        Property movie = moviesList.get(position);

        holder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPropertyClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(position);
            }
        });

        fillViews(movie, holder, position);




        return convertView;
    }



    private void fillViews(Property property, final ViewHolder holder, int position) {
        final String imgURL = property.getImgUrl().getSm();

        String address1 = property.getAddress1();
//        String city = property.getCity();
//        String state = property.getStateOrProvince();
//        String zip = property.getZip();

        holder.tv_address.setText(address1);
        holder.tv_status.setText(property.getStatus());
        Picasso.with(mContext).load(imgURL).placeholder(R.drawable.image_placeholder).into(holder.ivProperty);

    }

    public interface OnItemClickListener {
        void onPropertyClick(int position);

        void onDeleteClick(int position);
    }
}