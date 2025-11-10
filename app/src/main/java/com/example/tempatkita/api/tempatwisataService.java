package com.example.tempatkita.api;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class TempatWisataService {

    // Tambah data tempat wisata baru
    public static void addTempatWisata(Firestore db, String id, String nama, String lokasi, String deskripsi, String gambarUrl) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("nama", nama);
        data.put("lokasi", lokasi);
        data.put("deskripsi", deskripsi);
        data.put("gambarUrl", gambarUrl);
        data.put("createdAt", FieldValue.serverTimestamp());

        ApiFuture<WriteResult> result = db.collection("tempatwisata").document(id).set(data);
        System.out.println("Tempat wisata added at: " + result.get().getUpdateTime());
    }

    // Hapus tempat wisata berdasarkan ID
    public static void deleteTempatWisata(Firestore db, String id) throws Exception {
        ApiFuture<WriteResult> writeResult = db.collection("tempatwisata").document(id).delete();
        System.out.println("Tempat wisata deleted at: " + writeResult.get().getUpdateTime());
    }

    // Baca (read) data tempat wisata
    public static void getTempatWisata(Firestore db, String id) throws Exception {
        DocumentReference docRef = db.collection("tempatwisata").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            System.out.println("Tempat Wisata Data: " + document.getData());
        } else {
            System.out.println("No such tempat wisata!");
        }
    }

    // Update tempat wisata
    public static void updateTempatWisata(Firestore db, String id, Map<String, Object> updates) throws Exception {
        ApiFuture<WriteResult> writeResult = db.collection("tempatwisata").document(id).update(updates);
        System.out.println("Tempat wisata updated at: " + writeResult.get().getUpdateTime());
    }
}
