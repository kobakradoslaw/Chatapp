package com.example.radoslaw.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Radoslaw on 2017-11-07.
 */

public class Find extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
    }

    public void mapbuttonOnClick(View v ){
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }
    public void searchButtonOnClick(View v ){
        EditText et = findViewById(R.id.efindemail);
        findUserbyEmail(String.valueOf(et.getText()), v);
    }

    public  void findUserbyEmail(String mail, View v ){


        Database.db.collection("groups").document(String.valueOf(Database.getUserProfile().get("groupid"))).collection("users")
                .whereEqualTo("mail",mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FindActivity", document.getId());
                                Log.d("FindActivity", document.getId() + " => " + document.getData());
                                Database.setFindUserUid(String.valueOf(document.getData().get("uid")));
                                Log.d("FindActivityTHATHHTTHA", String.valueOf(document.getData().get("uid")));
                            }

                            Database.db.collection("users")
                                    .whereEqualTo("uid",Database.getFindUserUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            Log.d("FindActivity", String.valueOf(Database.getFindUserUid()));
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    Log.d("FindActivity", document.getId() + " => " + document.getData());
                                                    Database.setFindUserProfile(document.getData());
                                                    setTest();
                                                }
                                            } else {
                                                Log.d("FindActivity", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.d("FindActivity", "Error getting documents: ", task.getException());

                        }
                    }
                });
        Toast.makeText(this,String.valueOf(Database.getFindUserProfile().get("name")) , Toast.LENGTH_SHORT).show();
    }
    public void setTest(){
        TextView tfindname = findViewById(R.id.tfindname);
        TextView tfindsurname = findViewById(R.id.tfindsurname);
        TextView tfindstatus = findViewById(R.id.tfindstatus);

        tfindname.setText(String.valueOf(Database.getFindUserProfile().get("name")));

        tfindsurname.setText(String.valueOf(Database.getFindUserProfile().get("surname")));
        TextView tfindphone = findViewById(R.id.tfindphone);
        tfindphone.setText(String.valueOf(Database.getFindUserProfile().get("phoneNumber")));


        TextView tfindLatitude = findViewById(R.id.tfindLatitude);
        TextView tfindLongitude = findViewById(R.id.tfindLongitude);
        GeoPoint point = (GeoPoint) Database.getFindUserProfile().get("location");
        tfindLatitude.setText(String.valueOf(point.getLatitude()));
        tfindLongitude.setText(String.valueOf(point.getLongitude()));
        tfindstatus.setText("Znaleziono");
        View v = getWindow().getDecorView().findViewById(android.R.id.content);
        v.invalidate();
    }

}
