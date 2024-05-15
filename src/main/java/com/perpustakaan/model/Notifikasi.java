package com.perpustakaan.model;

/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi untuk objek Notifikasi
 */
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
