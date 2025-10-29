package com.example.tempatkita.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.ViewHolder> {

    private final List<Wisata> wisataList;
    private final SharedPreferences prefs;
    private final Set<String> savedItems;

    public WisataAdapter(Context context, List<Wisata> wisataList) {
        this.wisataList = wisataList;
        this.prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.savedItems = new HashSet<>(prefs.getStringSet("saved_wisata", new HashSet<>()));
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

        boolean isSaved = savedItems.contains(wisata.getNama());
        holder.iconLove.setImageResource(isSaved ? R.drawable.ic_love_filled : R.drawable.ic_love_border);

        // Klik item → buka detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("nama", wisata.getNama());
            intent.putExtra("lokasi", wisata.getLokasi());
            intent.putExtra("gambar", wisata.getGambar());
            v.getContext().startActivity(intent);
        });

        // Klik love → animasi + toggle simpan/hapus
        holder.iconLove.setOnClickListener(v -> {
            boolean currentlySaved = savedItems.contains(wisata.getNama());

            // Animasi pop (scale in & out)
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.iconLove, "scaleX", 1f, 1.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.iconLove, "scaleY", 1f, 1.3f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(500);
            animatorSet.start();

            // Ganti icon setelah animasi mulai
            if (currentlySaved) {
                savedItems.remove(wisata.getNama());
                holder.iconLove.setImageResource(R.drawable.ic_love_border);
            } else {
                savedItems.add(wisata.getNama());
                holder.iconLove.setImageResource(R.drawable.ic_love_filled);
            }

            prefs.edit().putStringSet("saved_wisata", new HashSet<>(savedItems)).apply();
        });
    }

    @Override
    public int getItemCount() {
        return wisataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, iconLove;
        TextView textNama, textLokasi;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageWisata);
            textNama = itemView.findViewById(R.id.textNamaWisata);
            textLokasi = itemView.findViewById(R.id.textLokasiWisata);
            iconLove = itemView.findViewById(R.id.iconLove);
        }
    }
}
