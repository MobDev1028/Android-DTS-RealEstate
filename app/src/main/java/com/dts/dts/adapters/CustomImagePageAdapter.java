package com.dts.dts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dts.dts.R;
import com.dts.dts.models.Img;
import com.dts.dts.views.CustomImageVIew;
import com.dts.dts.views.ImageViewTouch;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by BilalHaider on 4/21/2016.
 */
public class CustomImagePageAdapter extends PagerAdapter {
    private static final String TAG = CustomImagePageAdapter.class.getSimpleName();
    private final int itemRes;
    private float screenWidth;
    private Context mContext;
    private List<Img> urlList;
    private LayoutInflater inflater;


    public CustomImagePageAdapter(Context context, List<Img> picList, int itemRes, float sw) {
        mContext = context;
        urlList = picList;
        inflater = LayoutInflater.from(context);
        this.itemRes = itemRes;
        screenWidth = sw;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(itemRes, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        String s = urlList.get(position).getImgUrl().getMd();
        //viewHolder.imageView.setImageUrl(App.analysis.get(position).getUrl(), App.instance.getImageLoader());
//        Picasso.with(mContext).load(urlList.get(position).getImgUrl().getMd()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError() {
//                Picasso.with(mContext).load(urlList.get(position).getImgUrl().getMd()).into(viewHolder.imageView);
//            }
//        });

        Picasso.with(mContext).load(urlList.get(position).getImgUrl().getMd()).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);

//        Picasso.with(mContext)
//                .load(urlList.get(position).getImgUrl().getMd())
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        // loaded bitmap is here (bitmap)
//
////                        int width = (int)screenWidth;
////                        int bw = bitmap.getWidth();
////                        int bh = bitmap.getHeight();
////                        float sx = width / bw;
////                        int height = bh*(int)sx;
////                        viewHolder.imageView.getLayoutParams().width = width;
////                        viewHolder.imageView.getLayoutParams().height = height;
//
////                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
//                        viewHolder.imageView.setImageBitmap(bitmap);
////                        bitmap.recycle();
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });

        viewHolder.imageView.setOnHorizontalScrollEnableListener(new CustomImageVIew.OnHorizontalScrollEnableListener() {
            @Override
            public void onHorizontalScrollEnable(boolean enable) {
                mHorizontalEnableListener.onHorizontalScrollEnable(enable);
            }
        });

        viewHolder.imageView.setOnVerticalScrollEnableListener(new CustomImageVIew.OnVerticalScrollEnableListener() {
            @Override
            public void onVerticalScrollEnable(boolean enable) {
                mVerticalEnableListener.onVerticalScrollEnable(enable);
            }
        });

        container.addView(view);
        view.setTag(viewHolder);
        return view;

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
        public CustomImageVIew imageView;

        public ViewHolder(View view) {
            imageView = (CustomImageVIew) view.findViewById(R.id.img_property);
        }
    }

    private OnHorizontalScrollEnableListener mHorizontalEnableListener;
    public void setOnHorizontalScrollEnableListener(OnHorizontalScrollEnableListener listener)
    {
        mHorizontalEnableListener = listener;
    }

    public interface OnHorizontalScrollEnableListener{
        void onHorizontalScrollEnable(boolean enable);
    }


    private OnVerticalScrollEnableListener mVerticalEnableListener;
    public void setOnVerticalScrollEnableListener(OnVerticalScrollEnableListener listener)
    {
        mVerticalEnableListener = listener;
    }

    public interface OnVerticalScrollEnableListener{
        void onVerticalScrollEnable(boolean enable);
    }
}
