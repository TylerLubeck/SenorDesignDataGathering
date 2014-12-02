package com.tylerlubeck.buildingmapper;


import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Canvas;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Spinner;

/**
 * Created by Hunter on 10/11/14.
 *
 * File var??
 */
public class FloorMapImage  extends Activity implements AdapterView.OnItemSelectedListener{
    private File file_path;
    private Context ctx;
    private int w,h,original_w,original_h;
    private int radius, pt_x, pt_y;
    private Bitmap unmarked;
    private ImageView image;
    String building;

    FloorMapImage(String building, int floorNum, ImageView _image, Context _ctx){
        this.building = building;
        image = _image;
        w = 1200;
        h = 800;
        radius = 10;
        ctx = _ctx;
        pt_x = -1;
        pt_y = -1;
        /*
        String file_path = building.toLowerCase() + String.valueOf(floorNum);
        image.setImageResource(ctx.getResources().getIdentifier(file_path , "drawable", ctx.getPackageName()));
        Bitmap Floorbitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        original_h = Floorbitmap.getHeight();
        original_w = Floorbitmap.getWidth();
        unmarked = Bitmap.createScaledBitmap(Floorbitmap, w, h, true);
        image.setImageBitmap(unmarked);
        */
        this.create_image(building, floorNum);
    }

    public void create_image(int floorNum) {
        this.create_image(this.building, floorNum);
    }

    public void create_image(String building, int floorNum) {
        String file_path = building.toLowerCase() + String.valueOf(floorNum);
        Log.d("BUILDINGMAPPER", "NEW FILE: " + file_path);
        this.image.setImageResource(this.ctx.getResources().getIdentifier(file_path , "drawable", ctx.getPackageName()));
        Bitmap Floorbitmap = ((BitmapDrawable)this.image.getDrawable()).getBitmap();
        this.original_h = Floorbitmap.getHeight();
        this.original_w = Floorbitmap.getWidth();
        this.unmarked = Bitmap.createScaledBitmap(Floorbitmap, this.w, this.h, true);
        this.image.setImageBitmap(this.unmarked);
    }

    public void create_and_draw_point(int floorNum, int x, int y) {
        this.create_image(floorNum);
        this.draw_point_noclear(x, y);
    }

    void draw_point(int x, int y){
        Log.d("DRAWING AT:",Integer.toString(x) + ":" + Integer.toString(y));
        Bitmap bmp = unmarked.copy(unmarked.getConfig(), true);
        if(x >= 0 && y >= 0 ) {
            float scaled_x = x * (float) w / (float) original_w;
            float scaled_y = y * (float) h / (float) original_h;

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            Canvas canvas = new Canvas(bmp);
            canvas.drawCircle(scaled_x, scaled_y, radius, paint);
        }
        image.setImageBitmap(bmp);
    }

    public String getBuilding() {
        return building;
    }

    void draw_point_noclear(int x, int y){
        Log.d("DRAWING AT:",Integer.toString(x) + ":" + Integer.toString(y));
        Bitmap bmp = ((BitmapDrawable)this.image.getDrawable()).getBitmap();
        if(x >= 0 && y >= 0 ) {
            float scaled_x = x * (float) w / (float) original_w;
            float scaled_y = y * (float) h / (float) original_h;
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            Canvas canvas = new Canvas(bmp);
            canvas.drawCircle(scaled_x, scaled_y, radius, paint);
        }
        this.image.setImageBitmap(bmp);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Location loc = (Location) adapterView.getItemAtPosition(i);
        draw_point(loc.x,loc.y);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("FLOOR", "NOTHING SELECTED");
        draw_point(-1,-1);
    }
}
