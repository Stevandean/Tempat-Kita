package com.example.tempatkita.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempatkita.R;

import java.io.IOException;
import java.io.InputStream;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ambil komponen dari XML
        ImageView imageView = findViewById(R.id.imageViewDetail);
        TextView textNama = findViewById(R.id.textViewName);
        TextView textLokasi = findViewById(R.id.textViewLocation);
        TextView textDeskripsi = findViewById(R.id.textViewDescription);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnLihatPeta = findViewById(R.id.btnLihatPeta);

        // Ambil data dari intent
        String nama = getIntent().getStringExtra("nama");
        String lokasi = getIntent().getStringExtra("lokasi");
        String gambarNama = getIntent().getStringExtra("gambar"); // ambil nama file gambar (dari JSON)

        // Tampilkan data teks
        textNama.setText(nama);
        textLokasi.setText(lokasi);

        // 🔹 Load gambar dari assets/img/
        if (gambarNama != null && !gambarNama.isEmpty()) {
            try {
                // Bersihkan path agar tidak ada slash ganda
                gambarNama = gambarNama.replaceFirst("^/+", ""); // hapus '/' di depan jika ada

                // 🔸 Jangan tambah "/img" lagi di sini
                InputStream inputStream = getAssets().open(gambarNama);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gambar tidak ditemukan: " + gambarNama, Toast.LENGTH_SHORT).show();
            }
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground); // fallback
        }


        // deskripsi panjang secara dinamis
        textDeskripsi.setText(
                "Tempat wisata " + nama + " berlokasi di " + lokasi + ", sebuah kawasan yang terkenal dengan pesona alamnya yang menawan serta atmosfer yang menenangkan. " +
                "Destinasi ini menjadi salah satu pilihan favorit bagi wisatawan yang ingin melepas penat dari rutinitas, sekaligus menikmati keindahan alam, budaya lokal, dan suasana khas daerah yang ramah dan bersahabat.\n\n" +

                "Di " + nama + ", pengunjung akan disambut dengan panorama alam yang memukau—mulai dari hamparan hijau pepohonan, udara yang sejuk, hingga suara alam yang menenangkan. " +
                "Tempat ini sangat cocok untuk Anda yang menyukai ketenangan atau ingin mencari inspirasi di tengah suasana alami yang asri. " +
                "Bagi pencinta fotografi, tersedia banyak spot menarik yang sangat Instagramable, mulai dari sudut pemandangan alam terbuka hingga area dengan dekorasi unik yang menjadi ciri khas destinasi ini.\n\n" +

                "Selain pesona alamnya, " + nama + " juga menjadi daya tarik wisata kuliner. Di sekitar lokasi, Anda bisa mencicipi beragam makanan khas daerah yang menggugah selera, ditemani dengan suasana hangat masyarakat lokal. " +
                "Tak jarang pula, terdapat pertunjukan seni dan budaya yang menambah nilai pengalaman berwisata di sini.\n\n" +

                "Fasilitas di kawasan ini sudah cukup lengkap untuk menunjang kenyamanan pengunjung. Tersedia area parkir luas, pusat informasi wisata, toilet bersih, area bermain anak, serta tempat istirahat yang nyaman. " +
                "Beberapa pengelola juga menyediakan penyewaan sepeda, gazebo untuk bersantai, serta toko suvenir yang menjual produk lokal sebagai oleh-oleh. " +
                "Semua ini menjadikan pengalaman berkunjung semakin menyenangkan dan berkesan.\n\n" +

                "Beragam aktivitas bisa dilakukan di " + nama + ", mulai dari berjalan santai di sekitar area, berfoto ria di spot pemandangan terbaik, menikmati piknik keluarga, hingga mengikuti kegiatan outbound yang seru. " +
                "Bagi penggemar petualangan, beberapa jalur trekking dan area eksplorasi alam juga tersedia untuk dieksplor.\n\n" +

                "Waktu terbaik untuk berkunjung biasanya pada pagi hari saat udara masih segar dan kabut tipis menyelimuti area, atau sore hari ketika cahaya matahari mulai redup dan menciptakan suasana yang hangat serta romantis. " +
                "Bagi wisatawan yang ingin menghabiskan waktu lebih lama, tersedia penginapan mulai dari homestay sederhana hingga resort modern dengan fasilitas lengkap dan pemandangan indah langsung ke alam sekitar.\n\n" +

                "Dengan perpaduan keindahan alam, fasilitas yang memadai, dan keramahan masyarakat sekitar, " + nama + " benar-benar menawarkan pengalaman wisata yang menyenangkan dan tak terlupakan. " +
                "Destinasi ini sangat direkomendasikan bagi siapa pun yang ingin beristirahat sejenak dari kesibukan kota dan mencari ketenangan di tengah nuansa alam yang menenangkan.\n\n" +

                "Jadi, saat Anda berkunjung ke kawasan " + lokasi + ", jangan lewatkan kesempatan untuk menikmati pesona " + nama + ". " +
                "Rasakan setiap momennya, abadikan keindahannya, dan bawa pulang kenangan berharga dari tempat wisata luar biasa ini!"
        );

        // 🔙 Tombol kembali ke halaman utama
        btnBack.setOnClickListener(v -> {
            finish(); // Kembali ke halaman sebelumnya (MainActivity)
            Toast.makeText(this, "Kembali ke daftar wisata", Toast.LENGTH_SHORT).show();
        });

        // 📍 Tombol lihat peta (buka Google Maps)
        btnLihatPeta.setOnClickListener(v -> {
            String url = "https://www.google.com/maps/search/" + nama.replace(" ", "+");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // 🔔 Notifikasi saat halaman detail dibuka
        Toast.makeText(this, "Menampilkan detail: " + nama, Toast.LENGTH_SHORT).show();
    }
}
