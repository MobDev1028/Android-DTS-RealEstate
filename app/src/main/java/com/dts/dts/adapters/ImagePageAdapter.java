package com.dts.dts.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dts.dts.R;
import com.dts.dts.models.Img;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.MaskTransformation;

/**
 * Created by BilalHaider on 4/21/2016.
 */
public class ImagePageAdapter extends PagerAdapter {
    private static final String TAG = ImagePageAdapter.class.getSimpleName();
    private final int itemRes;
    private Context mContext;
    public List<Img> urlList;
    private LayoutInflater inflater;
    private int tag;

    public ImagePageAdapter(Context context, List<Img> picList, int itemRes) {
        mContext = context;
        urlList = picList;
        inflater = LayoutInflater.from(context);
        this.itemRes = itemRes;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(itemRes, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        System.gc();
        Picasso.with(mContext).load(urlList.get(position).getImgUrl().getMd())
                .transform(new MaskTransformation(mContext, R.drawable.mask_listitem_property))
                .placeholder(R.drawable.image_placeholder)
                .centerCrop().fit()
                .into(viewHolder.imageView);


        container.addView(view);
        view.setTag(viewHolder);
        return view;

    }

    public void setTag(int t)
    {
        tag = t;
    }

    public int getTag()
    {
        return tag;
    }

    @Override
    public int getCount() {
        return urlList.size();
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
        public ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view;
        }
    }
}
