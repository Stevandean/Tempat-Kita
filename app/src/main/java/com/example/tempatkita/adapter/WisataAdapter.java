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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.model.Wisata;
import com.example.tempatkita.ui.DetailActivity;
import com.example.tempatkita.ui.MainActivity;

import java.io.IOException;
import java.io.InputStream;
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
    }

    public void syncWithSavedPreferences(List<Wisata> newList) {
        for (Wisata w : newList) {
            w.setLoved(savedItems.contains(w.getNama()));
        }
        wisataList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wisata, parent, false));
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
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.iconLove.setImageResource(
                wisata.isLoved() ? R.drawable.ic_love_filled : R.drawable.ic_love_border
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("nama", wisata.getNama());
            intent.putExtra("lokasi", wisata.getLokasi());
            intent.putExtra("gambar", wisata.getGambar());
            context.startActivity(intent);
        });

        holder.iconLove.setOnClickListener(v -> toggleLike(holder, wisata));
    }

    private void toggleLike(ViewHolder holder, Wisata wisata) {
        boolean currentlyLiked = wisata.isLoved();

        ObjectAnimator sx = ObjectAnimator.ofFloat(holder.iconLove, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(holder.iconLove, "scaleY", 1f, 1.3f, 1f);
        AnimatorSet as = new AnimatorSet();
        as.playTogether(sx, sy);
        as.setDuration(300);
        as.start();

        wisata.setLoved(!currentlyLiked);
        if (currentlyLiked) savedItems.remove(wisata.getNama());
        else savedItems.add(wisata.getNama());

        prefs.edit().putStringSet("saved_wisata", new HashSet<>(savedItems)).apply();

        Toast.makeText(context,
                wisata.getNama() + (wisata.isLoved() ? " Ditambahkan ke favorit" : " Dihapus dari favorit"),
                Toast.LENGTH_SHORT).show();

        // Sync dengan MainActivity list
        if (context instanceof MainActivity) {
            MainActivity main = (MainActivity) context;
            main.updateFavorite(wisata, wisata.isLoved());

            if (!main.isFiltering) {
                main.sortListsAndReload();
                return;
            }
        }

        notifyItemChanged(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return wisataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, iconLove;
        TextView textNama, textLokasi;

        ViewHolder(View item) {
            super(item);
            imageView = item.findViewById(R.id.imageWisata);
            textNama = item.findViewById(R.id.textNamaWisata);
            textLokasi = item.findViewById(R.id.textLokasiWisata);
            iconLove = item.findViewById(R.id.iconLove);
        }
    }
}
