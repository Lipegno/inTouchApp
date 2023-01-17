package com.example.intouch;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.media.audiofx.DynamicsProcessing;
import android.util.Config;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class ScreenManager {

    private static volatile ScreenManager instance;
    private static final String TAG = "Screen Manager";
    private static int right_last_color;
    private static int left_last_color;
    public static ScreenManager getInstance() {
        if (instance == null) {
            synchronized (ScreenManager.class) {
                if (instance == null) {
                    instance = new ScreenManager();
                }
            }
        }
        return instance;
    }

    public int getColorFromMood(String mood,Context c){
        Log.i(TAG,"Mood "+mood);
        if(mood.equals("positive")){
            return c.getResources().getColor(R.color.good);
        }else if(mood.equals("neutral")){
            return c.getResources().getColor(R.color.neutral);
        }else if(mood.equals("negative")){
            return c.getResources().getColor(R.color.bad);
        }

        return Color.parseColor("#FFFFFF");
    }

    @SuppressLint("ResourceType")
    public void UpdatePartnerColor(String mood, Context context) {

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");
            int color = getColorFromMood(mood,context);
            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image_v2);

            VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
            gradient_r.setColorFilter(right_last_color,PorterDuff.Mode.SRC_IN);

            //gradient_r.setTint(color);
            //}else{
            VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
            gradient_l.setColorFilter(color,PorterDuff.Mode.SRC_IN);

            //}
            left_last_color = color;
            Log.i(TAG,"left changed color : "+left_last_color+"   right current color: "+right_last_color);

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceType")
    public void UpdateColor(int color, Context context) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");

            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image_v2);

            //if(side.equals("right")){
                VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
                gradient_r.setColorFilter(color,PorterDuff.Mode.SRC_IN);
                //gradient_r.setTint(color);
            //}else{
                VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
                gradient_l.setColorFilter(left_last_color,PorterDuff.Mode.SRC_IN);
            //}
            right_last_color = color;
            Log.i(TAG,"right changed color : "+right_last_color+"   left current color: "+left_last_color);


            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initWallpapers(Context c){
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(c);

        int color = 0;
        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(c, R.drawable.wallpaper_image_v2);
        VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
        gradient_r.setColorFilter(color,PorterDuff.Mode.SRC_IN);
        VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
        gradient_l.setColorFilter(color,PorterDuff.Mode.SRC_IN);
        //right_last_color = color;
        Log.i(TAG,"right changed color : "+right_last_color+"   left current color: "+left_last_color);


        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }
}
