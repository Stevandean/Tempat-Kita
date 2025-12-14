package com.example.tempatkita.adapter;

// ================= IMPORT ANDROID =================
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// ================= IMPORT ANDROIDX =================
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// ================= IMPORT LIBRARY =================
import com.bumptech.glide.Glide;

// ================= IMPORT APP =================
import com.example.tempatkita.R;
import com.example.tempatkita.model.Wisata;

// ================= IMPORT JAVA =================
import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.ViewHolder> {

    private Context context;
    private List<Wisata> data;
    private boolean isAdmin;
    private Listener listener;

    // ================= INTERFACE =================
    public interface Listener {
        void onItemClick(Wisata w);
        void onLove(Wisata w);
        void onEdit(Wisata w);
        void onDelete(Wisata w);
    }

    // ================= CONSTRUCTOR =================
    public WisataAdapter(Context context,
                         List<Wisata> data,
                         boolean isAdmin,
                         Listener listener) {
        this.context = context;
        this.data = data;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    // ================= VIEW HOLDER =================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_wisata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Wisata w = data.get(position);

        // ================= RESET STATE (WAJIB) =================
        h.love.setVisibility(View.GONE);
        h.layoutAdmin.setVisibility(View.GONE);

        h.love.setOnClickListener(null);
        h.btnEdit.setOnClickListener(null);
        h.btnDelete.setOnClickListener(null);

        // ================= DATA =================
        h.nama.setText(w.getNama());
        h.lokasi.setText(w.getLokasi());

        Glide.with(context)
                .load(w.getGambarUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(h.image);

        h.itemView.setOnClickListener(v -> listener.onItemClick(w));

        // ================= ROLE =================
        if (isAdmin) {
            // ADMIN
            h.layoutAdmin.setVisibility(View.VISIBLE);

            h.btnEdit.setOnClickListener(v -> listener.onEdit(w));
            h.btnDelete.setOnClickListener(v -> listener.onDelete(w));
        } else {
            // USER
            h.love.setVisibility(View.VISIBLE);

            h.love.setOnClickListener(v -> listener.onLove(w));
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // ================= VIEW HOLDER CLASS =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, love, btnEdit, btnDelete;
        TextView nama, lokasi;
        View layoutAdmin;

        ViewHolder(@NonNull View v) {
            super(v);

            image = v.findViewById(R.id.imageWisata);
            nama = v.findViewById(R.id.textNamaWisata);
            lokasi = v.findViewById(R.id.textLokasiWisata);

            love = v.findViewById(R.id.iconLove);
            layoutAdmin = v.findViewById(R.id.layoutAdminAction);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
