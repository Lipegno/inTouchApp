package com.example.intouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

// TODO: If you are using appcompat
//import android.support.annotation.Nullable;
//import android.support.v7.widget.AppCompatRadioButton;

public class MyRadioButton extends AppCompatRadioButton {

    private View view;
    private AppCompatButton positiveButton;
    private AppCompatButton neutralButton;
    private AppCompatButton negativeButton;
    private Context context;

    public int backgroundColor = Color.WHITE;

    public MyRadioButton(Context context) {
        super(context);
        init(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setColorSchemes(ArrayList<String> colors){

        GradientDrawable positiveShape = (GradientDrawable) context.getDrawable(R.drawable.circle_button);
        positiveShape.setColor(Color.parseColor(colors.get(0)));

        GradientDrawable neutralShape = (GradientDrawable) context.getDrawable(R.drawable.circle_button);
        neutralShape.setColor(Color.parseColor(colors.get(1)));

        GradientDrawable negativeShape = (GradientDrawable) context.getDrawable(R.drawable.circle_button);
        negativeShape.setColor(Color.parseColor(colors.get(2)));

        positiveButton.setBackground(positiveShape);
        neutralButton.setBackground(neutralShape);
        negativeButton.setBackground(negativeShape);

        redrawLayout();
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.aligned_circles, null);
        positiveButton = view.findViewById(R.id.positiveButton);
        neutralButton = view.findViewById(R.id.neutralButton);
        negativeButton = view.findViewById(R.id.negativeButton);
        this.context = context;
    }

    private void redrawLayout() {
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
        view.setDrawingCacheEnabled(false);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
