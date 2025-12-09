package com.example.tempatkita.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.tempatkita.di.FirebaseModule;

import java.util.HashMap;
import java.util.Map;

public class FirestoreRepository {

    private final FirebaseFirestore db;

    public FirestoreRepository() {
        db = FirebaseModule.provideFirestore();
    }

    public void addUser(String name, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(doc -> System.out.println("User Added: " + doc.getId()))
                .addOnFailureListener(e -> System.out.println("Error: " + e.getMessage()));
    }
}
