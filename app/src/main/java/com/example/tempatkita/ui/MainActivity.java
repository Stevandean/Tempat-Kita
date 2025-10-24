package com.example.tempatkita.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.adapter.WisataAdapter;
import com.example.tempatkita.model.Wisata;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataAdapter adapter;
    private List<Wisata> wisataList;
    private List<Wisata> filteredList;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Toolbar setup
//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("🌴 Info Pariwisata Indonesia");
//        }

        // 🔹 Inisialisasi komponen
        recyclerView = findViewById(R.id.recyclerViewWisata);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        wisataList = getWisataList();
        filteredList = new ArrayList<>();

        // Tampilkan halaman pertama (10 item)
        int end = Math.min(PAGE_SIZE, wisataList.size());
        filteredList.addAll(wisataList.subList(0, end));

        adapter = new WisataAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // 🔹 Setup SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        // 🔹 Tombol Load More
        MaterialButton btnLoadMore = findViewById(R.id.btnLoadMore);
        btnLoadMore.setOnClickListener(v -> loadMore());
    }

    // 🔹 Fungsi untuk filter pencarian
    private void filterList(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(wisataList.subList(0, Math.min(currentPage * PAGE_SIZE, wisataList.size())));
        } else {
            for (Wisata w : wisataList) {
                if (w.getNama().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(w);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 🔹 Fungsi pagination
    private void loadMore() {
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, wisataList.size());
        if (start < end) {
            filteredList.addAll(wisataList.subList(start, end));
            adapter.notifyItemRangeInserted(start, end - start);
            currentPage++;
        } else {
            Toast.makeText(this, "Semua data sudah ditampilkan", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔹 Data wisata asli (dummy manual, tapi nyata)
    private List<Wisata> getWisataList() {
        List<Wisata> list = new ArrayList<>();

        list.add(new Wisata("Candi Borobudur", "Magelang, Jawa Tengah", R.drawable.hero));
        list.add(new Wisata("Candi Prambanan", "Sleman, Yogyakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Bromo", "Probolinggo, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Danau Toba", "Sumatera Utara", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Komodo", "Nusa Tenggara Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Raja Ampat", "Papua Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Kawah Ijen", "Banyuwangi, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Kuta", "Bali", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Parangtritis", "Yogyakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Derawan", "Kalimantan Timur", R.drawable.ic_launcher_foreground));

        list.add(new Wisata("Gunung Rinjani", "Lombok, Nusa Tenggara Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Labuan Bajo", "Flores, Nusa Tenggara Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Pink", "Lombok Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Tana Toraja", "Sulawesi Selatan", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Taman Nasional Ujung Kulon", "Banten", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Samosir", "Sumatera Utara", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Curug Cimahi", "Bandung Barat, Jawa Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Anyer", "Serang, Banten", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Kepulauan Seribu", "DKI Jakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Taman Mini Indonesia Indah", "Jakarta Timur", R.drawable.ic_launcher_foreground));

        list.add(new Wisata("Bukit Tinggi", "Sumatera Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Losari", "Makassar, Sulawesi Selatan", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Merapi", "Yogyakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Balekambang", "Malang, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Candi Mendut", "Magelang, Jawa Tengah", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Sanur", "Denpasar, Bali", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Goa Pindul", "Gunungkidul, Yogyakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Taman Nasional Baluran", "Situbondo, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Semeru", "Lumajang, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Tanjung Tinggi", "Belitung", R.drawable.ic_launcher_foreground));

        list.add(new Wisata("Pulau Weh", "Sabang, Aceh", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Benteng Rotterdam", "Makassar", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Kawah Putih", "Ciwidey, Bandung", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Taman Safari Indonesia", "Bogor, Jawa Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Papandayan", "Garut, Jawa Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Pandawa", "Bali", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Bukit Bintang", "Gunungkidul, Yogyakarta", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Air Terjun Madakaripura", "Probolinggo, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Kampung Naga", "Tasikmalaya, Jawa Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Lembah Harau", "Sumatera Barat", R.drawable.ic_launcher_foreground));

        list.add(new Wisata("Pantai Ora", "Maluku Tengah", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Kelimutu", "Ende, Flores", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Nihiwatu", "Sumba Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Belitung", "Kepulauan Bangka Belitung", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Tanjung Lesung", "Pandeglang, Banten", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Candi Sewu", "Klaten, Jawa Tengah", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Air Terjun Sipiso-piso", "Karo, Sumatera Utara", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Tidung", "Kepulauan Seribu", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Danau Kelimutu", "Nusa Tenggara Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Lawu", "Karanganyar, Jawa Tengah", R.drawable.ic_launcher_foreground));

        return list;
    }
}
