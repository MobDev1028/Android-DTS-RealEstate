package com.dts.dts.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class CustomImageVIew extends ImageView {
    Matrix matrix;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;
    float maxScale = 4f;
    float[] mw;

    int viewWidth, viewHeight;
    static final int CLICK = 0;
    float saveScale = 1f;
    protected float origWidth, origHeight;
    int oldMeasuredWidth, oldMeasuredHeight;

    ScaleGestureDetector mScaleDetector;

    Context context;

    public CustomImageVIew(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public CustomImageVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    boolean doubleClick = false;
    boolean singleClick = false;
    Handler doubleHandler = new Handler();

    Runnable runnable  = new Runnable() {
        @Override
        public void run() {
            doubleClick = false;
            if (singleClick == true)
            {
                singleClick = false;
            }
        }
    };
    PointF m_curr;


    public boolean onDoubleTap(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();
        float[] matrixValues = {};
        Matrix inverseMatrix = new Matrix();
        final float[] doubleTapImagePoint = new float[2];

        matrix.reset();
        matrix.set(getImageMatrix());
        matrix.getValues(matrixValues);
        matrix.invert(inverseMatrix);
        doubleTapImagePoint[0] = x;
        doubleTapImagePoint[1] = y;
        inverseMatrix.mapPoints(doubleTapImagePoint);
        final float scale = matrixValues[Matrix.MSCALE_X];
        final float targetScale = saveScale;
        final float finalX;
        final float finalY;
        // assumption: if targetScale is less than 1, we're zooming out to fit the screen
        if (targetScale < 1.0f) {
            // scaling the image to fit the screen, we want the resulting image to be centred. We need to take
            // into account the shift that is applied to zoom on the tapped point, easiest way is to reuse
            // the transformation matrix.
            RectF imageBounds = new RectF(getDrawable().getBounds());
            // set up matrix for target
            matrix.reset();
            matrix.postTranslate(-doubleTapImagePoint[0], -doubleTapImagePoint[1]);
            matrix.postScale(targetScale, targetScale);
            matrix.mapRect(imageBounds);

            finalX = ((getWidth() - imageBounds.width()) / 2.0f) - imageBounds.left;
            finalY = ((getHeight() - imageBounds.height()) / 2.0f) - imageBounds.top;
        }
        // else zoom around the double-tap point
        else {
            finalX = x;
            finalY = y;
        }

        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final long startTime = System.currentTimeMillis();
        final long duration = 800;
        post(new Runnable() {
            @Override
            public void run() {
                float t = (float) (System.currentTimeMillis() - startTime) / duration;
                t = t > 1.0f ? 1.0f : t;
                float interpolatedRatio = interpolator.getInterpolation(t);
                float tempScale = scale + interpolatedRatio * (targetScale - scale);
                float tempX = x + interpolatedRatio * (finalX - x);
                float tempY = y + interpolatedRatio * (finalY - y);
                matrix.reset();
                // translate initialPoint to 0,0 before applying zoom
                matrix.postTranslate(-doubleTapImagePoint[0], -doubleTapImagePoint[1]);
                // zoom
                matrix.postScale(tempScale, tempScale);
                // translate back to equivalent point
                matrix.postTranslate(tempX, tempY);
                setImageMatrix(matrix);
                if (t < 1f) {
                    post(this);
                }
            }
        });

        return false;
    }


    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix = new Matrix();
        mw = new float[9];
        setImageMatrix(matrix);
//        setScaleType(ScaleType.MATRIX);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                mScaleDetector.onTouchEvent(event);
                PointF curr = new PointF(event.getX(), event.getY());
                m_curr = curr;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (singleClick == true)
                        {
                            singleClick = false;
                            doubleClick = true;
                            final float orignScale = saveScale;
                            if (saveScale > 1) {
                                saveScale = 1;
                                Animation scale = new ScaleAnimation(orignScale, 1, orignScale, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                scale.setDuration(300);
                                scale.setFillAfter(true);
                                scale.setFillEnabled(true);
                                startAnimation(scale);

                                matrix.postScale(1/orignScale, 1/orignScale,
                                        viewWidth/2, viewHeight/2);
                                fixTrans();
                            }
                            else {
                                saveScale = 3;
                                Animation scale = new ScaleAnimation(1, saveScale,
                                        1, saveScale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                                scale.setDuration(300);
                                scale.setFillAfter(true);
                                scale.setFillEnabled(true);
                                startAnimation(scale);

                                matrix.postScale(saveScale, saveScale,
                                        event.getX(), event.getY());

                                fixTrans();
                            }
                            break;
                        }

                        singleClick = true;
                        doubleHandler.postDelayed(runnable, 300);

                        last.set(curr);
                        start.set(last);
                        mode = DRAG;

                        if (mVerticalEnableListener != null)
                            mVerticalEnableListener.onVerticalScrollEnable(false);
                        if (mHorizontalEnableListener != null)
                            mHorizontalEnableListener.onHorizontalScrollEnable(false);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(saveScale == 1) {
                            mode = NONE;
                            if (mVerticalEnableListener != null)
                                mVerticalEnableListener.onVerticalScrollEnable(true);
                            if (mHorizontalEnableListener != null)
                                mHorizontalEnableListener.onHorizontalScrollEnable(true);
                        }
                        if (mode == DRAG && doubleClick == false) {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float fixTransX = getFixDragTrans(deltaX, viewWidth,
                                    origWidth * saveScale);
                            float fixTransY = getFixDragTrans(deltaY, viewHeight,
                                    origHeight * saveScale);


                            Drawable drawable = getDrawable();
                            if (drawable == null)
                                break;

                            int w = drawable.getIntrinsicWidth();
                            int h = drawable.getIntrinsicHeight();

                            matrix.postTranslate(fixTransX, fixTransY);

                            float[] values = new float[9];
                            matrix.getValues(values);



                            Log.e("Matrix Value", values.toString());
                            float x = values[2];
                            float y = values[5];
                            float scale = values[0];
                            float width = w * scale;
                            float height = h * scale;

                            if (deltaX < 0) {
                                if (mHorizontalEnableListener != null) {
                                    if (width + x - viewWidth <= 30 * scale) {
                                        mHorizontalEnableListener.onHorizontalScrollEnable(true);
                                    } else {
                                        mHorizontalEnableListener.onHorizontalScrollEnable(false);
                                    }
                                }
                            }
                            else
                            {
                                if (mHorizontalEnableListener != null) {
                                    if (x >= -30 * scale) {
                                        mHorizontalEnableListener.onHorizontalScrollEnable(true);
                                    } else {
                                        mHorizontalEnableListener.onHorizontalScrollEnable(false);
                                    }
                                }
                            }

                            if (deltaY <= 0)
                            {
                                if (mVerticalEnableListener != null)
                                {
                                    if (height + y - viewHeight <= 20 * scale)
                                        mVerticalEnableListener.onVerticalScrollEnable(true);
                                    else
                                        mVerticalEnableListener.onVerticalScrollEnable(false);
                                }
                            }
                            else
                            {
                                if (mVerticalEnableListener != null)
                                {
                                    if (y > -20 * scale)
                                        mVerticalEnableListener.onVerticalScrollEnable(true);
                                    else
                                        mVerticalEnableListener.onVerticalScrollEnable(false);
                                }
                            }
                            fixTrans();
                            last.set(curr.x, curr.y);


                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (doubleClick == false) {
                            mode = NONE;

                            if (mVerticalEnableListener != null)
                                mVerticalEnableListener.onVerticalScrollEnable(true);

                            int xDiff = (int) Math.abs(curr.x - start.x);
                            int yDiff = (int) Math.abs(curr.y - start.y);
                            if (xDiff < CLICK && yDiff < CLICK)
                                performClick();
                        }

                        break;

//                    case MotionEvent.ACTION_POINTER_UP:
//                        mode = NONE;
//                        break;
                }

                setImageMatrix(matrix);
                invalidate();
                return true; // indicate event was handled
            }

        });
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }

            if (origWidth * saveScale <= viewWidth
                    || origHeight * saveScale <= viewHeight)
                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
                        viewHeight / 2);
            else
                matrix.postScale(mScaleFactor, mScaleFactor,
                        detector.getFocusX(), detector.getFocusY());

            fixTrans();
            return true;
        }
    }

    void fixTrans() {
        matrix.getValues(mw);
        float transX = mw[Matrix.MTRANS_X];
        float transY = mw[Matrix.MTRANS_Y];

        float a = origWidth * saveScale;
        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
        float fixTransY = getFixTrans(transY, viewHeight, origHeight
                * saveScale);

        if (fixTransX != 0 || fixTransY != 0) {
            matrix.postTranslate(fixTransX, fixTransY);


        }
        scrollEnable();
    }

    void scrollEnable()
    {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0)
            return;
        int bmWidth = drawable.getIntrinsicWidth();
        int bmHeight = drawable.getIntrinsicHeight();

        Log.d("aaaaaa", String.format("%d", bmWidth));
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
                || viewWidth == 0 || viewHeight == 0)
            return;
        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {
            // Fit to screen.
            float scale;

            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0
                    || drawable.getIntrinsicHeight() == 0)
                return;
            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();

            Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
//            scale = Math.max(scaleX, scaleY);
            matrix.setScale(scaleX, scaleY);

            float redundantYSpace = 0;
            float redundantXSpace = 0;


            redundantYSpace = viewHeight - (scaleY * bmHeight) ;
            redundantXSpace = viewWidth - (scaleX * bmWidth);
            redundantYSpace /= 2;
            redundantXSpace /= 2;


            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;
            origHeight = viewHeight - 2 * redundantYSpace;
            setImageMatrix(matrix);
        }
        fixTrans();
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

