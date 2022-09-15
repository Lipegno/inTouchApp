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
import android.media.audiofx.DynamicsProcessing;
import android.util.Config;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class ScreenManager {

    private static volatile ScreenManager instance;

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

    @SuppressLint("ResourceType")
    public void UpdateColor(int color, Context context) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            String side = context.getSharedPreferences(MainActivity.MY_PREFERENCE, MODE_PRIVATE).getString("wallpaperSide","right");

            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.wallpaper_image);
            GradientDrawable gradient;

            if(side.equals("right")){
                gradient = (GradientDrawable) drawable.findDrawableByLayerId(R.id.right_rectangle);
            }else{
                gradient = (GradientDrawable) drawable.findDrawableByLayerId(R.id.left_rectangle);
            }
            gradient.setColor(color);

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

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }
}
