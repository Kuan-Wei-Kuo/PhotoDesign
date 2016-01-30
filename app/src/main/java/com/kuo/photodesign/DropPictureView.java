package com.kuo.photodesign;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/*
 * Created by User on 2016/1/20.
 */
public class DropPictureView extends ImageView {

    private static final int MODE_STOP = -1;
    private static final int MODE_INIT = 0;
    private static final int MODE_MOVE = 1;
    private static final int MODE_ZOOM = 2;

    private Paint paint, pointPaint;
    private float left, top, right, bottom, radius, mDropWidth;
    private int mode;

    public DropPictureView(Context context) {
        super(context);

        onCreatePaint();
    }

    public DropPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onCreatePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mode == MODE_INIT) {
            left = getWidth() / 4;
            top =  getHeight() / 4;
            right = getWidth() - left;
            bottom = top + (right - left);
            radius = (right - left) / 10;
            mDropWidth = right - left;
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
                    if (event.getX() > right && event.getY() < top && (right - left) <= (getWidth() - 30)) {
                        if(left >= 0 && top >= 0 && right <= getWidth() && bottom <= getHeight()) {
                            left -= 5;
                            top -= 5;
                            right += 5;
                            bottom += 5;
                            invalidate();
                        }
                    } else if(event.getX() < right && event.getY() > top && (right - left) >= (getWidth() / 6)) {
                        left += 5;
                        top += 5;
                        right -= 5;
                        bottom -= 5;
                        invalidate();
                    }
                }

                mDropWidth = right - left;

                break;
        }

        return true;
    }

    private void onCreatePaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        mode = MODE_INIT;
    }
}
