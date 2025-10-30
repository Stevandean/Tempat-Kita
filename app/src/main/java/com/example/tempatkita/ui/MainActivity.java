package com.example.tempatkita.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.adapter.WisataAdapter;
import com.example.tempatkita.model.Wisata;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.widget.NestedScrollView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataAdapter adapter;
    private final List<Wisata> wisataList = new ArrayList<>();
    private final List<Wisata> filteredList = new ArrayList<>();

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    private AutoCompleteTextView dropdownKota;
    private List<String> daftarKota = new ArrayList<>();
    private String kotaDipilih = "";
    private String currentQuery = "";

    // tambahkan ini bersama field lain
    private android.content.SharedPreferences prefs;
    private List<Wisata> lovedList = new ArrayList<>();
    private List<Wisata> normalList = new ArrayList<>();
    private int normalOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabGoTop = findViewById(R.id.fabGoTop);
        NestedScrollView scrollView = findViewById(R.id.scrollViewMain);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > 300) {
                        if (fabGoTop.getVisibility() == View.GONE) {
                            fabGoTop.show();
                        }
                    } else {
                        if (fabGoTop.getVisibility() == View.VISIBLE) {
                            fabGoTop.hide();
                        }
                    }
                });

        fabGoTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));

        setupSearchView();
        setupDropdownKota();
        setupRecyclerView();
        setupLoadMoreButton();
    }

    /** Setup SearchView di Light & Dark Mode */
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        if (searchView == null) return;

        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setQueryHint("Mau berlibur ke mana?");

        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int backgroundColor = ContextCompat.getColor(this, R.color.surface);
        int textColor = ContextCompat.getColor(this, R.color.text_primary);
        int hintColor = ContextCompat.getColor(this, R.color.text_secondary);
        int iconColor = ContextCompat.getColor(this, R.color.text_primary);

        searchView.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        try {
            int plateId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_plate", null, null);
            View plate = searchView.findViewById(plateId);
            if (plate != null) {
                plate.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
            }

            int textId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(textId);
            if (textView != null) {
                textView.setTextColor(textColor);
                textView.setHintTextColor(hintColor);
                textView.setHint("Mau berlibur ke mana?");
                textView.setTextSize(15);
            }

            int iconId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_mag_icon", null, null);
            ImageView icon = searchView.findViewById(iconId);
            if (icon != null) {
                icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }

            int closeId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_close_btn", null, null);
            ImageView closeIcon = searchView.findViewById(closeId);
            if (closeIcon != null) {
                closeIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query != null ? query : "";
                filterCombined();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText != null ? newText : "";
                filterCombined();
                return true;
            }
        });
    }

    /** Setup Dropdown Kota/Kabupaten */
    private void setupDropdownKota() {
        dropdownKota = findViewById(R.id.dropdownKota);

        // Load data dari regency.json
        try {
            InputStream is = getAssets().open("regency.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            daftarKota.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String namaKota = obj.optString("regency", obj.optString("name", ""));
                if (!namaKota.isEmpty()) daftarKota.add(namaKota);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat daftar kota (periksa assets/regency.json)", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapterKota = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, daftarKota);
        dropdownKota.setAdapter(adapterKota);
        dropdownKota.setThreshold(1);

        // Saat user memilih kota dari dropdown
        dropdownKota.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item == null) return;
            String selected = item.toString();

            dropdownKota.setText(selected, false);

            // Sembunyikan keyboard
            dropdownKota.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(dropdownKota.getWindowToken(), 0);

            // Simpan kota yang dipilih dan filter
            kotaDipilih = selected != null ? selected : "";
            filterCombined();
        });

        // Tambahkan listener untuk deteksi penghapusan teks manual
        dropdownKota.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Jika teks kosong -> reset kotaDipilih dan tampilkan semua (atau filter hanya berdasarkan search)
                if (s.toString().trim().isEmpty()) {
                    if (!kotaDipilih.isEmpty()) {
                        kotaDipilih = "";
                        filterCombined();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /** Setup RecyclerView dan tampilkan data */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewWisata);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Ambil semua data dari sumber JSON
        wisataList.clear();
        wisataList.addAll(getWisataList());

        // Ambil daftar like dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> lovedNames = prefs.getStringSet("saved_wisata", new HashSet<>());

        // Filter agar destinasi yang di-love selalu muncul di awal
        List<Wisata> lovedList = new ArrayList<>();
        List<Wisata> normalList = new ArrayList<>();

        for (Wisata w : wisataList) {
            if (lovedNames.contains(w.getNama())) {
                w.setLoved(true);
                lovedList.add(w);
            } else {
                w.setLoved(false);
                normalList.add(w);
            }
        }

        // Gabungkan: loved di atas, sisanya di bawah (tapi tetap batasi PAGE_SIZE awal)
        List<Wisata> initialList = new ArrayList<>();
        initialList.addAll(lovedList);

        int remaining = PAGE_SIZE - lovedList.size();
        if (remaining > 0) {
            initialList.addAll(normalList.subList(0, Math.min(remaining, normalList.size())));
        }

        filteredList.clear();
        filteredList.addAll(initialList);

        adapter = new WisataAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // Simpan untuk pagination
        this.lovedList = lovedList;
        this.normalList = normalList;
        currentPage = 1;
    }


    /** Tombol Load More */
    private void setupLoadMoreButton() {
        MaterialButton btnLoadMore = findViewById(R.id.btnLoadMore);
        btnLoadMore.setOnClickListener(v -> loadMore());
    }

    /** Gabungan filter: nama (currentQuery) + kota (kotaDipilih) */
    private void filterCombined() {
        filteredList.clear();

        // Normalize filter strings
        String q = (currentQuery == null ? "" : currentQuery.trim().toLowerCase());
        String k = (kotaDipilih == null ? "" : kotaDipilih.trim().toLowerCase());

        q = normalize(q);
        k = normalize(k);

        for (Wisata w : wisataList) {
            String nama = normalize(w.getNama().toLowerCase());
            String lokasi = normalize(w.getLokasi().toLowerCase());

            boolean matchNama = q.isEmpty() || nama.contains(q);
            boolean matchKota = k.isEmpty() || lokasi.contains(k) || k.contains(lokasi);

            // Filter tetap mempertimbangkan kota walau search dikosongkan
            if (matchNama && matchKota) {
                filteredList.add(w);
            }
        }

        // Kalau user hapus teks pencarian dan tidak pilih kota, tampilkan default 10 data
        if (q.isEmpty() && k.isEmpty()) {
            filteredList.clear();
            int end = Math.min(PAGE_SIZE, wisataList.size());
            filteredList.addAll(wisataList.subList(0, end));
        }

        if (filteredList.isEmpty()) {
            if (!q.isEmpty() || !k.isEmpty()) {
                Toast.makeText(this, "Tidak ada hasil yang cocok", Toast.LENGTH_SHORT).show();
            }
        }

        adapter.notifyDataSetChanged();
    }

    /** Make text simpler for matching */
    private String normalize(String s) {
        if (s == null) return "";
        String result = s.replaceAll("(?i)kabupaten", "")
                .replaceAll("(?i)kota", "")
                .replaceAll("[,\\-]"," ")
                .replaceAll("\\s+"," ")
                .trim();
        return result;
    }

    /** Pagination (load lebih banyak data) */
    private void loadMore() {
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, normalList.size());

        if (start < end) {
            List<Wisata> newList = normalList.subList(start, end);
            adapter.syncWithSavedPreferences(newList);
            currentPage++;
            Toast.makeText(this, "Menampilkan data tambahan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Semua data sudah ditampilkan", Toast.LENGTH_SHORT).show();
        }
    }

    /** Data wisata dari JSON */
    private List<Wisata> getWisataList() {
        List<Wisata> list = new ArrayList<>();
        try {
            InputStream is = getAssets().open("destination.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String nama = obj.optString("nama");
                String lokasi = obj.optString("lokasi");
                String gambar = obj.optString("gambar");

                // Jika JSON cuma berisi nama gambar tanpa ekstensi
                String[] possibleExt = {".png", ".jpg", ".jpeg"};
                String foundPath = null;

                for (String ext : possibleExt) {
                    try (InputStream test = getAssets().open("img/" + gambar + ext)) {
                        foundPath = "img/" + gambar + ext;
                        break;
                    } catch (IOException ignored) {}
                }

                if (foundPath == null) {
                    foundPath = "img/default.png"; // fallback
                }

                list.add(new Wisata(nama, lokasi, foundPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat data wisata (destination.json)", Toast.LENGTH_SHORT).show();
        }
        return list;
    }
}