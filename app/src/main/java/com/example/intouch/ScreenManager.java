package com.example.intouch;

import static android.content.Context.MODE_PRIVATE;

import static com.example.intouch.InTouchWidget.WIDGET_MY_MOOD_CHANGE;
import static com.example.intouch.InTouchWidget.WIDGET_PARTNER_MOOD_CHANGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Config;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenManager {

    private static volatile ScreenManager instance;
    private static final String TAG = "Screen Manager";
    private static int right_last_color;
    private static int left_last_color;
    public static final String MY_PREFERENCE = "InTouch";

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

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(Context c){
        Log.i(TAG,"Storage State: "+Environment.getExternalStorageState());
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + c.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    private void storeImage(Bitmap image, Context c) {
        File pictureFile = getOutputMediaFile(c);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
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

    public void initWallpapersFirstStartup(Context context) {

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        try {
            Log.i(TAG,"First startup of the wallpapers!!");
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");
            SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
            String partner_mood =prefs.getString(WIDGET_PARTNER_MOOD_CHANGE,"nomood");
            String my_mood = prefs.getString(WIDGET_MY_MOOD_CHANGE,"nomood");
            int colorLeft=0;
            int colorRight=0;
            if(partner_mood.equals("nomood")) {
                colorLeft = Color.parseColor("#FFDDDDDD");
            }else{
                colorLeft = getColorFromMood(partner_mood,context);
            }
            if(my_mood.equals("nomood")) {
                colorRight = Color.parseColor("#FFCCCCCC");
            }else{
                colorRight = getColorFromMood(my_mood,context);
            }

            left_last_color = colorLeft;
            right_last_color = colorRight;

            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image_v2);
            VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
            gradient_r.setColorFilter(colorRight,PorterDuff.Mode.SRC_IN);

            VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
            gradient_l.setColorFilter(colorLeft,PorterDuff.Mode.SRC_IN);

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Toast.makeText(context,"SDK Válido",Toast.LENGTH_LONG);
            wallpaperManager.setBitmap(bitmap);
            //     wallpaperManager.setBitmap(bitmap);
           // }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage().toString());
        }
    }

    private int getPartnerLastColor(Context context){
        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        String partner_mood =prefs.getString(WIDGET_PARTNER_MOOD_CHANGE,"nomood");
        Log.i(TAG,"Getting partner last color: "+partner_mood);
        if(partner_mood.equals("nomood")) {
            return Color.parseColor("#FFDDDDDD");
        }else{
            return getColorFromMood(partner_mood,context);
        }
    }

    private int getMyLastColor(Context context){
        SharedPreferences prefs  = context.getSharedPreferences(MY_PREFERENCE,Context.MODE_PRIVATE);
        String my_mood =prefs.getString(WIDGET_MY_MOOD_CHANGE,"nomood");
        Log.i(TAG,"Getting my last color: "+my_mood);
        if(my_mood.equals("nomood")) {
            return Color.parseColor("#FFDDDDDD");
        }else{
            return getColorFromMood(my_mood,context);
        }
    }



    @SuppressLint({"ResourceType", "NewApi"})
    public void UpdatePartnerColor(String mood, Context context) {

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");
            int color = getColorFromMood(mood,context);
            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image_v2);

            VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
            gradient_r.setColorFilter(color,PorterDuff.Mode.SRC_IN);

            //gradient_r.setTint(color);
            //}else{
            VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
            gradient_l.setColorFilter(getMyLastColor(context),PorterDuff.Mode.SRC_IN);

            //}
            right_last_color = color;
            Log.i(TAG,"left changed color : "+left_last_color+"   right current color: "+right_last_color);

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Toast.makeText(context,"SDK Válido",Toast.LENGTH_LONG);
            wallpaperManager.setBitmap(bitmap);
           // }else{
            //    wallpaperManager.setBitmap(bitmap);
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"ResourceType", "NewApi"})
    public void UpdateColor(int color, Context context) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");

            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image_v2);

            //if(side.equals("right")){
                VectorDrawable gradient_r = (VectorDrawable) drawable.findDrawableByLayerId(R.id.right_triangle);
                gradient_r.setColorFilter(getPartnerLastColor(context),PorterDuff.Mode.SRC_IN);
                //gradient_r.setTint(color);
            //}else{
                VectorDrawable gradient_l = (VectorDrawable) drawable.findDrawableByLayerId(R.id.left_triangle);
                gradient_l.setColorFilter(color,PorterDuff.Mode.SRC_IN);
            //}
            left_last_color = color;
            Log.i(TAG,"right changed color : "+right_last_color+"   left current color: "+left_last_color);


            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
            //Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Toast.makeText(context,"SDK Válido",Toast.LENGTH_LONG);
            wallpaperManager.setBitmap(bitmap);

           //     wallpaperManager.setBitmap(bitmap);
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public void initWallpapers(Context c){
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
    }*/

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }
}
