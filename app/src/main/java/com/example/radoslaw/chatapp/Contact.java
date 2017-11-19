package com.example.radoslaw.chatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    public void embassyMapButtonOnClick(View v) {
        startActivity(new Intent(getApplicationContext(), MapEmbassy.class));
    }

    public void embassyMessageButtonOnClick(View v) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage((String.valueOf(Database.getGroupProfile().get("embassyn"))),null,((String.valueOf(Database.getUserProfile().get("name")))+" "+(String.valueOf(Database.getUserProfile().get("surname")))+(String.valueOf(Database.loc))),null,null);
    }

    public void embassyPhoneButtonOnClick(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+String.valueOf(Database.getGroupProfile().get("embassyn"))));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void organizerMapButtonOnClick(View v) {
        startActivity(new Intent(getApplicationContext(), MapsOrganizer.class));
    }

    public void organizerMessageButtonOnClick(View v) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage((String.valueOf(Database.getGroupProfile().get("organizern"))),null,((String.valueOf(Database.getUserProfile().get("name")))+" "+(String.valueOf(Database.getUserProfile().get("surname")))+(String.valueOf(Database.loc))),null,null);
    }

    public void organizerPhoneButtonOnClick(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+String.valueOf(Database.getGroupProfile().get("organizern"))));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void rezidentPhoneButtonOnClick(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+String.valueOf(Database.getGroupProfile().get("rezidentn"))));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void rezidentMessageButtonOnClick(View v) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage((String.valueOf(Database.getGroupProfile().get("rezidentn"))),null,((String.valueOf(Database.getUserProfile().get("name")))+" "+(String.valueOf(Database.getUserProfile().get("surname")))+(String.valueOf(Database.loc))),null,null);
    }

    public void guidePhoneButtonOnClick(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        Log.d("guide button value", String.valueOf(Database.getGuideProfile().get("phoneNumber")));
        callIntent.setData(Uri.parse("tel:"+String.valueOf(Database.getGuideProfile().get("phoneNumber"))));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }
    public void guideMessageButtonOnClick(View v) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage((String.valueOf(Database.getGuideProfile().get("phoneNumber"))),null,((String.valueOf(Database.getUserProfile().get("name")))+" "+(String.valueOf(Database.getUserProfile().get("surname")))+(String.valueOf(Database.loc))),null,null);
    }

}
//TODO: Dodać obsługę przycisuku SOS oraz wyświetlanie miniaturek przy postaciach