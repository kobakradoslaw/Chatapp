package com.example.radoslaw.chatapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Radoslaw on 2017-10-20.
 */

public class Database { //TODO: Pobierać informacje raz, zapisywać do zmiennych i odwoływać sie do zmiennych.

    private static final String TAG = "Baza danych TEST" ;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static Map<String,Object> userProfile = new HashMap<>();
    private static Map<String,Object> groupProfile = new HashMap<>();

    private static String userDocumentID;
    private static String groupDocumentID;
    public static GeoPoint loc;

    //Zmienne opisujące dane z rejestracji
    private static String userDisplayName;
    private static String userEmail;
    private static String useruid;
    private static Uri userPhoto;



    public static void testPushUser(){
        userProfile.put("name","Radosław");
        userProfile.put("surname","JJAJAJAJ");
        userProfile.put("sex","Male");
        userProfile.put("birth",1995);
        userProfile.put("nationality","Polish");
        userProfile.put("phoneNumber",694177999);
        userProfile.put("mail","radek90gg@gmail.com");
    }

    public static void pushUser(){
        testPushUser();
        db.collection("users").add(userProfile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    public static void testPushGroup(){
        String documentname = "scoiatael";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Whatever");
        data.put("organizer", "Scoia Inc.");
        data.put("userkey", "sanki123");
        data.put("guidekey","root");
        data.put("groupid",documentname);
        db.collection("groups").document(documentname).set(data, SetOptions.merge());
    }

    public static void assignUser(){
        db.collection("users")
                .whereEqualTo("mail",userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userDocumentID = document.getId();
                                userProfile.putAll(document.getData());
                                assignUserUID();
                                getlocation(loc);
                                getGroupData();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public static void getGroupData(){
        db.collection("groups").whereEqualTo("name",userProfile.get("groupname"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                groupDocumentID = document.getId();
                                groupProfile.putAll(document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public static void joinGroup(String key){ //TODO: pobieranie danych z grupy po czym??
        if(key.endsWith("guide")) {
            db.collection("groups")
                    .whereEqualTo("guidekey", key)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    groupDocumentID = document.getId();
                                    groupProfile.putAll(document.getData());

                                }
                                sendGroupToUserProfile();
                                Map<String, Object> data = new HashMap<>();
                                data.put("name",userProfile.get("name"));
                                data.put("surname",userProfile.get("surname"));
                                data.put("mail",userProfile.get("mail"));
                            db.document("groups/"+groupDocumentID+"/guides/"+useruid)
                                    .set(data,SetOptions.merge());
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
        } else {
            db.collection("groups")
                    .whereEqualTo("userkey", key)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    groupDocumentID = document.getId();
                                    groupProfile.putAll(document.getData());

                                }
                                sendGroupToUserProfile();
                                Map<String, Object> data = new HashMap<>();
                                data.put("name",userProfile.get("name"));
                                data.put("surname",userProfile.get("surname"));
                                data.put("mail",userProfile.get("mail"));
                                db.document("groups/"+groupDocumentID+"/users/"+useruid)
                                        .set(data,SetOptions.merge());
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
        }
    }

    public static void leaveGroup(){
        db.collection("groups").document(String.valueOf(userProfile.get("groupid"))).collection("users").document(useruid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        Map<String,Object> updates = new HashMap<>();
        updates.put("groupid", FieldValue.delete());
        updates.put("groupname", FieldValue.delete());
        db.collection("users").document(userDocumentID).update(updates);
    }

    public static void assignUserUID(){
        Map<String, Object> data = new HashMap<>();
        data.put("uid", useruid);
        db.collection("users").document(userDocumentID).set(data, SetOptions.merge());
    }

    public static void sendGroupToUserProfile(){
        Map<String, Object> data = new HashMap<>();
        data.put("groupid", groupDocumentID);
        data.put("groupname", groupProfile.get("name"));
        db.collection("users").document(userDocumentID).set(data, SetOptions.merge());
    }


    public static void getlocation(GeoPoint a){
        Map<String, GeoPoint> data = new HashMap<>();
        data.put("location", a);
        Log.d(TAG, "location =====>" + a);
        db.collection("users").document(userDocumentID).set(data, SetOptions.merge());
    }

    /**
     * Metoda do zapisania danych użytkownika(podanych przy rejestracji) do zmiennych
      */
    public static void updateCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userDisplayName = user.getDisplayName();
            userEmail = user.getEmail();
            useruid = user.getUid();
            userPhoto = user.getPhotoUrl();
        }
    }


    public static String getUserDisplayName() {
        return userDisplayName;
    }

    public static void setUserDisplayName(String userDisplayName) {
        Database.userDisplayName = userDisplayName;
    }

    public static Map<String, Object> getUserProfile() {
        return userProfile;
    }

    public static Map<String, Object> getGroupProfile() {
        return groupProfile;
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
