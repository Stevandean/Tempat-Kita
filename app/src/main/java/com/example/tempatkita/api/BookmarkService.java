package com.example.tempatkita.api;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class BookmarkService {

    // Tambahkan tempat wisata ke bookmark user
    public static void addBookmark(Firestore db, String userId, String tempatId) throws Exception {
        Map<String, Object> bookmarkData = new HashMap<>();
        bookmarkData.put("tempatId", tempatId);
        bookmarkData.put("createdAt", FieldValue.serverTimestamp());

        ApiFuture<WriteResult> result = db.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(tempatId)
                .set(bookmarkData);

        System.out.println("Bookmark added at: " + result.get().getUpdateTime());
    }

    // Hapus bookmark
    public static void deleteBookmark(Firestore db, String userId, String tempatId) throws Exception {
        ApiFuture<WriteResult> writeResult = db.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(tempatId)
                .delete();

        System.out.println("Bookmark deleted at: " + writeResult.get().getUpdateTime());
    }

    // Baca semua bookmark milik user
    public static void getBookmarks(Firestore db, String userId) throws Exception {
        CollectionReference bookmarksRef = db.collection("users").document(userId).collection("bookmarks");
        ApiFuture<QuerySnapshot> future = bookmarksRef.get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            System.out.println("Bookmark: " + doc.getData());
        }
    }
}
