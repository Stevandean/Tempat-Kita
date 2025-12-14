package com.example.tempatkita.model;

import java.io.Serializable;

public class Wisata implements Serializable {
    private String id;
    private String nama;
    private String lokasi;
    private String deskripsi;
    private String gambarUrl;
    private double rating;
    private boolean loved;

    public Wisata() {}

    // GETTER & SETTER
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isLoved() { return loved; }
    public void setLoved(boolean loved) { this.loved = loved; }
}
