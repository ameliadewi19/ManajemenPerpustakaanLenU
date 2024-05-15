package com.perpustakaan.model;

public class Notifikasi {
    private String pesan;
    
    public Notifikasi() { }

    public Notifikasi(String pesan) {
        this.pesan = pesan;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
}
