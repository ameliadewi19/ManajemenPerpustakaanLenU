package com.perpustakaan.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "peminjaman")
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai objek untuk data Peminjaman
 */
public class Peminjaman {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_peminjaman")
    private long idPeminjaman;

    @ManyToOne
    @JoinColumn(name = "id_buku", nullable = false)
    private Buku buku;

    @ManyToOne
    @JoinColumn(name = "id_mahasiswa", nullable = false)
    private Mahasiswa mahasiswa;

    @Column(name = "tanggal_peminjaman")
    private LocalDate tanggalPeminjaman;

    @Column(name = "batas_peminjaman")
    private LocalDate batasPeminjaman;

    @Column(name = "tanggal_pengembalian")
    private LocalDate tanggalPengembalian;

    public Peminjaman() {
    }

    public Peminjaman(Buku buku, Mahasiswa mahasiswa, LocalDate tanggalPeminjaman,
                      LocalDate batasPeminjaman, LocalDate tanggalPengembalian) {
        this.buku = buku;
        this.mahasiswa = mahasiswa;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.batasPeminjaman = batasPeminjaman;
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public long getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(long idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public Buku getBuku() {
        return buku;
    }

    public void setBuku(Buku buku) {
        this.buku = buku;
    }

    public Mahasiswa getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public LocalDate getTanggalPeminjaman() {
        return tanggalPeminjaman;
    }

    public void setTanggalPeminjaman(LocalDate tanggalPeminjaman) {
        this.tanggalPeminjaman = tanggalPeminjaman;
    }

    public LocalDate getBatasPeminjaman() {
        return batasPeminjaman;
    }

    public void setBatasPeminjaman(LocalDate batasPeminjaman) {
        this.batasPeminjaman = batasPeminjaman;
    }

    public LocalDate getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    public void setTanggalPengembalian(LocalDate tanggalPengembalian) {
        this.tanggalPengembalian = tanggalPengembalian;
    }
}
