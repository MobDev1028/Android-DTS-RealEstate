package com.dts.dts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.dts.dts.R;
import com.dts.dts.models.Parent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageRecycleAdapter extends BaseSwipeAdapter {

    private LayoutInflater inflater=null;

    private ArrayList<Parent> moviesList;
    private Context mContext;
    private OnItemClickListener listener;

    private CellExpanded lastCellExpanded;
    private CellExpanded NoCellExpanded = new CellExpanded(-1, -1);

    private JSONObject dictSelectedMessage;
    private JSONObject dictSelectedProperty;

    private int numberOfCellsExpanded;

    private boolean isInquired;


    public int total;

    ArrayList<LinearLayout> mArrayChildLayout = new ArrayList<>();

    public class MessageViewHolder{
        ImageButton btnProperty;
        TextView tv_subject;
        TextView tv_address;
        TextView tv_country;
        TextView tv_duration;
        ImageButton btnAction;

        public MessageViewHolder(View view) {
            btnProperty = (ImageButton) view.findViewById(R.id.btn_property);
            tv_subject = (TextView) view.findViewById(R.id.tv_title);
            tv_address = (TextView) view.findViewById(R.id.tv_duration);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            btnAction = (ImageButton) view.findViewById(R.id.arrow_button);
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
    public MessageRecycleAdapter(Context context, ArrayList<Parent> moviesList, OnItemClickListener listener) {
        this.moviesList = moviesList;
        mContext = context;
        this.listener = listener;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    @Override
    public int getCount() {
        return total;
    }

    @Override
    public Parent getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        ActualPostion actualPostion = findParent(position);
        View v = null;
        if (actualPostion.mIsParentCell == false)
        {
            v = LayoutInflater.from(mContext).inflate(R.layout.listitem_message_child, null);
            SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
            swipeLayout.setEnabled(false);
        }
        else{
            v = LayoutInflater.from(mContext).inflate(R.layout.listitem_message, null);

            SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
            v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        final ActualPostion actualPostion = findParent(position);

        int parent = actualPostion.mParent;
        boolean isParentCell = actualPostion.mIsParentCell;
        int aPosition = actualPostion.mActualPosition;
        if (isParentCell == false)
        {
            ChildViewHolder holder = new ChildViewHolder(convertView);
            JSONObject dictMessage = moviesList.get(parent).childs.get(position - aPosition - 1);
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            final MessageViewHolder holder = new MessageViewHolder(convertView);
            JSONObject dictMessage = moviesList.get(parent).dict;
            JSONObject dictProperty = moviesList.get(parent).dictProperty;
            try {
                String type = dictMessage.getString("type");
                if (type.equalsIgnoreCase("doc_sign"))
                {
                    holder.tv_subject.setText("SIGN LEASE");
                    if (dictMessage.getInt("declined") != 0)
                    {
//                    holder.btnAction.setTitle(DECLINED);
                    }
                    else if(dictMessage.getJSONObject("doc") != null)
                    {
                        if (dictMessage.getJSONObject("doc").getInt("signed") == 0)
                        {
                            //     holder.btnAction.setTitle("SIGN DOC");

                        }
                        else {
                            //     holder.btnAction.setTitle("SIGNED");
                        }

                    }
                    else{
                        //     holder.btnAction.setTitle("SIGN DOC");

                    }
                }
                else if (type.equalsIgnoreCase("follow_up"))
                {
                    holder.tv_subject.setText("FOLLOW_UP");
                }
                else if (type.equalsIgnoreCase("demo"))
                {
                    holder.tv_subject.setText("ON-SITE DEMO");
                }
                else if (type.equalsIgnoreCase("inquire"))
                {
                    holder.tv_subject.setText("INQUIRED");
//                holder.tv_subject.setTextColor(0x02ce37);
                }
                else
                    holder.tv_subject.setText(String.format("%d", type).toUpperCase());


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

                }
            });

            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItem(actualPostion, position);
                }
            });

        }

    }

    public void selectedItem(ActualPostion actualPosition, int postion)
    {
        int parent = actualPosition.mParent;
        boolean isParentCell = actualPosition.mIsParentCell;
        int aPosition = actualPosition.mActualPosition;

        if (isParentCell)
        {}
        else
        {
            isInquired = false;
            dictSelectedMessage = moviesList.get(parent).childs.get(postion - aPosition - 1);
            dictSelectedProperty = moviesList.get(parent).dictProperty;

            try {
                if (dictSelectedMessage.getString("type").equalsIgnoreCase("doc_sign"))
                {
//                    self.performSegueWithIdentifier("messageToDoc", sender: self)
                }
                else if (dictSelectedMessage.getString("type").equalsIgnoreCase("demo")) {
//                    self.performSegueWithIdentifier("messagesToDemo", sender: self)
                }
                else if (dictSelectedMessage.getString("type").equalsIgnoreCase("inquire")) {
                    isInquired = true;
//                    self.performSegueWithIdentifier("messageToFollowUp", sender: self)
                }
                else {
//                    self.performSegueWithIdentifier("messageToFollowUp", sender: self)
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        updateCells(parent, postion);
        this.notifyDataSetChanged();
    }

    private void updateCells(int parent, int index) {

        switch (moviesList.get(parent).State) {

            case 1: //Parent.Expand
//                collapseSubItemsAtIndex(index, parent);
                lastCellExpanded = NoCellExpanded;
//                selectedParent = -1;
//                selectedIndex = -1;
                break;
            case 0: //Parent.Collapsed:
                switch (numberOfCellsExpanded) {
                    case 0:
                    // exist one cell expanded previously
//                        if (lastCellExpanded != NoCellExpanded) {
//                            int indexOfCellExpanded = lastCellExpanded.indexCellExpanded;
//                            int parentOfCellExpanded = lastCellExpanded.parentCellexpanded;
//
//                            collapseSubItemsAtIndex(indexOfCellExpanded, parentOfCellExpanded);
//
//                    // cell tapped is below of previously expanded, then we need to update the index to expand.
//                            if (parent > parentOfCellExpanded) {
//                                int newIndex = index - moviesList.get(parentOfCellExpanded).childs.size();
//                                expandItemAtIndex(newIndex, parent);
//                                lastCellExpanded = new CellExpanded(newIndex, parent);
//                            }
//                            else {
//                                expandItemAtIndex(index, parent);
//                                lastCellExpanded = new CellExpanded(index, parent);
//                            }
//                        }
//                        else {
//                            expandItemAtIndex(index, parent);
//                            lastCellExpanded = new CellExpanded(index, parent);
//                        }
//                    case 1://.Several:
//                        expandItemAtIndex(index, parent);
            }
        }
    }

//    private void collapseSubItemsAtIndex(int index, int parent) {
//
//        var indexPaths = [NSIndexPath]()
//
//        int numberOfChilds = moviesList.get(parent).childs.size();
//
//        // update the state of the cell.
//        moviesList.get(parent).State = 1;//.Collapsed
//
//        if( index + 1 <= index + numberOfChilds){} else { return; }
//
//        // create an array of NSIndexPath with the selected positions
//        indexPaths = (index + 1...index + numberOfChilds).map { NSIndexPath(forRow: $0, inSection: 0)}
//
//        // remove the expanded cells
//        self.tblMessages.deleteRowsAtIndexPaths(indexPaths, withRowAnimation: UITableViewRowAnimation.Fade)
//
//        // update the total of rows
//        self.total -= numberOfChilds
//    }
    private ActualPostion findParent(int index) {

        int position = 0, parent = 0;
        if(position >= index) {
            return new ActualPostion(parent, true, parent);
        }

        Parent item = moviesList.get(parent);

        do {

            if (item.State == Parent.Expanded)
                position += item.childs.size() + 1;
            else if (item.State == Parent.Collapsed)
                position += 1;

            parent += 1;

            // if is not outside of dataSource boundaries
            if (parent < moviesList.size()) {
                item = moviesList.get(parent);
            }

        } while (position < index);

            // if it's a parent cell the indexes are equal.
            if (position == index) {
                return new ActualPostion(parent, position == index, position);
        }

        item = moviesList.get(parent - 1);
        return new ActualPostion(parent - 1, position == index, position - item.childs.size() - 1);
    }


    public class ActualPostion
    {
        public int mParent;
        public boolean mIsParentCell;
        public int mActualPosition;
        public ActualPostion(int parent, boolean isParentCell, int actualPosition){
            mParent = parent;
            mIsParentCell = isParentCell;
            mActualPosition = actualPosition;
        }

    }

    public class CellExpanded
    {
        public int indexCellExpanded;
        public int parentCellexpanded;

        public CellExpanded(int ic, int pc)
        {
            indexCellExpanded = ic;
            parentCellexpanded = pc;
        }
        public CellExpanded()
        {

        }
    }

    public interface OnItemClickListener {

        void onActionClick(int position);

        void onPropertyClick(int position);
    }
}