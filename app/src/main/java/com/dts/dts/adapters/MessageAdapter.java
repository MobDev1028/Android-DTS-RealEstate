package com.dts.dts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.models.Parent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by silver on 12/24/16.
 */

public class MessageAdapter extends BaseExpandableListAdapter{
    private Context mContext;
    private OnItemClickListener mListener;


    private ArrayList<Parent> m_dataSource;

    public MessageAdapter(Context context, ArrayList<Parent> dataSource, OnItemClickListener listener)
    {
        mContext = context;
        m_dataSource = dataSource;
        mListener = listener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return m_dataSource.get(groupPosition).childs.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView != null) {
            convertView = null;
        }
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listitem_message_child, null);


        ChildViewHolder holder = new ChildViewHolder(convertView);
        JSONObject dictMessage = m_dataSource.get(groupPosition).childs.get(childPosition);
        try {
            if (dictMessage.getString("type").equalsIgnoreCase("doc_sign"))
                holder.tv_subject.setText("SIGN LEASE");
            else if (dictMessage.getString("type").equalsIgnoreCase("follow_up"))
                holder.tv_subject.setText("FOLLOW_UP");
            else if (dictMessage.getString("type").equalsIgnoreCase("demo"))
                holder.tv_subject.setText("ON-SITE DEMO");
            else if (dictMessage.getString("type").equalsIgnoreCase("inquire"))
                holder.tv_subject.setText("INQUIRED");
            else
                holder.tv_subject.setText(dictMessage.getString("type").toUpperCase());

            holder.tv_duration.setText(dictMessage.getString("updated_at_formatted"));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onActionClick(groupPosition, childPosition);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                .size();

        return m_dataSource.get(groupPosition).childs.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_dataSource.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return m_dataSource.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView != null) {
            convertView = null;
        }

        LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.listitem_message, null);


        final MessageViewHolder holder = new MessageViewHolder(convertView);

        holder.deletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mListener.onDeleteMessage(groupPosition);
            }
        });

        JSONObject dictMessage = m_dataSource.get(groupPosition).dict;
        JSONObject dictProperty = m_dataSource.get(groupPosition).dictProperty;
        try {
            String type = dictMessage.getString("type");
            holder.tv_subject.setTextColor(Color.parseColor("#ff0500"));

            if (type.equalsIgnoreCase("doc_sign"))
            {
                holder.tv_subject.setText("SIGN LEASE");
                if (dictMessage.getInt("declined") != 0)
                {
                    holder.btnAction.setText("DECLINED");
                }
                else if(!dictMessage.isNull("doc"))
                {
                    if (dictMessage.getJSONObject("doc").getInt("signed") == 0)
                    {
                             holder.btnAction.setText("SIGN");

                    }
                    else {
                             holder.btnAction.setText("SIGNED");
                    }

                }
                else{
                         holder.btnAction.setText("SIGN");

                }
            }
            else if (type.equalsIgnoreCase("follow_up"))
            {
                holder.tv_subject.setText("FOLLOW_UP");
                if (dictMessage.getInt("replies") == 0)
                    holder.btnAction.setText("FOLLOW");
                else
                    holder.btnAction.setText("REPLIED");

            }
            else if (type.equalsIgnoreCase("demo"))
            {
                holder.tv_subject.setText("ON-SITE DEMO");
                if (dictMessage.getInt("accepted") != 0)
                    holder.btnAction.setText("ACCEPTED");
                else if (dictMessage.getInt("declined") != 0)
                    holder.btnAction.setText("DECLINED");
                else
                    holder.btnAction.setText("DEMO");
            }
            else if (type.equalsIgnoreCase("inquire"))
            {
                holder.tv_subject.setText("INQUIRED");
                holder.tv_subject.setTextColor(Color.parseColor("#02ce37"));

                if (dictMessage.getInt("replies") == 0)
                    holder.btnAction.setText("VIEW");
                else
                    holder.btnAction.setText("REPLIED");
            }
            else {
                holder.tv_subject.setText(type.toUpperCase());

                if (dictMessage.getInt("replies") == 0)
                    holder.btnAction.setText("VIEW");
                else
                    holder.btnAction.setText("REPLIED");
            }

            holder.tv_duration.setText(dictMessage.getString("updated_at_formatted"));
            String address1 = dictProperty.getString("address1");
            String city = dictProperty.getString("city");
            String state = dictProperty.getString("state_or_province");
            String zip = dictProperty.getString("zip");

            holder.tv_address.setText(address1);
            holder.tv_country.setText(city + ", " + state + " " + zip);

            final String imgURL = dictProperty.getJSONObject("img_url").getString("sm");

            Picasso.with(mContext).load(imgURL).networkPolicy(NetworkPolicy.OFFLINE).into(holder.btnProperty, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mContext).load(imgURL).into(holder.btnProperty);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPropertyClick(groupPosition);

            }
        });

        holder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActionClick(groupPosition, 0);
            }
        });


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class MessageViewHolder{
        ImageButton btnProperty;
        TextView tv_subject;
        TextView tv_address;
        TextView tv_country;
        TextView tv_duration;
        TextView btnAction;
        Button deletButton;

        public MessageViewHolder(View view) {
            btnProperty = (ImageButton) view.findViewById(R.id.btn_property);
            tv_subject = (TextView) view.findViewById(R.id.tv_title);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            btnAction = (TextView) view.findViewById(R.id.btn_action);
            deletButton = (Button) view.findViewById(R.id.delete);
        }
    }

    public class ChildViewHolder{
        TextView tv_subject;
        TextView tv_duration;

        public ChildViewHolder(View view) {
            tv_subject = (TextView) view.findViewById(R.id.tv_subject_child);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration_child);

        }
    }

    public interface OnItemClickListener {

        void onActionClick(int parentPosition, int childPosition);

        void onPropertyClick(int position);

        void onDeleteMessage(int postion);
    }
}
