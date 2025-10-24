package com.example.tempatkita.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempatkita.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ambil komponen dari XML baru
        ImageView imageView = findViewById(R.id.imageViewDetail);
        TextView textNama = findViewById(R.id.textViewName);
        TextView textLokasi = findViewById(R.id.textViewLocation);
        TextView textDeskripsi = findViewById(R.id.textViewDescription);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnLihatPeta = findViewById(R.id.btnLihatPeta);

        // Ambil data dari intent
        String nama = getIntent().getStringExtra("nama");
        String lokasi = getIntent().getStringExtra("lokasi");
        int gambar = getIntent().getIntExtra("gambar", 0);

        // Tampilkan data
        textNama.setText(nama);
        textLokasi.setText(lokasi);
        imageView.setImageResource(gambar);

        // Tambahkan deskripsi panjang secara dinamis
        textDeskripsi.setText(
            "Tempat wisata " + nama + " terletak di " + lokasi + ". " +
            "Destinasi ini dikenal sebagai salah satu lokasi wisata favorit yang menawarkan perpaduan keindahan alam, budaya, dan kenyamanan bagi para pengunjung.\n\n" +
            "Di " + nama + ", Anda dapat menikmati suasana yang menenangkan dengan pemandangan alam yang memukau. " +
            "Bagi para pencinta fotografi, tempat ini menawarkan banyak spot menarik yang Instagramable. " +
            "Selain itu, wisatawan juga dapat mencicipi kuliner khas daerah sekitar yang menggugah selera.\n\n" +
            "Fasilitas di kawasan ini cukup lengkap, mulai dari area parkir yang luas, pusat oleh-oleh, hingga area istirahat yang nyaman. " +
            "Beberapa kegiatan populer yang bisa dilakukan di sini antara lain berjalan-jalan santai, bersepeda, piknik bersama keluarga, atau sekadar menikmati udara segar sambil bersantai.\n\n" +
            "Waktu terbaik untuk berkunjung adalah saat pagi atau sore hari, ketika cuaca terasa sejuk dan cahaya matahari menambah keindahan panorama. " +
            "Bagi yang ingin menginap, tersedia pula penginapan dengan berbagai pilihan harga dan fasilitas.\n\n" +
            "Dengan segala daya tarik dan keunikannya, " + nama + " menjadi destinasi yang wajib dikunjungi saat Anda berada di kawasan " + lokasi +
            ". Nikmati pengalaman tak terlupakan dan abadikan setiap momennya di tempat wisata luar biasa ini!"
        );


        // Tombol kembali ke halaman utama
        btnBack.setOnClickListener(v -> {
            finish(); // Kembali ke halaman sebelumnya (MainActivity)
            Toast.makeText(this, "Kembali ke daftar wisata", Toast.LENGTH_SHORT).show();
        });

        // Tombol lihat peta (contoh buka Google Maps)
        btnLihatPeta.setOnClickListener(v -> {
            String url = "https://www.google.com/maps/search/" + nama.replace(" ", "+");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // Notifikasi saat halaman detail dibuka
        Toast.makeText(this, "Menampilkan detail: " + nama, Toast.LENGTH_SHORT).show();
    }
}
