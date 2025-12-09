package com.example.tempatkita.data.firestore;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.tempatkita.model.Wisata;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WisataRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String COLLECTION = "wisata";

    // ================= GET LIST =================
    public interface OnListListener {
        void onSuccess(QuerySnapshot data);
        void onError(String error);
    }

    public void getAllWisata(OnListListener listener) {
        db.collection(COLLECTION).get()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // ================= ADD ===================
    public interface OnActionListener {
        void onSuccess();
        void onError(String error);
    }

    public void addWisata(Wisata w, OnActionListener listener){
        Map<String,Object> map = new HashMap<>();
        map.put("nama", w.getNama());
        map.put("lokasi", w.getLokasi());
        map.put("deskripsi", w.getDeskripsi());
        map.put("images", w.getImages());
        map.put("rating", w.getRating());

        db.collection(COLLECTION).add(map)
                .addOnSuccessListener(doc -> listener.onSuccess())
                .addOnFailureListener(e->listener.onError(e.getMessage()));
    }

    // ================= UPDATE =================
    public void updateWisata(String id, Wisata w, OnActionListener listener){
        Map<String,Object> map = new HashMap<>();
        map.put("nama", w.getNama());
        map.put("lokasi", w.getLokasi());
        map.put("deskripsi", w.getDeskripsi());
        map.put("images", w.getImages());
        map.put("rating", w.getRating());

        db.collection(COLLECTION).document(id).update(map)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e->listener.onError(e.getMessage()));
    }

    // ================= DELETE =================
    public void deleteWisata(String id, OnActionListener listener){
        db.collection(COLLECTION).document(id).delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e->listener.onError(e.getMessage()));
    }
}
