package com.example.tempatkita.model;

public class Wisata {
    private String nama;
    private String lokasi;
    private int gambar;
    private boolean loved;

    public Wisata(String nama, String lokasi, int gambar) {
        this.nama = nama;
        this.lokasi = lokasi;
        this.gambar = gambar;
        this.loved = false;
    }

    public String getNama() { return nama; }
    public String getLokasi() { return lokasi; }
    public int getGambar() { return gambar; }
    public boolean isLoved() { return loved; }

    public void setNama(String nama) { this.nama = nama; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setGambar(int gambar) { this.gambar = gambar; }
    public void setLoved(boolean loved) { this.loved = loved; }
}
