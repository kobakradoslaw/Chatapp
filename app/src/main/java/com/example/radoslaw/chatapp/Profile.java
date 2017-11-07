package com.example.radoslaw.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView profileImage = findViewById(R.id.profile_image);      //TODO: Stworzyć metodę aby pobrać i wyświetlić dane w tym activity, wywalić to z onCreate (Brzydkie)
        Glide.with(this).load(Database.getUserPhoto()).into(profileImage);
    }
}
