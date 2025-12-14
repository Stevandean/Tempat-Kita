package com.example.tempatkita.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tempatkita.R;
import com.example.tempatkita.adapter.CommentAdapter;
import com.example.tempatkita.model.Comment;
import com.example.tempatkita.model.Wisata;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class DetailActivity extends AppCompatActivity {

    // ===== DETAIL VIEW =====
    ImageView image;
    TextView nama, lokasi, deskripsi;
    Button btnBack, btnMap;

    // ===== COMMENT =====
    RecyclerView rvComment;
    EditText edtComment;
    CommentAdapter adapter;
    List<Comment> list = new ArrayList<>();

    FirebaseFirestore db;
    String wisataId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_detail);

        // ===== INIT DETAIL =====
        image = findViewById(R.id.imageViewDetail);
        nama = findViewById(R.id.textViewName);
        lokasi = findViewById(R.id.textViewLocation);
        deskripsi = findViewById(R.id.textViewDescription);
        btnBack = findViewById(R.id.btnBack);
        btnMap = findViewById(R.id.btnLihatPeta);

        // ===== GET DATA =====
        Wisata w = (Wisata) getIntent().getSerializableExtra("data");
        wisataId = getIntent().getStringExtra("wisata");

        if (w == null || wisataId == null) {
            Toast.makeText(this, "Data wisata tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== SET DETAIL =====
        nama.setText(w.getNama());
        lokasi.setText(w.getLokasi());
        deskripsi.setText(w.getDeskripsi());

        Glide.with(this)
                .load(w.getGambarUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(image);

        btnBack.setOnClickListener(v -> finish());

        btnMap.setOnClickListener(v -> {
            String url = "https://www.google.com/maps/search/" +
                    w.getNama().replace(" ", "+");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        // ===== COMMENT =====
        db = FirebaseFirestore.getInstance();

        rvComment = findViewById(R.id.rvComment);
        edtComment = findViewById(R.id.edtComment);

        rvComment.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(this, list);
        rvComment.setAdapter(adapter);

        findViewById(R.id.btnSend).setOnClickListener(v -> sendComment());

        loadCommentRealtime();
    }

    // ===== LOAD COMMENT REALTIME =====
    private void loadCommentRealtime() {
        db.collection("wisata")
                .document(wisataId)
                .collection("comments")
                .orderBy("createdAt")
                .addSnapshotListener((snap, e) -> {
                    if (snap == null) return;

                    list.clear();
                    for (var d : snap) {
                        Comment c = d.toObject(Comment.class);
                        c.setId(d.getId());
                        list.add(c);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    // ===== SEND COMMENT / REPLY =====
    private void sendComment() {
        String text = edtComment.getText().toString().trim();
        if (text.isEmpty()) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Login untuk komentar", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", uid);
        data.put("userName", "User");
        data.put("content", text);
        data.put("createdAt", System.currentTimeMillis());
        data.put("parentId", adapter.getReplyingToId()); // 🔥 INI PENTING

        db.collection("wisata")
                .document(wisataId)
                .collection("comments")
                .add(data)
                .addOnSuccessListener(a -> {
                    edtComment.setText("");
                    adapter.clearReply(); // reset mode reply
                });
    }
}
