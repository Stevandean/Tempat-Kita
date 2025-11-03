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

    private SharedPreferences prefs;
    public List<Wisata> lovedList = new ArrayList<>();
    public List<Wisata> normalList = new ArrayList<>();
    public boolean isFiltering = false;

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
//        setupDropdownKota();
        setupRecyclerView();
        setupLoadMoreButton();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setQueryHint("Mau berlibur ke mana?");

        int textColor = ContextCompat.getColor(this, R.color.text_primary);
        int hintColor = ContextCompat.getColor(this, R.color.text_secondary);
        int iconColor = ContextCompat.getColor(this, R.color.text_primary);
        int bg = ContextCompat.getColor(this, R.color.surface);

        try {
            int plateId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_plate", null, null);
            View plate = searchView.findViewById(plateId);
            if (plate != null) plate.setBackgroundTintList(ColorStateList.valueOf(bg));

            int textId = searchView.getContext().getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(textId);
            textView.setTextColor(textColor);
            textView.setHintTextColor(hintColor);
            textView.setHint("Mau berlibur ke mana?");
            textView.setTextSize(15);

            ImageView icon = searchView.findViewById(searchView.getContext().getResources()
                    .getIdentifier("android:id/search_mag_icon", null, null));
            icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

        } catch (Exception ignored) {}

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) {
                currentQuery = q;
                filterCombined();
                return true;
            }
            @Override public boolean onQueryTextChange(String q) {
                currentQuery = q;
                filterCombined();
                return true;
            }
        });
    }

    private void setupDropdownKota() {
        dropdownKota = findViewById(R.id.dropdownKota);

        try {
            InputStream is = getAssets().open("regency.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer); is.close();
            JSONArray arr = new JSONArray(new String(buffer, StandardCharsets.UTF_8));

            daftarKota.clear();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                daftarKota.add(obj.optString("regency", obj.optString("name", "")));
            }
        } catch (Exception ignored) {}

        ArrayAdapter<String> adapterKota = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, daftarKota);

        dropdownKota.setAdapter(adapterKota);
        dropdownKota.setThreshold(1);

        dropdownKota.setOnItemClickListener((parent, view, position, id) -> {
            kotaDipilih = parent.getItemAtPosition(position).toString();
            dropdownKota.clearFocus();
            hideKeyboard();
            filterCombined();
        });

        dropdownKota.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c){
                if (s.toString().trim().isEmpty()) {
                    kotaDipilih = "";
                    filterCombined();
                }
            }
            @Override public void afterTextChanged(Editable s){}
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dropdownKota.getWindowToken(), 0);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewWisata);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wisataList.clear();
        wisataList.addAll(getWisataList());

        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> lovedNames = prefs.getStringSet("saved_wisata", new HashSet<>());

        lovedList.clear();
        normalList.clear();

        for (Wisata w : wisataList) {
            if (lovedNames.contains(w.getNama())) {
                w.setLoved(true);
                lovedList.add(w);
            } else {
                normalList.add(w);
            }
        }

        filteredList.clear();
        filteredList.addAll(lovedList);
        int rem = PAGE_SIZE - lovedList.size();
        if (rem > 0) filteredList.addAll(normalList.subList(0, Math.min(rem, normalList.size())));

        adapter = new WisataAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void setupLoadMoreButton() {
        MaterialButton btnLoadMore = findViewById(R.id.btnLoadMore);
        btnLoadMore.setOnClickListener(v -> loadMore());
    }

    /** ✅ Dipanggil Adapter tiap love toggle */
    public void updateFavorite(Wisata w, boolean loved) {
        if (loved) {
            normalList.remove(w);
            if (!lovedList.contains(w)) lovedList.add(w);
        } else {
            lovedList.remove(w);
            if (!normalList.contains(w)) normalList.add(w);
        }
    }

    /** ✅ Untuk refresh list jika tidak sedang filter */
    public void sortListsAndReload() {
        List<Wisata> merged = new ArrayList<>(lovedList);
        merged.addAll(normalList);

        filteredList.clear();
        filteredList.addAll(merged);

        adapter.notifyDataSetChanged();
    }

    private void filterCombined() {
        isFiltering = true;

        filteredList.clear();

        String q = normalize(currentQuery.toLowerCase());
        String k = normalize(kotaDipilih.toLowerCase());

        for (Wisata w : wisataList) {
            String nama = normalize(w.getNama().toLowerCase());
            String lokasi = normalize(w.getLokasi().toLowerCase());

            boolean matchNama = q.isEmpty() || nama.contains(q);
            boolean matchKota = k.isEmpty() || lokasi.contains(k);

            if (matchNama && matchKota) filteredList.add(w);
        }

        if (q.isEmpty() && k.isEmpty()) {
            filteredList.clear();
            filteredList.addAll(lovedList);
            filteredList.addAll(normalList.subList(0, Math.min(PAGE_SIZE, normalList.size())));
        }

        adapter.notifyDataSetChanged();
    }

    private String normalize(String s) {
        return s.replaceAll("(?i)kabupaten", "")
                .replaceAll("(?i)kota", "")
                .replaceAll("[,\\-]"," ")
                .trim();
    }

    private void loadMore() {
        int start = lovedList.size() + (currentPage * PAGE_SIZE);
        int end = Math.min(start + PAGE_SIZE, normalList.size());
        if (start < end) {
            List<Wisata> newList = normalList.subList(start, end);
            adapter.syncWithSavedPreferences(newList);
            currentPage++;
        } else {
            Toast.makeText(this, "Semua data sudah ditampilkan", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Wisata> getWisataList() {
        List<Wisata> list = new ArrayList<>();
        try {
            InputStream is = getAssets().open("destination.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer); is.close();
            JSONArray arr = new JSONArray(new String(buffer, StandardCharsets.UTF_8));

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String nama = obj.optString("nama");
                String lokasi = obj.optString("lokasi");
                String gambar = obj.optString("gambar");

                String[] ext = {".png",".jpg",".jpeg"};
                String path = null;
                for (String e : ext) {
                    try (InputStream t = getAssets().open("img/"+gambar+e)) {
                        path = "img/"+gambar+e; break;
                    } catch (IOException ignored){}
                }
                if (path == null) path = "img/default.png";

                list.add(new Wisata(nama,lokasi,path));
            }
        } catch (Exception ignored) {}
        return list;
    }
}
