package com.dts.dts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.models.Property;
import com.dts.dts.utils.LocalPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FollowUpAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;

    private Context mContext;

    private ArrayList<JSONObject> allMessages;

    public class ViewHolder{
        TextView tv_title;
        TextView tv_duration;
        TextView tv_content;

        public ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
        }
    }

    public FollowUpAdapter(Context context, ArrayList<JSONObject> messages) {
        allMessages = messages;
        mContext = context;
//        this.listener = listener;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return allMessages.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return allMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        String url = null;

        if(convertView!=null){
            convertView = null;
        }
        {
            convertView = inflater.inflate(R.layout.follow_cell, null);
            holder = new ViewHolder(convertView);

            JSONObject dictMessage = allMessages.get(position);
            JSONObject dictRecipient = null;
            try {
                if (dictMessage.has("recipient")){

                    dictRecipient = dictMessage.getJSONObject("recipient");

                    if (dictRecipient != null) {
                        String cidRecipient = dictRecipient.getString("cid");
                        if (cidRecipient != null) {
                            String userCid = LocalPreferences.getUserCid(mContext);
                            if (userCid != null) {
                                if (cidRecipient.equalsIgnoreCase(userCid)) {
                                    holder.tv_title.setText("BROKER SAID");
                                    holder.tv_title.setTextColor(Color.parseColor("#02ce37"));
                                }
                                else {
                                    holder.tv_title.setText("YOU SAID");
                                    holder.tv_title.setTextColor(Color.parseColor("#ff0500"));
                                }
                            }
                        }
                    }

                }
                else{
                    holder.tv_title.setText("YOU SAID");
                    holder.tv_title.setTextColor(Color.parseColor("#ff0500"));
                }

                holder.tv_duration.setText(dictMessage.getString("updated_at_formatted"));
                holder.tv_content.setText(dictMessage.getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }



        return convertView;
    }


}