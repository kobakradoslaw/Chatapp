package com.example.radoslaw.chatapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Radoslaw on 2017-10-20.
 */

public class Database {

    private static final String TAG = "Baza danych TEST" ;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

   public static Map<String,Integer> licz = new HashMap<>();


    public static void get(){
        licz.put("jeden",1);
        licz.put("dwa",2);
        licz.put("trzy",3);

        db.collection("testowa_kolekcja").add(licz).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }
    public static void get1(){
        licz.put("jeden",1);
        licz.put("dwa",2);
        licz.put("trzy",3);

        db.collection("testowa_kolekcja").document("Liczydło").set(licz).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID:");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }
    static public String[]  GetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();
            String[] details = {name,email,uid};
            return details;
        }
        return null;
    }

}
