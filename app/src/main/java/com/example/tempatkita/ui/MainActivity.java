package com.example.tempatkita.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

        // read regency.json from assets (if file missing, we fallback silently to empty list)
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
                // Some JSONs use "regency" key, others "name" — keep to "regency" per your example
                String namaKota = obj.optString("regency", obj.optString("name", ""));
                if (!namaKota.isEmpty()) daftarKota.add(namaKota);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat daftar kota (periksa assets/regency.json)", Toast.LENGTH_SHORT).show();
        }

        // set adapter for AutoCompleteTextView
        ArrayAdapter<String> adapterKota = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, daftarKota);
        dropdownKota.setAdapter(adapterKota);
        dropdownKota.setThreshold(1);

        // IMPORTANT: use parent.getItemAtPosition(position) — not daftarKota.get(position)
        dropdownKota.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item == null) return;
            String selected = item.toString();

            // set text into dropdown without filtering again
            dropdownKota.setText(selected, false);

            // hide keyboard & clear focus
            dropdownKota.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(dropdownKota.getWindowToken(), 0);

            kotaDipilih = selected != null ? selected : "";
            filterCombined();
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

        adapter = new WisataAdapter(this,filteredList);
        recyclerView.setAdapter(adapter);
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

        // remove common words to make matching more tolerant
        q = normalize(q);
        k = normalize(k);

        for (Wisata w : wisataList) {
            String nama = normalize(w.getNama().toLowerCase());
            String lokasi = normalize(w.getLokasi().toLowerCase());

            boolean matchNama = q.isEmpty() || nama.contains(q);
            boolean matchKota = k.isEmpty() || lokasi.contains(k) || k.contains(lokasi);

            if (matchNama && matchKota) {
                filteredList.add(w);
            }
        }

        if (filteredList.isEmpty()) {
            // show toast only if user actively searching or chose a city
            if (!q.isEmpty() || !k.isEmpty())
                Toast.makeText(this, "Tidak ada hasil yang cocok", Toast.LENGTH_SHORT).show();
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
