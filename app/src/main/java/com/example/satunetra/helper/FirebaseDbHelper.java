package com.example.satunetra.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDbHelper {
    FirebaseDatabase database;
    DatabaseReference reference;

    public FirebaseDbHelper(){
        database = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getReference() {
        return reference;
    }
}
