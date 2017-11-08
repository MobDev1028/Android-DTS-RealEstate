package com.dts.dts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.models.Property;
import com.dts.dts.utils.LocalPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteRecycleAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;

    private List<Property> moviesList;
    private Context mContext;
    private OnItemClickListener listener;

    public class ViewHolder{
        ImageButton btnProperty;
        TextView tv_subject;

        TextView tv_address;
        TextView tv_country;
        ImageButton btnAction;

        Button hideButton;
        Button unfavBtton;
        public ViewHolder(View view) {
            btnProperty = (ImageButton) view.findViewById(R.id.btn_property);
            btnAction = (ImageButton) view.findViewById(R.id.arrow_button);
            tv_subject = (TextView) view.findViewById(R.id.tv_title);
            tv_address = (TextView) view.findViewById(R.id.tv_duration);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            hideButton = (Button) view.findViewById(R.id.hide);
            unfavBtton = (Button) view.findViewById(R.id.unfav);

        }
    }

    public FavoriteRecycleAdapter(Context context, List<Property> moviesList, OnItemClickListener listener) {
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
//        else
//        {
//            holder = (ViewHolder) convertView.getTag();
//        }

        convertView = inflater.inflate(R.layout.listitem_favorite, null);

        holder = new ViewHolder(convertView);

        Property movie = moviesList.get(position);

            holder.btnAction.setVisibility(View.INVISIBLE);
            if(!LocalPreferences.getUserToken(mContext).isEmpty()) {
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onActionClick(position);
                    }
                });
            }

            holder.btnProperty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPropertyClick(position);
                }
            });

            holder.unfavBtton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onUnfavClick(position);
                }
            });

            final ViewHolder finalHolder = holder;
            holder.hideButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onHideClick(position);
                }
            });


            if (movie.getHidden() == 1) {
                convertView.setBackgroundColor(Color.RED);
                holder.hideButton.setText("UNHIDE");
                holder.unfavBtton.setVisibility(View.GONE);
            }
            fillViews(movie, holder, position);

//        }



        return convertView;
    }



    private void fillViews(Property property, final ViewHolder holder, int position) {
        holder.tv_subject.setText(property.getDateLikedFormatted());

        final String imgURL = property.getImgUrl().getSm();

        String address1 = property.getAddress1();
        String city = property.getCity();
        String state = property.getStateOrProvince();
        String zip = property.getZip();

        holder.tv_address.setText(address1);
        holder.tv_country.setText(city + ", " + state + " " + zip);

        Picasso.with(mContext).load(imgURL).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder).into(holder.btnProperty, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(mContext).load(imgURL).into(holder.btnProperty);
            }
        });

    }

    public interface OnItemClickListener {

        void onActionClick(int position);

        void onPropertyClick(int position);

        void onHideClick(int position);

        void onUnfavClick(int postion);
    }
}