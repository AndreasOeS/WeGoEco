package com.example.wegoeco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothActivity2 extends AppCompatActivity {
    public TextView textView;
    public Button startKnap;
    public Datastream2 datastream2 = new Datastream2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth2);
        textView = findViewById(R.id.text);
        startKnap = findViewById(R.id.btn_start);

        startKnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datastream2.readData();
                shift();
                startKnap.setEnabled(false);
            }
        });

    }

    public void shift(){
        Thread thread = new Thread(){
            public void run(){
                while(true){
                    if (datastream2.slut) {
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}