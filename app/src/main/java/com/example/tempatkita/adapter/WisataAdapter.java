package com.example.tempatkita.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.ViewHolder> {

    private final List<Wisata> wisataList;
    private final SharedPreferences prefs;
    private final Set<String> savedItems;
    private final Context context;

    public WisataAdapter(Context context, List<Wisata> wisataList) {
        this.context = context;
        this.wisataList = wisataList;
        this.prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.savedItems = new HashSet<>(prefs.getStringSet("saved_wisata", new HashSet<>()));

        // pastikan setiap data tahu status "loved" dan posisi asli
        for (int i = 0; i < wisataList.size(); i++) {
            Wisata w = wisataList.get(i);
            w.setOriginalIndex(i);
            w.setLoved(savedItems.contains(w.getNama()));
        }

        sortList();
    }

    // dipanggil ketika load more
    public void syncWithSavedPreferences(List<Wisata> newList) {
        for (int i = 0; i < newList.size(); i++) {
            Wisata w = newList.get(i);
            w.setOriginalIndex(wisataList.size() + i);
            w.setLoved(savedItems.contains(w.getNama()));
        }
        wisataList.addAll(newList);
        sortList();
        notifyDataSetChanged();
    }

    public void refreshFromPrefs() {
        savedItems.clear();
        savedItems.addAll(prefs.getStringSet("saved_wisata", new HashSet<>()));
        for (Wisata w : wisataList) {
            w.setLoved(savedItems.contains(w.getNama()));
        }
        sortList();
        notifyDataSetChanged();
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

        try {
            InputStream is = context.getAssets().open(wisata.getGambar());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.imageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.iconLove.setImageResource(
                wisata.isLoved() ? R.drawable.ic_love_filled : R.drawable.ic_love_border
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("nama", wisata.getNama());
            intent.putExtra("lokasi", wisata.getLokasi());
            intent.putExtra("gambar", wisata.getGambar());
            v.getContext().startActivity(intent);
        });

        holder.iconLove.setOnClickListener(v -> toggleLike(holder, wisata));
    }

    private void toggleLike(ViewHolder holder, Wisata wisata) {
        boolean currentlyLiked = wisata.isLoved();

        // animasi love
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.iconLove, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.iconLove, "scaleY", 1f, 1.3f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.start();

        wisata.setLoved(!currentlyLiked);
        if (currentlyLiked) {
            savedItems.remove(wisata.getNama());
        } else {
            savedItems.add(wisata.getNama());
        }

        prefs.edit().putStringSet("saved_wisata", new HashSet<>(savedItems)).apply();

        int oldPosition = wisataList.indexOf(wisata);
        sortList();
        int newPosition = wisataList.indexOf(wisata);

        notifyItemMoved(oldPosition, newPosition);
        notifyItemChanged(newPosition);
    }

    private void sortList() {
        Collections.sort(wisataList, new Comparator<Wisata>() {
            @Override
            public int compare(Wisata o1, Wisata o2) {
                if (o1.isLoved() && !o2.isLoved()) return -1;
                if (!o1.isLoved() && o2.isLoved()) return 1;
                return Integer.compare(o1.getOriginalIndex(), o2.getOriginalIndex());
            }
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