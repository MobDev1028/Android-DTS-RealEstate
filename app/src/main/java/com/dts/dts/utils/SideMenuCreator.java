package com.dts.dts.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dts.dts.R;
import com.dts.dts.activities.GeneralInfoActivity;
import com.dts.dts.fragments.SupportFragment;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Android Dev E5550 on 12/9/2016.
 */

public class SideMenuCreator implements NavigationView.OnNavigationItemSelectedListener {
    private static final long ANIMATION_DURATION = 180;
    private final NavigationView mNavView;
    private final View mainView;
    private Activity mContext;
    //private DrawerLayout mDrawerLayout;
    private SideMenuToggle mMenuToggle;
    private int sideMenuWidth;
    private Intent pendingIntent;
    private Fragment pendingFragment;

    private boolean isOpen = false;
    private static final float BLUR_RADIUS = 25f;

    public OnMenuClickListener mClickListener;

    public SideMenuCreator(final Activity context, View view) {
        mContext = context;
        mNavView = (NavigationView) view.findViewById(R.id.nv_drawer_items);
        final ImageView bg = (ImageView) view.findViewById(R.id.bg_image);
        bg.setImageResource(R.drawable.bg_splash);




        bg.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(context)
                        .radius(25)
                        .sampling(3)
                        .color(Color.argb(180, 0, 0, 0))
                        .async()
                        .capture(bg)
                        .into(bg);
            }
        });


        //mDrawerLayout = (DrawerLayout) view.findViewById(R.id.dl_side_menu);
        mainView = view.findViewById(R.id.main_layout);
        sideMenuWidth = mContext.getResources().getDimensionPixelSize(R.dimen.sideMenuWidth);

        mainView.setBackgroundColor(ContextCompat.getColor(context, R.color.windowBackground));
        initializeSideMenu();
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//    public Bitmap blur(Bitmap image) {
//        if (null == image) return null;
//
//        Bitmap outputBitmap = Bitmap.createBitmap(image);
//        final RenderScript renderScript = RenderScript.create(mContext);
//        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
//        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
//
////Intrinsic Gausian blur filter
//        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
//        theIntrinsic.setRadius(BLUR_RADIUS);
//        theIntrinsic.setInput(tmpIn);
//        theIntrinsic.forEach(tmpOut);
//        tmpOut.copyTo(outputBitmap);
//        return outputBitmap;
//    }

    private void initializeSideMenu() {
        mNavView.setNavigationItemSelectedListener(this);
        mNavView.setItemIconTintList(null);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        //Checking if the item is in checked state or not, if not make it in checked state
//        if (menuItem.isChecked())
//            menuItem.setChecked(false);
//        else
//            menuItem.setChecked(true);

        //Closing drawer on item click
        if (mMenuToggle.isDrawerOpen()) {
            mMenuToggle.toggleDrawer();
//            menuItem.setChecked(true);
            mClickListener.onMenuClicked(menuItem.getItemId());
        }

        //Check to see which item was being clicked and perform appropriate action

        return false;
    }

    public void addFragment(Fragment fragment, String tag){
        FragmentTransaction ft = mContext.getFragmentManager().beginTransaction();
        ft.add(R.id.ly_property, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    public SideMenuToggle getMenuToggle() {
        mMenuToggle = new SideMenuToggle() {

            @Override
            public void toggleDrawer() {
                if (isOpen) {
                /*if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);*/
                    animateRight();
                } else {
                    //mDrawerLayout.openDrawer(Gravity.RIGHT);
                    animateLeft();
                }
                isOpen = !isOpen;
            }

            @Override
            public boolean isDrawerOpen() {
                //return mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
                return isOpen;
            }
        };
        return mMenuToggle;
    }

    private void animateRight() {
        mainView.animate().setDuration(ANIMATION_DURATION).translationX(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainView.setX(0);
//                if (pendingIntent != null) {
//                    mContext.startActivity(pendingIntent);
//                    pendingIntent = null;
//                }


            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void animateLeft() {
        mainView.animate().setDuration(ANIMATION_DURATION).translationX(-sideMenuWidth).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainView.setX(-sideMenuWidth);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setOnMenuClickListener(OnMenuClickListener listener){

        mClickListener = listener;
    }

    public interface SideMenuToggle {
        void toggleDrawer();

        boolean isDrawerOpen();
    }

    public interface OnMenuClickListener{
        void onMenuClicked(int resId);
    }
}
