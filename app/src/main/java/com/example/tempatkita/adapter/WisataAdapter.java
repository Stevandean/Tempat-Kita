package com.example.tempatkita.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tempatkita.R;
import com.example.tempatkita.model.Wisata;

import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.ViewHolder> {

    Context context;
    List<Wisata> data;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Wisata wisata);
        void onLoveClick(Wisata wisata, int position);
    }

    public WisataAdapter(Context context, List<Wisata> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_wisata, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Wisata w = data.get(i);

        holder.nama.setText(w.getNama());
        holder.lokasi.setText(w.getLokasi());

        if (w.getImages() != null && !w.getImages().isEmpty()) {
            Glide.with(context)
                    .load(w.getImages().get(0))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(w));
        holder.love.setOnClickListener(v -> {
            listener.onLoveClick(w, i);
            holder.love.setImageResource(R.drawable.ic_love_filled);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, love;
        TextView nama, lokasi;

        public ViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imageWisata);
            nama = v.findViewById(R.id.textNamaWisata);
            lokasi = v.findViewById(R.id.textLokasiWisata);
            love = v.findViewById(R.id.iconLove);
        }
    }
}
