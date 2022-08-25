package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class ColorSchemePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_scheme);
    }

    // write click listener for radio group

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioColorS1:
                if (checked){
                    // Save color scheme 1
                    break;
                }
            case R.id.radioColorS2:
                if (checked){
                    // Save color scheme 2
                    break;
                }
            case R.id.radioColorCustomize:
                if(checked) {
                    // Customization
                    break;
                }
            default:
                // Save color scheme 1
                break;

        }
    }

}