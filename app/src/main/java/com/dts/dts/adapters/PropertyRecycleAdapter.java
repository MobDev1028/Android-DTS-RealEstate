package com.dts.dts.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dts.dts.R;
import com.dts.dts.fragments.PropertyFragment;
import com.dts.dts.models.Img;
import com.dts.dts.models.Property;
import com.dts.dts.utils.Utils;
import com.dts.dts.views.CustomViewPager;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;

public class PropertyRecycleAdapter extends RecyclerView.Adapter<PropertyRecycleAdapter.ViewHolder> {

    private static final String TAG = PropertyRecycleAdapter.class.getSimpleName();
    private final int mItemWidth;
    private final int mItemHeight;
    public List<Property> moviesList;
    private Context mContext;
    private OnItemClickListener listener;

    private ArrayList<ImagePageAdapter> arrayImageAdapter = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        View btnHeart;
        View container;
        TextView propertyPrice;
        TextView propertyTerm;
        TextView bedCount;
        TextView bathCount;
        TextView sqftCount;

        ImageView ivStamp;

        TextView propertyAddress;
        View isNew;
        TextView imagesCount;
        CustomViewPager vPPropertyImages;

        HorizontalScrollView imageScrollView;
        LinearLayout contentView;

        List<Img> picImgs = new ArrayList<>();
        ImagePageAdapter imagePageAdapter;

        public ViewHolder(View view) {
            super(view);
            container = view;
            vPPropertyImages = (CustomViewPager) view.findViewById(R.id.vp_img_property);
            imagesCount = (TextView) view.findViewById(R.id.txt_count_images);
            isNew = view.findViewById(R.id.img_is_new);
            bedCount = (TextView) view.findViewById(R.id.txt_count_bedrooms);
            bathCount = (TextView) view.findViewById(R.id.txt_count_baths);
            sqftCount = (TextView) view.findViewById(R.id.txt_count_sq);
            propertyAddress = (TextView) view.findViewById(R.id.txt_property_address);
            propertyPrice = (TextView) view.findViewById(R.id.txt_property_price);
            propertyTerm = (TextView) view.findViewById(R.id.txt_term);
            btnHeart = view.findViewById(R.id.img_heart);

            ivStamp = (ImageView) view.findViewById(R.id.inrequired);


            vPPropertyImages.setLayoutParams(new RelativeLayout.LayoutParams(mItemWidth, mItemHeight));
            vPPropertyImages.setOffscreenPageLimit(2);



//            imageScrollView = (HorizontalScrollView) view.findViewById(R.id.image_scrollview);
//            contentView = (LinearLayout) view.findViewById(R.id.content_layout);
//
//            imageScrollView.setLayoutParams(new RelativeLayout.LayoutParams(mItemWidth, mItemHeight));
        }
    }

    public PropertyRecycleAdapter(Context context, List<Property> moviesList, int widthPixels, int itemHeight, OnItemClickListener listener) {
        this.moviesList = moviesList;
        mContext = context;
        mItemWidth = widthPixels;
        mItemHeight = itemHeight;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_property, parent, false);

        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Property movie = moviesList.get(moviesList.size() - position - 1);

        fillViews(movie, holder, position);
    }

    private void fillViews(Property property, ViewHolder holder, int position) {
        setupViewPager(holder, property, position);
        setupHeartButton(holder, property);

        holder.imagesCount.setText(String.format("1/%d", property.getImgs().size()));

        holder.propertyPrice.setText("$" + Utils.suffixNumber(property.getPrice()) + "/");
        holder.propertyTerm.setText(property.getTerm());
        holder.propertyAddress.setText(WordUtils.capitalize(property.getAddress1()));
        holder.bedCount.setText(property.getBed() + "");
        holder.bathCount.setText(property.getBath() + "");

        String sqFtString = property.getLotSize() + "";

        if (sqFtString.length() > 4) {
            float sqFtNumber = property.getLotSize();
//            String sqFt = Utils.suffixNumber(sqFtNumber);
            String sqFt = Math.round(sqFtNumber)+"";
            holder.sqftCount.setText(sqFt);
        }
        else {
            holder.sqftCount.setText(Math.round(property.getLotSize())+"");
        }

        holder.ivStamp.setVisibility(View.GONE);

        if (property.getInquired() > 0) {
            holder.ivStamp.setVisibility(View.VISIBLE);
        }


        holder.isNew.setVisibility(View.GONE);
    }

    private void setupHeartButton(final ViewHolder holder, final Property property) {
        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onHeartClick(moviesList.size() - holder.getAdapterPosition()-1);

            }
        });

        holder.btnHeart.setSelected(property.getLiked() == 1);
    }

    private void setupViewPager(final ViewHolder holder, final Property property, int position) {

        ImagePageAdapter imagePageAdapter = new ImagePageAdapter(mContext, property.getImgs(), R.layout.pageritem_prop_img);
        holder.vPPropertyImages.setAdapter(imagePageAdapter);

        holder.vPPropertyImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                holder.imagesCount.setText(position + 1 + "/" + property.getImgs().size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        holder.vPPropertyImages.setOnItemClickListener(new CustomViewPager.OnItemClickListener() {
            @Override
            public void onItemClick() {
                listener.onItemClick(moviesList.size() - holder.getAdapterPosition()-1);
            }
        });
    }




    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onHeartClick(int position);
    }


}