package com.example.tempatkita.model;

public class Wisata {
    private String nama;
    private String lokasi;
    private String gambar;
    private boolean loved;
    private int likeCount;
    private int originalIndex;

    public Wisata(String nama, String lokasi, String gambar) {
        this.nama = nama;
        this.lokasi = lokasi;
        this.gambar = gambar;
        this.loved = false;
        this.likeCount = 0; // 🔹 Awal 0
    }

    public String getNama() { return nama; }
    public String getLokasi() { return lokasi; }
    public String getGambar() { return gambar; }
    public boolean isLoved() { return loved; }
    public int getLikeCount() { return likeCount; }
    public int getOriginalIndex() { return originalIndex; }

    public void setNama(String nama) { this.nama = nama; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setGambar(String gambar) { this.gambar = gambar; }
    public void setLoved(boolean loved) { this.loved = loved; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setOriginalIndex(int originalIndex) { this.originalIndex = originalIndex; }
}
