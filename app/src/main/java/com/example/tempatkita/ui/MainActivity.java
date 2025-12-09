package com.example.tempatkita.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.adapter.WisataAdapter;
import com.example.tempatkita.model.Wisata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataAdapter adapter;
    private List<Wisata> listWisata = new ArrayList<>();
    private List<Wisata> listFull = new ArrayList<>();

    private FirebaseFirestore db;
    private MaterialButton btnLoadMore;
    private FloatingActionButton fabGoTop;
    private DocumentSnapshot lastVisible = null;
    private SearchView searchView;
    private AutoCompleteTextView dropdownKota;

    private static final int LIMIT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ==== INIT UI ====
        recyclerView = findViewById(R.id.recyclerViewWisata);
        btnLoadMore  = findViewById(R.id.btnLoadMore);
        fabGoTop     = findViewById(R.id.fabGoTop);
        searchView   = findViewById(R.id.searchView);
        dropdownKota = findViewById(R.id.dropdownKota);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WisataAdapter(this, listWisata, new WisataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Wisata wisata) {
                Toast.makeText(MainActivity.this, "Buka detail: " + wisata.getNama(), Toast.LENGTH_SHORT).show();
                // di sini nanti intent ke DetailActivity
            }

            @Override
            public void onLoveClick(Wisata wisata, int position) {
                Toast.makeText(MainActivity.this, "Favorite: " + wisata.getNama(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        // ==== FIREBASE ====
        db = FirebaseFirestore.getInstance();
        loadWisata(false);

        btnLoadMore.setOnClickListener(v -> loadWisata(true));
        fabGoTop.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                fabGoTop.setVisibility(rv.canScrollVertically(-1) ? View.VISIBLE : View.GONE);
            }
        });

        handleSearch();

        // ================= Bottom Menu Listener =================
        LinearLayout btnLogin = findViewById(R.id.btnLogin);
        LinearLayout btnHome  = findViewById(R.id.btnHome);
        LinearLayout btnFav   = findViewById(R.id.btnFav);
        LinearLayout btnUser  = findViewById(R.id.btnUser);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnHome.setOnClickListener(v -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());
        btnFav.setOnClickListener(v -> Toast.makeText(this, "Favorit", Toast.LENGTH_SHORT).show());
        btnUser.setOnClickListener(v -> Toast.makeText(this, "User", Toast.LENGTH_SHORT).show());
    }


    // ================= SEARCH =================
    private void handleSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                FilterWisata(newText);
                return true;
            }
        });
    }

    private void FilterWisata(String text) {
        listWisata.clear();

        if (text.isEmpty()) listWisata.addAll(listFull);
        else {
            for (Wisata w : listFull) {
                if (w.getNama().toLowerCase().contains(text.toLowerCase()))
                    listWisata.add(w);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ================= FIREBASE LOAD =================
    private void loadWisata(boolean loadMore) {

        Query query = db.collection("wisata")
                .orderBy("nama")
                .limit(LIMIT);

        if (loadMore && lastVisible != null)
            query = query.startAfter(lastVisible);

        query.get().addOnSuccessListener(snap -> {
            if (!loadMore) {
                listWisata.clear();
                listFull.clear();
            }

            if (!snap.isEmpty()) {
                lastVisible = snap.getDocuments().get(snap.size() - 1);

                for (DocumentSnapshot doc : snap) {
                    Wisata w = doc.toObject(Wisata.class);
                    listWisata.add(w);
                    listFull.add(w);
                }
                adapter.notifyDataSetChanged();
            }

            btnLoadMore.setVisibility(snap.size() < LIMIT ? View.GONE : View.VISIBLE);

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
        );
    }
}
