package com.example.radoslaw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Radoslaw on 2017-11-06.
 */

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView profile_name = findViewById(R.id.currentGroup);
        profile_name.setText(String.valueOf(Database.getGroupProfile().get("name")));
    }

    public void joinButtonOnClick(View v ){
        EditText et = findViewById(R.id.eGroupKey);
        TextView profile_name = findViewById(R.id.currentGroup);
        Database.joinGroup(String.valueOf(et.getText()));
        profile_name.setText(String.valueOf(Database.getGroupProfile().get("name")));
        refresh();
    }

    public void leaveButtonOnClick(View v ){
        Database.leaveGroup();
        TextView profile_name = findViewById(R.id.currentGroup);
        profile_name.setText("Brak");
        refresh();
    }
    private void refresh(){
        View v = getWindow().getDecorView().findViewById(android.R.id.content);
        //Database.getGroupData();
        v.invalidate();
    }

}


