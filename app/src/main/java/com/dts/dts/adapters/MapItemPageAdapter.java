package com.dts.dts.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.models.Property;
import com.dts.dts.utils.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by BilalHaider on 4/21/2016.
 */
public class MapItemPageAdapter extends PagerAdapter {
    private static final String TAG = MapItemPageAdapter.class.getSimpleName();
    private Context mContext;
    private List<Property> properties;
    private LayoutInflater inflater;
    private OnMapItemClickListener listener;

    public MapItemPageAdapter(Context context, List<Property> propertyList,OnMapItemClickListener listener) {
        mContext = context;
        properties = propertyList;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.pageritem_map_detail, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        final Property property = properties.get(position);
        //viewHolder.imageView.setImageUrl(App.analysis.get(position).getUrl(), App.instance.getImageLoader());
        Picasso.with(mContext).load(property.getImgUrl().getMd()).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                String json = gson.toJson(property); //convert
                listener.onImageClick(json);
//                String propertyJson = propertyDic.toString();

            }
        });

        viewHolder.propertyPrice.setText("$" + Utils.suffixNumber(property.getPrice()) + "/month");
        viewHolder.propertyAddress.setText(property.getAddress1());
        viewHolder.bedCount.setText(property.getBed() + "");
        viewHolder.bathCount.setText(property.getBath() + "");

        String sqFtString = property.getLotSize() + "";

        if (sqFtString.length() > 4) {
            float sqFtNumber = property.getLotSize();
            String sqFt = Utils.suffixNumber(sqFtNumber);
            viewHolder.sqCount.setText(sqFt);
        }
        else {
            viewHolder.sqCount.setText(sqFtString);
        }

        viewHolder.inrequire.setVisibility(View.GONE);

        if (property.getInquired() > 0) {
            viewHolder.inrequire.setVisibility(View.VISIBLE);
        }

        viewHolder.btnHeart.setSelected(property.getLiked() == 1);

        viewHolder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onHeartClick(position);
            }
        });
        container.addView(view);

//        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onImageClick(position);
//            }
//        });

        return view;

    }

    @Override
    public int getCount() {
        return properties.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private class ViewHolder {
        ImageView imageView;
        TextView propertyPrice;
        TextView bedCount;
        TextView bathCount;
        TextView sqCount;
        TextView propertyAddress;
        View btnHeart;
        View isNew;
        ImageView inrequire;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.img_property);
            isNew = view.findViewById(R.id.img_is_new);
            bedCount = (TextView) view.findViewById(R.id.txt_count_bedrooms);
            bathCount = (TextView) view.findViewById(R.id.txt_count_baths);
            sqCount = (TextView) view.findViewById(R.id.txt_count_sq);
            propertyAddress = (TextView) view.findViewById(R.id.txt_property_address);
            propertyPrice = (TextView) view.findViewById(R.id.txt_property_price);
            btnHeart = view.findViewById(R.id.img_heart);
            inrequire = (ImageView) view.findViewById(R.id.iv_inrequire);
        }
    }

    public interface OnMapItemClickListener {
        void onHeartClick(int position);
        void onImageClick(String json);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
