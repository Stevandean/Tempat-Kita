package com.example.tempatkita.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tempatkita.R;
import com.example.tempatkita.utils.BottomMenuHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddWisataActivity extends AppCompatActivity {

    private EditText edtNama, edtLokasi, edtKategori, edtDeskripsi;
    private ImageView imgPreview;
    private Button btnChoose, btnSimpan;

    private Uri imageUri;
    private String imageUrlLama = null;
    private String wisataId = null;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wisata);

        // ================= INIT VIEW =================
        edtNama = findViewById(R.id.edtNama);
        edtLokasi = findViewById(R.id.edtLokasi);
        edtKategori = findViewById(R.id.edtKategori);
        edtDeskripsi = findViewById(R.id.edtDeskripsi);
        imgPreview = findViewById(R.id.imgPreview);
        btnChoose = findViewById(R.id.btnChooseImage);
        btnSimpan = findViewById(R.id.btnSimpan);

        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);

        btnChoose.setOnClickListener(v -> pilihGambar());
        btnSimpan.setOnClickListener(v -> simpanData());

        // ================= CEK MODE EDIT =================
        wisataId = getIntent().getStringExtra("id");

        if (wisataId != null) {
            setTitle("Edit Wisata");
            btnSimpan.setText("Update Wisata");

            db.collection("wisata")
                    .document(wisataId)
                    .get()
                    .addOnSuccessListener(d -> {
                        if (d.exists()) {
                            edtNama.setText(d.getString("nama"));
                            edtLokasi.setText(d.getString("lokasi"));
                            edtKategori.setText(d.getString("kategori"));
                            edtDeskripsi.setText(d.getString("deskripsi"));

                            imageUrlLama = d.getString("gambarUrl");

                            Glide.with(this)
                                    .load(imageUrlLama)
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .into(imgPreview);
                        }
                    });
        } else {
            setTitle("Tambah Wisata");
        }

        // ================= BOTTOM MENU =================
        BottomMenuHelper.setup(this);
    }

    // ================= PILIH GAMBAR =================
    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    // ================= SIMPAN DATA =================
    private void simpanData() {

        if (imageUri == null && imageUrlLama == null) {
            Toast.makeText(this,
                    "Pilih gambar terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        if (imageUri != null) {
            uploadGambarDanSimpan();
        } else {
            // EDIT TANPA GANTI GAMBAR
            simpanKeFirestore(imageUrlLama);
        }
    }

    // ================= UPLOAD GAMBAR =================
    private void uploadGambarDanSimpan() {

        String fileName = UUID.randomUUID().toString();

        MediaManager.get().upload(imageUri)
                .option("public_id", fileName)
                .callback(new UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = resultData.get("secure_url").toString();
                        simpanKeFirestore(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddWisataActivity.this,
                                "Upload gagal: " + error.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long total) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    // ================= SIMPAN FIRESTORE =================
    private void simpanKeFirestore(String imageUrl) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("nama", edtNama.getText().toString());
        map.put("lokasi", edtLokasi.getText().toString());
        map.put("kategori", edtKategori.getText().toString());
        map.put("deskripsi", edtDeskripsi.getText().toString());
        map.put("gambarUrl", imageUrl);
        map.put("timestamp", System.currentTimeMillis());

        if (wisataId == null) {
            // ===== ADD =====
            map.put("rating", 0);
            map.put("loved", false);

            db.collection("wisata")
                    .add(map)
                    .addOnSuccessListener(r -> selesai("Berhasil ditambahkan"));

        } else {
            // ===== UPDATE =====
            db.collection("wisata")
                    .document(wisataId)
                    .update(map)
                    .addOnSuccessListener(r -> selesai("Berhasil diperbarui"));
        }
    }

    // ================= FINISH =================
    private void selesai(String msg) {
        progressDialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
