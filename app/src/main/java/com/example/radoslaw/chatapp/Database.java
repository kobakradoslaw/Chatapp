package com.example.radoslaw.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Radoslaw on 2017-10-20.
 */

public class Database {

    private static final String TAG = "Baza danych TEST" ;
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageRef = storage.getReference();

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static Map<String,Object> userProfile = new HashMap<>();

    private static Map<String,Object> testUserProfile = new HashMap<>();

    private static Map<String,Object> findUserProfile = new HashMap<>();
    private static Map<String,Object> groupProfile = new HashMap<>();
    private static Map<String,Object> guideProfile = new HashMap<>();

    public static Map<String, Object> getItemMap() {
        return itemMap;
    }

    public static void setItemMap(Map<String, Object> itemMap) {
        Database.itemMap = itemMap;
    }

    private static Map<String,Object> itemMap = new HashMap<>();

    private static String findUserUid;
    private static String findGuideUid;

    private static String userDocumentID;
    private static String groupDocumentID;
    public static GeoPoint loc;

    //Zmienne opisujące dane z rejestracji
    private static String userDisplayName;
    private static String userEmail;
    private static String useruid;
    private static Uri userPhoto;



    public static void testPushUser(){
        testUserProfile.put("name","Ukasz");
        testUserProfile.put("surname","TestowyUkasz");
        testUserProfile.put("sex","Male");
        testUserProfile.put("birth",2222);
        testUserProfile.put("nationality","Polish");
        testUserProfile.put("phoneNumber",783120690);
        testUserProfile.put("mail","luk.kieron@gmail.pl");
    }

    public static void pushUser(){
        testPushUser();
        db.collection("users").add(testUserProfile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    public static Map<String,Object> testPushGroup(){
        String documentname = "Kopisto Group";
        GeoPoint embas = new GeoPoint(50.4424175,30.4838007);
        GeoPoint ombas = new GeoPoint(50.4745103,18.8243375);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Kopisto Group");
        data.put("organizer", "RainbowTours.");
        data.put("userkey", "radek");
        data.put("guidekey","toorguide");
        data.put("groupid",documentname);
        data.put("embassy","Kijów");
        data.put("embassyloc",embas);
        data.put("embassyn","694177996");
        data.put("organizer","RainbowTours");
        data.put("organizerloc",ombas);
        data.put("organizern","694177996");
        data.put("rezident","Mirosław");
        data.put("rezidentn","694177996");
        db.collection("groups").document(documentname).set(data, SetOptions.merge());
        return data;
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
                            }

                            assignUserUID();
                            getlocation(loc);
                            getGroupData();
                            findGuideProfile();
                            getItemInfoFromDatabase();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        //if(null==userDocumentID) Toast.makeText(this.getApplicationContext(),"Nie znaleziono profilu w bazie",Toast.LENGTH_LONG);
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

    public static void joinGroup(String key){
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
                                sendGroupToUserProfile(true);
                                Map<String, Object> data = new HashMap<>();
                                data.put("name",userProfile.get("name"));
                                data.put("surname",userProfile.get("surname"));
                                data.put("mail",userProfile.get("mail"));
                                data.put("uid",useruid);
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
                                sendGroupToUserProfile(false);
                                Map<String, Object> data = new HashMap<>();
                                data.put("name",userProfile.get("name"));
                                data.put("surname",userProfile.get("surname"));
                                data.put("mail",userProfile.get("mail"));
                                data.put("uid",useruid);
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
        updates.put("guide",FieldValue.delete());
        db.collection("users").document(userDocumentID).update(updates);
        userProfile.remove("groupid");
        userProfile.remove("groupname");
        userProfile.remove("guide");
        groupProfile.clear();
        Log.w(TAG, "______________________________________"+String.valueOf(groupProfile));
        //groupProfile.put("name","aaaaaa");
    }

    public static void assignUserUID(){
        Map<String, Object> data = new HashMap<>();
        data.put("uid", useruid);
        if(null!=userDocumentID)
        db.collection("users").document(userDocumentID).set(data, SetOptions.merge());
    }

    public static void sendGroupToUserProfile(boolean guide){
        Map<String, Object> data = new HashMap<>();
        data.put("groupid", groupDocumentID);
        data.put("groupname", groupProfile.get("name"));
        if(guide)data.put("guide",true);
        db.collection("users").document(userDocumentID).set(data, SetOptions.merge());
    }


    public static void getlocation(GeoPoint a){
        Map<String, GeoPoint> data = new HashMap<>();
        data.put("location", a);
        Log.d(TAG, "location =====>" + a);
        if(null!=userDocumentID)
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

    public static Map<String, Object> getGuideProfile() {
        return guideProfile;
    }

    public static void setGuideProfile(Map<String, Object> guideProfile) {
        Database.guideProfile = guideProfile;
    }

    public  static void findGuideProfile(){
        db.collection("groups").document(String.valueOf(getUserProfile().get("groupid"))).collection("guides")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                findGuideUid = (String.valueOf(document.getData().get("uid")));
                                Log.d("finduser", String.valueOf(document.getData()));
                            }

                            db.collection("users")
                                    .whereEqualTo("uid",findGuideUid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            Log.d("FindActivity", findGuideUid);
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    Log.d("FindActivity", document.getId() + " => " + document.getData());
                                                    setGuideProfile(document.getData());
                                                    Log.d("Current guide profile", String.valueOf(getGuideProfile()));
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
    }


    public static void sendItemInfoToDatabase(String name, String uri){
        Map<String, Object> data = new HashMap<>();
        data.put("uid",useruid);
        data.put("filename",name);
        data.put("uri",uri);
        db.document("groups/"+groupDocumentID+"/items/"+name).set(data);
}

    private static void getItemInfoFromDatabase(){
        db.collection("groups").document(String.valueOf(getUserProfile().get("groupid"))).collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                          for(DocumentSnapshot document : task.getResult()){
                              Log.d(TAG, document.getId() + " => " + document.getData());
                              itemMap.put(document.getId(),document.getData());
                          }
                            //Log.d(TAG, String.valueOf(((Map) itemMap.get("primary:microlog.txt")).get("Uri")));
                        } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    }
                });
    }

    public static void downloadFromUri(final String uri, String name, final Context context){
        File localfilea = null;
        localfilea = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),name);
        StorageReference itemRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        final File finalLocalfilea = localfilea;
        itemRef.getFile(localfilea).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Pobieranie","zakończone?");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Uri.fromFile(finalLocalfilea).toString().contains(".pdf")) {
                    intent.setDataAndType(Uri.fromFile(finalLocalfilea), "application/pdf");
                }
                intent.setDataAndType(Uri.fromFile(finalLocalfilea), "image/jpeg");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    public static void sendMessageToDatabase(String text){
        Map<String, Object> data = new HashMap<>();
        data.put("uid",useruid);
        data.put("text",text);
        data.put("time", Calendar.getInstance().getTime());
        db.collection("groups/"+groupDocumentID+"/messages/").add(data);

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


    public static String getFindUserUid() {
        return findUserUid;
    }

    public static void setFindUserUid(String findUserUid) {
        Database.findUserUid = findUserUid;
    }

    public static Map<String, Object> getFindUserProfile() {
        return findUserProfile;
    }

    public static void setFindUserProfile(Map<String, Object> findUserProfile) {
        Database.findUserProfile = findUserProfile;
    }

}
