package com.example.patterndesign;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ColorProcessingActivity extends AppCompatActivity {

    private static final String TAG = "MyDebug";
    private static final String MODULE = "ColorProcessingActivity";

    private ImageView image_replace_color;
    private Bitmap bitmap, pre_bitmap;
    private ArrayList<Line> lines = new ArrayList<>();

    private final String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_processing);

        Button button_replace_color = findViewById(R.id.button_replace_color);
        final EditText edit_color_from = findViewById(R.id.edit_color_from);
        final EditText edit_color_to = findViewById(R.id.edit_color_to);
        edit_color_from.setText("#000000");
        edit_color_to.setText("#ff0000");

        Button button_transparent_background = findViewById(R.id.button_transparent_background);
        Button button_gray_scale = findViewById(R.id.button_gray_scale);

        Button button_save_as_jpg = findViewById(R.id.button_save_as_jpg);
        Button button_save_as_png = findViewById(R.id.button_save_as_png);

        Button button_threshold = findViewById(R.id.button_threshold);
        final EditText edit_threshold = findViewById(R.id.edit_threshold);
        edit_threshold.setText("125");

        final ImageView image_color_processing = findViewById(R.id.image_color_processing);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.correct2);

        image_color_processing.setImageBitmap(bitmap);

        isPermissionGranted();
        button_replace_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = replaceColor(bitmap, Color.parseColor(edit_color_from.getText().toString()), Color.parseColor(edit_color_to.getText().toString()));
                image_color_processing.setImageBitmap(bitmap);
            }
        });
        button_transparent_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = replaceIntervalColor(bitmap, 230, 255, 230, 255, 230, 255, Color.argb(0,255,255, 255));
                image_color_processing.setImageBitmap(bitmap);
            }
        });

        button_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int threshold = Integer.parseInt(edit_threshold.getText().toString());
                Log.d(TAG, MODULE + " threshold: " + threshold);
                bitmap = thresholdColor(bitmap, threshold);
                image_color_processing.setImageBitmap(bitmap);
            }
        });

        button_gray_scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = grayScale(bitmap);
                image_color_processing.setImageBitmap(bitmap);
            }
        });

        button_save_as_jpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_path = Environment.getExternalStorageDirectory().toString() + "/" + "WorldHere";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
                String time_stamp = sdf.format(new Date());
                final String file_name = "image_" + time_stamp + ".jpg";
                File file = new File(file_path, file_name);

                try{
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        button_save_as_png.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_path = Environment.getExternalStorageDirectory().toString() + "/" + "WorldHere";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
                String time_stamp = sdf.format(new Date());
                final String file_name = "image_" + time_stamp + ".png";
                File file = new File(file_path, file_name);

                try{
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public Bitmap replaceIntervalColor(Bitmap bitmap,int redStart,int redEnd,int greenStart, int greenEnd,int blueStart, int blueEnd,int colorNew) {
        if(bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                int r = Color.red(pix[index]);
                int g = Color.green(pix[index]);
                int b = Color.blue(pix[index]);
                if ( ( (redStart <= r) && (r <= redEnd) ) && ( (greenStart <= g) && (g <= greenEnd) ) && ( (blueStart <= b) && (b <= blueEnd) ) ){
                    pix[index] = colorNew;
                }
            }
        }
        return Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap thresholdColor(Bitmap bitmap, int threshold) {
        if(bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                int r = Color.red(pix[index]);
                int g = Color.green(pix[index]);
                int b = Color.blue(pix[index]);

                int gray = (int) Math.round(0.299f * r + 0.587f * g + 0.114 * b);
                if ( gray > threshold ){
                    pix[index] = Color.parseColor("#ffffff");
                }else {
                    pix[index] = Color.parseColor("#000000");
                }
            }
        }
        return Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap grayScale(Bitmap bitmap) {
        if(bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                int r = Color.red(pix[index]);
                int g = Color.green(pix[index]);
                int b = Color.blue(pix[index]);

                int gray = (int) Math.round(0.299f * r + 0.587f * g + 0.114 * b);
                pix[index] = Color.argb(255, gray, gray, gray);
            }
        }
        return Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
    }

    private int random_integer(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public Bitmap replaceColor(Bitmap src,int fromColor, int toColor) {

        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int index = 0; index < pixels.length; index++) {
            pixels[index] = (pixels[index] == fromColor) ? toColor : pixels[index];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
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
