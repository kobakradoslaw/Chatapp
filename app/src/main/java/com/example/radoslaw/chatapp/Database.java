package com.example.radoslaw.chatapp;

import android.net.Uri;
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

public class Database { //TODO: Pobierać informacje raz, zapisywać do zmiennych i odwoływać sie do zmiennych.

    private static final String TAG = "Baza danych TEST" ;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Map<String,Integer> licz = new HashMap<>();


    private static String userDisplayName;
    private static String userEmail;
    private static Uri userPhoto;

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

    /**
     * Metoda do zapisania danych użytkownika(podanych przy rejestracji) do zmiennych
      */
    public static void updateCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userDisplayName = user.getDisplayName();
            userEmail = user.getEmail();
            userPhoto = user.getPhotoUrl();
        }
    }


    public static String getUserDisplayName() {
        return userDisplayName;
    }

    public static void setUserDisplayName(String userDisplayName) {
        Database.userDisplayName = userDisplayName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        Database.userEmail = userEmail;
    }

    public static Uri getUserPhoto() {
        return userPhoto;
    }

    public static void setUserPhoto(Uri userPhoto) {
        Database.userPhoto = userPhoto;
    }
}
