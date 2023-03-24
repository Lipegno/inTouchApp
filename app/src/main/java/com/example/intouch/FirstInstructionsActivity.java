package com.example.intouch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.intouch.dbmodels.User;

public class FirstInstructionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button first_step_btn;
    private Button second_step_btn;
    private Button third_step_btn;
    private Button fourth_step_btn;

    private LinearLayout first_view;
    private LinearLayout second_view;
    private LinearLayout third_view;
    private LinearLayout fourth_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_instructions);

        first_step_btn = findViewById(R.id.first_step_btn);
        second_step_btn = findViewById(R.id.second_step_btn);
        third_step_btn = findViewById(R.id.third_step_btn);
        fourth_step_btn = findViewById(R.id.fourh_step_btn);
        first_step_btn.setOnClickListener(this);
        second_step_btn.setOnClickListener(this);
        third_step_btn.setOnClickListener(this);
        fourth_step_btn.setOnClickListener(this);

        first_view = findViewById(R.id.fist_step);
        second_view = findViewById(R.id.second_step);
        third_view = findViewById(R.id.third_step);
        fourth_view = findViewById(R.id.fourth_step);

        first_view.setVisibility(View.VISIBLE);
        second_view.setVisibility(View.GONE);
        third_view.setVisibility(View.GONE);
        fourth_view.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.first_step_btn){
            first_view.setVisibility(View.GONE);
            second_view.setVisibility(View.VISIBLE);
            third_view.setVisibility(View.GONE);
            fourth_view.setVisibility(View.GONE);
        }else if(view.getId()==R.id.second_step_btn){
            first_view.setVisibility(View.GONE);
            second_view.setVisibility(View.GONE);
            third_view.setVisibility(View.VISIBLE);
            fourth_view.setVisibility(View.GONE);
        }else if(view.getId()==R.id.third_step_btn){
            first_view.setVisibility(View.GONE);
            second_view.setVisibility(View.GONE);
            third_view.setVisibility(View.GONE);
            fourth_view.setVisibility(View.VISIBLE);
        }else if(view.getId()==R.id.fourh_step_btn){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}