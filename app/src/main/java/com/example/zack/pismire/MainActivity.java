package com.example.zack.pismire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button farmer;
    private Button fighter;
    private Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        farmer = (Button)findViewById(R.id.farmer);
        farmer.setOnClickListener(open_thermalimaging);
        fighter = (Button)findViewById(R.id.firefighter);
        fighter.setOnClickListener(open_fighterthermal);
        settings = (Button)findViewById(R.id.settingbtn);
        settings.setOnClickListener(settings_thermalimaging);
    }

    View.OnClickListener open_thermalimaging = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent();
            myIntent.setClass(MainActivity.this, thermalimage.class);
            startActivity(myIntent);
        }
    };

    View.OnClickListener open_fighterthermal = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent();
            myIntent.setClass(MainActivity.this, maxhot.class);
            startActivity(myIntent);
        }
    };

    View.OnClickListener settings_thermalimaging = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent();
            myIntent.setClass(MainActivity.this, thermalsettings.class);
            startActivity(myIntent);
        }
    };

}
