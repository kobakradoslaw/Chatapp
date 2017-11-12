package com.example.radoslaw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Radoslaw on 2017-11-07.
 */

public class Find extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
    }

    public void buttonOnClick(View v ){  //TODO: zmienić nazwę metody na bardziej opisową
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }
    public void searchButtonOnClick(View v ){
        EditText et = findViewById(R.id.efindemail);

    }

}
