package com.example.MySweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class SecondPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_second);

        Intent intent = getIntent();
        String timeMsg = intent.getStringExtra("timeMsg");
        String winMsg = intent.getStringExtra("outcomeMsg");
        String encMsg = intent.getStringExtra("encMsg");

        TextView textView1 = (TextView) findViewById(R.id.time);
        textView1.setText(timeMsg);
        TextView textView2 = (TextView) findViewById(R.id.winL);
        textView2.setText(winMsg);
        TextView textView3 = (TextView) findViewById(R.id.words);
        textView3.setText(encMsg);
    }

    public void playAgain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}