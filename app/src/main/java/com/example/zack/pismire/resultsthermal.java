package com.example.zack.pismire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class resultsthermal extends Activity {

    private double foundcels = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultsthermal);
        Intent myIntent = getIntent(); // gets the previously created intent
        foundcels = myIntent.getDoubleExtra("foundcels", 0);
        Toast.makeText(this, foundcels + " Celsius", Toast.LENGTH_LONG).show();

    }

}
