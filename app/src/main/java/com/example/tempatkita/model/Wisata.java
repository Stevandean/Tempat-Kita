package com.example.tempatkita.model;

import java.util.List;

public class Wisata {
    private String id;
    private String nama;
    private String lokasi;
    private String deskripsi;
    private List<String> images; // multi gambar
    private double rating;       // default 0
    private boolean loved;       // <--- tambahan untuk favorite

    public Wisata() {
        // Firestore membutuhkan constructor kosong
    }

    public Wisata(String id, String nama, String lokasi, String deskripsi, List<String> images, double rating, boolean loved) {
        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
        this.images = images;
        this.rating = rating;
        this.loved = loved;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isLoved() { return loved; }  // bisa pakai getLoved() juga
    public void setLoved(boolean loved) { this.loved = loved; }
}
