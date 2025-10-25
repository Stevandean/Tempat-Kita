package com.example.tempatkita.ui;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
    private final List<Wisata> wisataList = new ArrayList<>();
    private final List<Wisata> filteredList = new ArrayList<>();

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSearchView();
        setupRecyclerView();
        setupLoadMoreButton();
    }

    /** 🔍 Setup SearchView di Light & Dark Mode */
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        if (searchView == null) return;

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setQueryHint("Mau berlibur ke mana?");

        // Deteksi apakah sedang di Dark Mode
        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // Ambil warna dari resources
        int backgroundColor = ContextCompat.getColor(this, R.color.surface);
        int textColor = ContextCompat.getColor(this, R.color.text_primary);
        int hintColor = ContextCompat.getColor(this, R.color.text_secondary);
        int iconColor = ContextCompat.getColor(this, R.color.text_primary);

        // Ubah background SearchView
        searchView.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        try {
            // Background input field (search plate)
            int plateId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_plate", null, null);
            View plate = searchView.findViewById(plateId);
            if (plate != null) {
                plate.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
            }

            // Text input
            int textId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(textId);
            if (textView != null) {
                textView.setTextColor(textColor);
                textView.setHintTextColor(hintColor);
                textView.setHint("Mau berlibur ke mana?");
                textView.setTextSize(15);
            }

            // Ikon search
            int iconId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_mag_icon", null, null);
            ImageView icon = searchView.findViewById(iconId);
            if (icon != null) {
                icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }

            // Ikon close
            int closeId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_close_btn", null, null);
            ImageView closeIcon = searchView.findViewById(closeId);
            if (closeIcon != null) {
                closeIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Listener pencarian
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
    }

    /** Setup RecyclerView dan tampilkan data */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewWisata);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        wisataList.addAll(getWisataList());
        int end = Math.min(PAGE_SIZE, wisataList.size());
        filteredList.addAll(wisataList.subList(0, end));

        adapter = new WisataAdapter(filteredList);
        recyclerView.setAdapter(adapter);
    }

    /** Tombol Load More */
    private void setupLoadMoreButton() {
        MaterialButton btnLoadMore = findViewById(R.id.btnLoadMore);
        btnLoadMore.setOnClickListener(v -> loadMore());
    }

    /** Filter daftar wisata berdasarkan input */
    private void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            int end = Math.min(currentPage * PAGE_SIZE, wisataList.size());
            filteredList.addAll(wisataList.subList(0, end));
        } else {
            for (Wisata w : wisataList) {
                if (w.getNama().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(w);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /** Pagination (load lebih banyak data) */
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

    /** Data wisata dummy */
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
        list.add(new Wisata("Tana Toraja", "Sulawesi Selatan", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Weh", "Aceh", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Labuan Bajo", "Flores, NTT", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Bukit Tinggi", "Sumatera Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Belitung", "Kepulauan Bangka Belitung", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Samosir", "Sumatera Utara", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Taman Nasional Baluran", "Banyuwangi, Jawa Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pantai Pink", "Lombok Timur", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Pulau Menjangan", "Bali Barat", R.drawable.ic_launcher_foreground));
        list.add(new Wisata("Gunung Rinjani", "Lombok, NTB", R.drawable.ic_launcher_foreground));
        return list;
    }
}
