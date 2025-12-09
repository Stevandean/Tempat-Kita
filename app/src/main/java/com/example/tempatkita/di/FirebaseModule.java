package com.example.tempatkita.di;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseModule {

    private static FirebaseFirestore firestoreInstance;

    public static FirebaseFirestore provideFirestore() {
        if (firestoreInstance == null) {
            firestoreInstance = FirebaseFirestore.getInstance();
        }
        return firestoreInstance;
    }
}
