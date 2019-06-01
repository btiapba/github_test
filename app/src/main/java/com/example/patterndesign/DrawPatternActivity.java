package com.example.patterndesign;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class DrawPatternActivity extends AppCompatActivity {

    private static final String TAG = "MyDebug";
    private static final String MODULE = "CreatePatternActivity";

    public static ArrayList<Line> lines = new ArrayList<>();

    private Bitmap bitmap;
    private Canvas canvas_rect;
    private Paint paint_draw, paint_cancel;
    private ImageView image_test;

    private int screen_width;
    private int screen_height;
    private float screen_density;

    private static int count = 0;
    private boolean first_run = true;

    private int image_test_width;
    private int image_test_height;
    private ArrayList<Rect> rectList = new ArrayList<>();//opencv Rect

    // Used to load the 'native-lib' library on application startup.
    //static {
    //    System.loadLibrary("native-lib");
    //}

    private final String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pattern);

        //if(!isPermissionGranted()){
        //    return;
        //}

        image_test = findViewById(R.id.image_test);
        Button button_draw = findViewById(R.id.button_draw);
        Button button_cancel_draw = findViewById(R.id.button_cancel_draw);

        get_screen_size();
        init_canvas();

        paint_draw = new Paint();
        paint_draw.setStyle(Paint.Style.STROKE);
        paint_draw.setColor(Color.RED);
        paint_draw.setStrokeWidth(3);

        paint_cancel = new Paint();
        paint_cancel.setStyle(Paint.Style.STROKE);
        paint_cancel.setColor(Color.TRANSPARENT);
        paint_cancel.setStrokeWidth(3);

        draw_pattern();


        button_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, MODULE + " draw: ");
                //paint_draw.setColor(Color.RED);
                Rect rect = new Rect();
                rect.left = 10 + 50 * count;
                rect.top = 10 + 50 * count;
                rect.right = 100 + 50 * count;
                rect.bottom = 100 + 50 * count;
                rectList.add(rect);

                //canvas_rect.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint_draw);
                //canvas_rect.drawLine(100, 10, 100, 300, paint_draw);
                canvas_rect.drawCircle(300, 300, 50, paint_draw);


                image_test.invalidate();
                count++;
            }
        });

        button_cancel_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, MODULE + " cancel draw: ");
                //canvas_rect.drawColor(Color.TRANSPARENT);
                canvas_rect.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                //count = 0;
                //rectList.clear();

                rectList.remove(rectList.size()-1);
                for(Rect rect: rectList){
                    Log.d(TAG, MODULE + " Left/Top/Right/Bottom: " + rect.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom);
                    canvas_rect.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint_draw);
                }

                /*
                //paint_rect.setColor(Color.TRANSPARENT);
                Paint paint_cancel = new Paint();
                paint_cancel.setStyle(Paint.Style.STROKE);

                paint_cancel.setARGB(0, 255, 255, 255);
                paint_cancel.setAlpha(0);
                paint_cancel.setStrokeWidth(3);
                canvas_rect.drawRect(10, 10, 100, 100, paint_cancel);
                */
                //image_test.setImageBitmap(bitmap);
                image_test.invalidate();
            }
        });
    }

    private void draw_pattern(){
        for(Line line: lines){
            canvas_rect.drawLine(line.x_from, line.y_from, line.x_to, line.y_to, paint_draw);
        }
    }

    private void init_canvas(){
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        canvas_rect = new Canvas(bitmap);
        canvas_rect.drawColor(Color.TRANSPARENT);
        image_test.setImageBitmap(bitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    Uri sdCardUri = data.getData();
                    Log.d(TAG, MODULE + " Uri: " + sdCardUri);
                }
                break;
        }
    }






    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    private void get_screen_size(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen_height = metrics.heightPixels;
        screen_width = metrics.widthPixels;
        screen_density = metrics.density;
        Log.d(TAG, MODULE + " screen width/height/density: " + screen_width + ", " + screen_height + ", " + screen_density);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {


            if(first_run){


                /*
                image_test_width = image_test.getWidth();
                image_test_height = image_test.getHeight();

                bitmap = Bitmap.createBitmap(image_test_width, image_test_height, Bitmap.Config.ARGB_8888);
                bitmap.setHasAlpha(true);
                canvas_rect = new Canvas(bitmap);
                canvas_rect.drawColor(Color.TRANSPARENT);
                image_test.setImageBitmap(bitmap);
                first_run = false;

                */
            }

        }
    }

    public boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions_needed = new ArrayList<>();

            for(String permission: permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, MODULE + " Permission is granted: " + permission);
                } else{
                    permissions_needed.add(permission);
                }
            }
            if (!permissions_needed.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissions_needed.toArray(new String[permissions_needed.size()]), 222);
                return false;
            }else {
                return true;
            }
        }else { //permission is automatically granted on sdk<23 upon installation
            //Log.d(TAG, MODULE + " Permission is granted");
            return true;
        }
    }


}
