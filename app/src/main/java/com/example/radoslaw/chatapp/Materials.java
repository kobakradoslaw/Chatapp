package com.example.radoslaw.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Materials extends AppCompatActivity {
    private static final String TAG = "Materials" ;
    private static final int READ_REQUEST_CODE = 42;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    ArrayList al = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials);
        //Log.d(TAG, String.valueOf(Database.getItemMap().keySet()));

        for(String name: Database.getItemMap().keySet()){
            al.add(name);
            Log.d(TAG, name);
            Log.d(TAG, String.valueOf(Database.getItemMap().get(name)));
            Log.d(TAG, String.valueOf(((Map)Database.getItemMap().get(name)).get("uid")));
            //Uri a = (Uri.from String.valueOf(((Map)Database.getItemMap().get(name)).get("uri")));
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.fragment_material,al);
        ListView listView = (ListView) findViewById(R.id.material_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView)view).getText().toString();
                Log.d(TAG, String.valueOf(((Map)Database.getItemMap().get(item)).get("uri")));
                Database.downloadFromUri(String.valueOf(((Map)Database.getItemMap().get(item)).get("uri")),String.valueOf(((Map)Database.getItemMap().get(item)).get("filename")),getApplicationContext());
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
            }
        });

    }


    public void pick(View v ) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.


        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("name", uri.getLastPathSegment())
                        .build();

                StorageReference ref = storageRef.child("files/"+Database.getUserProfile().get("groupid")+"/"+uri.getLastPathSegment());
                UploadTask uploadTask = ref.putFile(uri,metadata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Log.i(TAG, "Uri po wysłaniu: " + taskSnapshot.getDownloadUrl().toString());
                        Database.sendItemInfoToDatabase(taskSnapshot.getMetadata().getName(),String.valueOf(taskSnapshot.getDownloadUrl()));
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

                Log.i(TAG, "Uri: " + uri.toString());
            }
        }
    }

}
