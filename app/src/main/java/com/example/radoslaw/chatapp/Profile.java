package com.example.radoslaw.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView profileImage = findViewById(R.id.profile_image);      //TODO: Stworzyć metodę aby pobrać i wyświetlić dane w tym activity, wywalić to z onCreate (Brzydkie)
        Glide.with(this).load(Database.getUserPhoto()).into(profileImage);
        setTextViews();
    }


    private void setTextViews(){
        TextView profile_name = findViewById(R.id.profile_name);
        TextView profile_surname = findViewById(R.id.profile_surname);
        TextView profile_birth = findViewById(R.id.profile_birth);
        TextView profile_mail = findViewById(R.id.profile_mail);
        TextView profile_sex = findViewById(R.id.profile_sex);
        TextView profile_phone_number = findViewById(R.id.profile_phone_number);
        TextView profile_accomodation = findViewById(R.id.profile_accomodation);
        TextView profile_nationality = findViewById(R.id.profile_nationality);
        TextView profile_organizer = findViewById(R.id.profile_organizer);
        TextView group_name = findViewById(R.id.profile_groupid);

        profile_name.setText(String.valueOf(Database.getUserProfile().get("name")));
        profile_surname.setText(String.valueOf(Database.getUserProfile().get("surname")));
        profile_birth.setText(String.valueOf(Database.getUserProfile().get("birth")));
        profile_mail.setText(String.valueOf(Database.getUserProfile().get("mail")));
        profile_sex.setText(String.valueOf(Database.getUserProfile().get("sex")));
        profile_phone_number.setText(String.valueOf(Database.getUserProfile().get("surname")));
        profile_organizer.setText(String.valueOf(Database.getGroupProfile().get("organizer")));
        profile_accomodation.setText(String.valueOf(Database.getUserProfile().get("accomodation")));
        profile_nationality.setText(String.valueOf(Database.getUserProfile().get("nationality")));
        group_name.setText(String.valueOf(Database.getGroupProfile().get("name")));

    }


}



