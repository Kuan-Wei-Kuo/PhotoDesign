package com.kuo.photodesign;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private final static int IMAGE_RESULT_CODE = 1;

    private boolean crop_mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!crop_mode) {
                    Intent openImage = new Intent();
                    openImage.setType("image/*");
                    openImage.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(openImage, IMAGE_RESULT_CODE);
                } else {
                    crop_mode = false;
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    DropPictureView mDropPictureView = (DropPictureView) findViewById(R.id.mDropPictureView);
                    mDropPictureView.setVisibility(View.GONE);

                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(mDropPictureView.getCropBitmap());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == IMAGE_RESULT_CODE) {
            Uri mUri = data.getData();
            ContentResolver mContentResolver = this.getContentResolver();
            try {
                DropPictureView mDropPictureView = (DropPictureView) findViewById(R.id.mDropPictureView);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);

                Bitmap sourceBitmap = BitmapFactory.decodeStream(mContentResolver.openInputStream(mUri));

                int height = (int) ((float) mDropPictureView.getWidth()/sourceBitmap.getWidth() * sourceBitmap.getHeight());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                layoutParams.addRule(Gravity.CENTER);

                imageView.setVisibility(View.GONE);
                mDropPictureView.setLayoutParams(layoutParams);
                mDropPictureView.setImageBitmap(sourceBitmap);
                mDropPictureView.setVisibility(View.VISIBLE);
                mDropPictureView.resterCropFrame();

                crop_mode = true;
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }



}
