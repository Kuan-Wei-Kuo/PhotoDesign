package com.kuo.photodesign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/*
 * Created by User on 2016/1/20.
 */
public class CropPictureView extends ImageView {

    private static final int MODE_STOP = -1;
    private static final int MODE_INIT = 0;
    private static final int MODE_MOVE = 1;
    private static final int MODE_ZOOM = 2;

    private Paint paint, pointPaint;
    private float left, top, right, bottom, radius, mDropWidth;
    private int mode;

    private float maxLimitWdith;

    public CropPictureView(Context context) {
        super(context);

        onCreatePaint();
    }

    public CropPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreatePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mode == MODE_INIT) {
            left = getWidth() > getHeight() ? getWidth() / 4 : 0;
            top =  getWidth() > getHeight() ? 0 : getHeight() / 4;
            right = getWidth() > getHeight() ? getHeight() + left : getWidth() - left;
            bottom = getWidth() > getHeight() ? getHeight() : top + (right - left);
            radius = (right - left) / 10;
            mDropWidth = right - left;

            maxLimitWdith = getWidth() > getHeight() ? getHeight() : getWidth();
        }

        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawCircle(right, top, radius, pointPaint);
    }

    float dX, dY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float x = event.getX();
                float y = event.getY();

                dX = left - x;
                dY = top - y;

                if(x >= (left + radius) && x <= (right - radius) && y >= (top + radius) && y <= (bottom - radius)) {
                    mode = MODE_MOVE;
                } else if(x >= (right - radius) && x <= (right + radius) && y >= (top - radius) && y <= (top + radius)) {
                    mode = MODE_ZOOM;
                } else {
                    mode = MODE_STOP;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if(mode == MODE_MOVE) {

                    left = event.getX() + dX;
                    top = event.getY() + dY;
                    left = left <= 0 ? 0 : left;
                    top = top <= 0 ? 0 : top;

                    right = left + mDropWidth;
                    bottom = top + mDropWidth;

                    if(right >= getWidth()) {
                        right = getWidth();
                        left = right - mDropWidth;
                    }

                    if(bottom >= getHeight()) {
                        bottom = getHeight();
                        top = bottom - mDropWidth;
                    }

                    invalidate();
                } else if(mode == MODE_ZOOM) {
                    if (event.getX() > right && event.getY() < top && (right - left) <= maxLimitWdith && (bottom - top) <= maxLimitWdith) {

                        if((right - left) != maxLimitWdith) {
                            left = left <= 0 ? 0 : right >= getWidth() ? left - 10 : left - 5;
                            top = top <= 0 ? 0 : bottom >= getHeight() ? top - 10 : top - 5;
                            right = right >= getWidth() ? getWidth() : left <= 0 ? right + 10 : right + 5;
                            bottom = bottom >= getHeight() ? getHeight() : top <= 0 ? bottom + 10 : bottom + 5;
                        }

                        left = left <= 0 ? 0 : left;
                        top = top <= 0 ? 0 : top;
                        right = (right - left) >= maxLimitWdith ? right >= getWidth() ? getWidth() : (maxLimitWdith + left) : right;
                        bottom = (bottom - top) >= maxLimitWdith ? bottom >= getHeight() ? getHeight() : (maxLimitWdith + top)  : bottom;

                        invalidate();
                    } else if(event.getX() < right && event.getY() > top && (right - left) >= (getWidth() / 6)) {
                        left += 5;
                        top += 5;
                        right -= 5;
                        bottom -= 5;
                        invalidate();
                    }
                }
                mDropWidth = (right - left) >= maxLimitWdith ? maxLimitWdith : right - left;
                break;
        }

        return true;
    }

    private void onCreatePaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        mode = MODE_INIT;
    }

    private Bitmap getScreenBitmap(Bitmap src, int width, int height) {
        return Bitmap.createScaledBitmap(src, width, height, true);
    }

    public void resterCropFrame() {
        mode = MODE_INIT;
        invalidate();
    }

    public void rotateImage() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() , matrix, true);

        int height = (int) ((float) getWidth()/bmp.getWidth() * bmp.getHeight());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(Gravity.CENTER);
        setLayoutParams(layoutParams);
        setImageBitmap(bmp);
    }

    public Bitmap getCropBitmap() {
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        return Bitmap.createBitmap(getScreenBitmap(bitmap, getWidth(), getHeight()), (int) left, (int) top, (int) mDropWidth, (int) mDropWidth);
    }
}
