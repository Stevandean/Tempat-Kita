package com.example.tempatkita.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import android.content.Context;
import java.io.InputStream;

public class FirebaseInit {

    private static Firestore firestoreInstance;

    // Inisialisasi Firebase (panggil sekali saja di MainActivity misalnya)
    public static void initialize(Context context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            try {
                // Pastikan file google-services.json sudah ada di folder app/
                FirebaseOptions options = FirebaseOptions.fromResource(context);
                FirebaseApp.initializeApp(context, options);
                System.out.println("Firebase initialized successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Firebase initialization failed: " + e.getMessage());
            }
        }
    }

    // Dapatkan instance Firestore
    public static Firestore getFirestore() {
        if (firestoreInstance == null) {
            firestoreInstance = FirestoreClient.getFirestore();
        }
        return firestoreInstance;
    }
}
