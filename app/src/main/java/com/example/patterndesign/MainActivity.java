package com.example.patterndesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyDebug";
    private static final String MODULE = "MainActivity";

    private ImageView image_replace_color;
    private Bitmap bitmap;
    private ArrayList<Line> lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edit_length_min = findViewById(R.id.edit_length_min);
        EditText edit_length_max = findViewById(R.id.edit_length_max);
        EditText edit_gap_min = findViewById(R.id.edit_gap_min);
        EditText edit_gap_max = findViewById(R.id.edit_gap_max);
        EditText edit_spacing_min = findViewById(R.id.edit_spacing_min);
        EditText edit_spacing_max = findViewById(R.id.edit_spacing_max);

        Button button_create_pattern = findViewById(R.id.button_create_pattern);
        Button button_draw_pattern = findViewById(R.id.button_draw_pattern);
        Button button_color_processing = findViewById(R.id.button_color_processing);


        button_create_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_pattern();
            }
        });
        button_draw_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DrawPatternActivity.class));
            }
        });

        button_color_processing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ColorProcessingActivity.class));
            }
        });

    }

    private void create_pattern(){

        int x_from = 0;
        int x_to = 0;


        while (x_from < 200) {

            int y_from = 0;
            int y_to = 0;

            while (y_from < 400) {

                int length = random_integer(1, 5);
                y_to = y_from + length;
                lines.add(new Line(x_from, y_from, x_to, y_to));

                int gap = random_integer(1, 3);
                y_from = y_to + gap;
            }
            int spacing = random_integer(1, 3);
            x_from = x_from + spacing;
            x_to = x_to + spacing;
            Log.d(TAG, MODULE + " x_from/x_to: " + x_from + ", " + x_to);
        }

        DrawPatternActivity.lines = lines;

        //int count = 0;
        //for(Line line: lines){
        //    Log.d(TAG, MODULE + " line(" + count +  ")" + ": " + line.x_from + ", " + line.y_from + ", " + line.x_to + ", " + line.y_to);
        //    count++;
        //}

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
            if(pixels[index] != fromColor){
                pixels[index] = Color.parseColor("#00000000");
                //Log.d(TAG, MODULE + " not match : (" + index);
            }

            pixels[index] = (pixels[index] == fromColor) ? toColor : pixels[index];


        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }



}
