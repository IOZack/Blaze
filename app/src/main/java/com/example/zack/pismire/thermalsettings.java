package com.example.zack.pismire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class thermalsettings extends Activity {

    Button savechanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermalsettings);
        savechanges = (Button)findViewById(R.id.savechanges);
        savechanges.setOnClickListener(gomain_thermalimaging);
    }

    View.OnClickListener gomain_thermalimaging = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent();
            myIntent.setClass(thermalsettings.this, MainActivity.class);
            startActivity(myIntent);
        }
    };

}
