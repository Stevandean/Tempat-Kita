package com.example.tempatkita.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.model.Wisata;
import com.example.tempatkita.ui.DetailActivity;

import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.ViewHolder> {

    private List<Wisata> wisataList;

    public WisataAdapter(List<Wisata> wisataList) {
        this.wisataList = wisataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wisata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wisata wisata = wisataList.get(position);
        holder.textNama.setText(wisata.getNama());
        holder.textLokasi.setText(wisata.getLokasi());
        holder.imageView.setImageResource(wisata.getGambar());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("nama", wisata.getNama());
            intent.putExtra("lokasi", wisata.getLokasi());
            intent.putExtra("gambar", wisata.getGambar());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wisataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textNama, textLokasi;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageWisata);
            textNama = itemView.findViewById(R.id.textNamaWisata);
            textLokasi = itemView.findViewById(R.id.textLokasiWisata);
        }
    }
}
