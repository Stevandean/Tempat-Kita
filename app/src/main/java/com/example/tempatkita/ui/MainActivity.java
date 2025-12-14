package com.example.tempatkita.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.adapter.WisataAdapter;
import com.example.tempatkita.api.CloudinaryConfig;
import com.example.tempatkita.model.Wisata;
import com.example.tempatkita.utils.BottomMenuHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataAdapter adapter;
    private List<Wisata> listWisata = new ArrayList<>();
    private List<Wisata> listFull = new ArrayList<>();
    private FirebaseFirestore db;
    private FloatingActionButton fabGoTop;
    private SearchView searchView;
    private AutoCompleteTextView dropdownKota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ================= INIT =================
        CloudinaryConfig.init(this);
        db = FirebaseFirestore.getInstance(); // ✅ WAJIB DULU

        boolean isAdmin = getSharedPreferences("USER_DATA", MODE_PRIVATE)
                .getString("role", "user")
                .equals("admin");

        recyclerView = findViewById(R.id.recyclerViewWisata);
        fabGoTop = findViewById(R.id.fabGoTop);
        searchView = findViewById(R.id.searchView);
        dropdownKota = findViewById(R.id.dropdownKota);

        // ================= RECYCLER =================
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WisataAdapter(this, listWisata, isAdmin,
                new WisataAdapter.Listener() {

                    @Override
                    public void onItemClick(Wisata w) {
                        Intent i = new Intent(MainActivity.this, DetailActivity.class);
                        i.putExtra("data", w);           // OBJECT Wisata
                        i.putExtra("wisata", w.getId()); // ID untuk comment
                        startActivity(i);
                    }

                    @Override
                    public void onLove(Wisata w) {
                        Toast.makeText(MainActivity.this,
                                "Favorite " + w.getNama(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEdit(Wisata w) {
                        Intent i = new Intent(MainActivity.this, AddWisataActivity.class);
                        i.putExtra("id", w.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(Wisata w) {
                        db.collection("wisata").document(w.getId())
                                .delete()
                                .addOnSuccessListener(a ->
                                        Toast.makeText(MainActivity.this,
                                                "Wisata dihapus",
                                                Toast.LENGTH_SHORT).show());
                    }
                });

        recyclerView.setAdapter(adapter);

        // ================= REALTIME FIRESTORE =================
        loadWisataRealtime();

        // ================= FAB =================
        fabGoTop.setOnClickListener(v ->
                recyclerView.smoothScrollToPosition(0));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                fabGoTop.setVisibility(
                        rv.canScrollVertically(-1) ? View.VISIBLE : View.GONE
                );
            }
        });

        handleSearch();

        // ================= BOTTOM MENU =================
        BottomMenuHelper.setup(this);
    }

    // ================= SEARCH =================
    private void handleSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWisata(newText);
                return true;
            }
        });
    }

    private void filterWisata(String text) {
        listWisata.clear();

        if (text.isEmpty()) {
            listWisata.addAll(listFull);
        } else {
            for (Wisata w : listFull) {
                if (w.getNama().toLowerCase().contains(text.toLowerCase())) {
                    listWisata.add(w);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ================= REALTIME LOAD =================
    private void loadWisataRealtime() {

        db.collection("wisata")
                .orderBy("nama")
                .addSnapshotListener((snap, e) -> {

                    if (e != null || snap == null) return;

                    listWisata.clear();
                    listFull.clear();

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Wisata w = doc.toObject(Wisata.class);
                        if (w != null) {
                            w.setId(doc.getId()); // 🔥 INI YANG WAJIB
                            listWisata.add(w);
                            listFull.add(w);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
