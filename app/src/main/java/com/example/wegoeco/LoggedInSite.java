package com.example.wegoeco;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LoggedInSite extends AppCompatActivity implements View.OnClickListener {

    private Button btn_weekly, btn_mountly, btn_yestoday;
    private TextView text_top_score_value, text_top_rank, text_mid_energi_use, text_mid_energi_use_score, text_mid_kwh_pr_km, text_mid_kwh_pr_km_score, text_mid_acceleration_score, text_mid_persona, text_mid_score_value;
    private ImageView persona_image, btn_info, btn_menu;
    private String[] dataArray;

    //ARRAY VALUES:
    //0: top score
    //1: top persona.
    //2: mid score.
    //3: mid persona.
    //4: mid kwh use.
    //5: mid energi score.
    //6: mid kwh pr km.
    //7:  kwh pr km score.
    //8: acceleration score.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_site);
        getSupportActionBar().hide();
        dataArray = new String[9];

        //Assigning variables
        AssignVariabels();

        //MISSING A LOADING WHILE GETTING DATA!

        //MISSING GETTING THE DATA FUNCTION

        //MISSING REMOVING LOADING AFTER DATA HAVE GOTTEN

        //Setting up on click listeners
        btn_yestoday.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_mountly.setOnClickListener(this);
        btn_weekly.setOnClickListener(this);

        //Setting the startup to be the yestoday
        ChangeDataArray(1);




    }

    private void AssignVariabels() {

        //Buttons assigned
        btn_yestoday = findViewById(R.id.btn_yestoday);
        btn_weekly = findViewById(R.id.btn_weekly);
        btn_mountly = findViewById(R.id.btn_mounthly);

        //Textview assigned
        text_top_rank = findViewById(R.id.top_text_rank);
        text_top_score_value = findViewById(R.id.top_text_score_value);
        text_mid_persona = findViewById(R.id.mid_text_persona_value);
        text_mid_acceleration_score = findViewById(R.id.mid_text_acceleration_value);
        text_mid_energi_use_score = findViewById(R.id.mid_text_energiuse_value_score);
        text_mid_energi_use = findViewById(R.id.mid_text_energiuse_value);
        text_mid_kwh_pr_km = findViewById(R.id.mid_text_kmprkwh_value);
        text_mid_kwh_pr_km_score = findViewById(R.id.mid_text_kmprkwh_value_score);
        text_mid_score_value = findViewById(R.id.mid_text_score_value);

        //Imageviews
        persona_image = findViewById(R.id.top_image);
        btn_info = findViewById(R.id.btn_info);
        btn_menu = findViewById(R.id.btn_menu);

    }


    @Override
    public void onClick(View v) {
        //In this function its posible to make new function that change image and text views to the correct state.
        switch (v.getId()){
            case R.id.btn_mounthly:
                System.out.println("btn mounthly");
                ChangeDataArray(3);
                break;
            case R.id.btn_weekly:
                System.out.println("btn weekly");
                ChangeDataArray(2);
                break;
            case R.id.btn_yestoday:
                System.out.println("btn yestoday");
                ChangeDataArray(1);
                break;
            case R.id.btn_info:
                System.out.println("btn info");
                break;
            case R.id.btn_menu:
                System.out.println("menu");
                break;
            default:
                System.out.println("Error");
        }
    }

    private void ChangeDataArray(int i) {
        //i = 1: yestoday
        //i = 2: weekly
        //i = 3: mounthly


        UpdateTextviewWithData();
    }

    private void UpdateTextviewWithData() {

        text_top_score_value.setText(dataArray[0]);
        text_top_rank.setText(dataArray[1]);
        text_mid_score_value.setText(dataArray[2]);
        text_mid_persona.setText(dataArray[3]);
        text_mid_energi_use.setText(dataArray[4]);
        text_mid_energi_use_score.setText(dataArray[5]);
        text_mid_kwh_pr_km.setText(dataArray[6]);
        text_mid_kwh_pr_km_score.setText(dataArray[7]);
        text_mid_acceleration_score.setText(dataArray[8]);
    }


}