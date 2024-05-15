package com.perpustakaan.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "buku")
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai objek untuk data Buku
 */
public class Buku {
	
	@Id
	@Column(name = "id_buku")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idBuku;
	
	@Column(name = "judulBuku")
	private String judulBuku;

	@Column(name = "penulis")
	private String penulis;
	
	@Column(name = "kuantitas")
	private int kuantitas;
	
	@Column(name = "tempat_penyimpanan")
	private String tempatPenyimpanan;
	
	@OneToMany(mappedBy = "buku", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Peminjaman> peminjamans;
	
	public Buku() {
		
	}
	
	public Buku(String judulBuku, String penulis, int kuantitas, String tempatPenyimpanan) {
		this.judulBuku = judulBuku;
		this.penulis = penulis;
		this.kuantitas = kuantitas;
		this.tempatPenyimpanan = tempatPenyimpanan;
	}

	public long getIdBuku() {
		return idBuku;
	}

	public void setIdBuku(long idBuku) {
		this.idBuku = idBuku;
	}

	public String getJudulBuku() {
		return judulBuku;
	}

	public void setJudulBuku(String judulBuku) {
		this.judulBuku = judulBuku;
	}

	public String getPenulis() {
		return penulis;
	}

	public void setPenulis(String penulis) {
		this.penulis = penulis;
	}
	
	public int getKuantitas() {
		return kuantitas;
	}
	
	public void setKuantitas(int kuantitas) {
		this.kuantitas = kuantitas;
	}

	public String getTempatPenyimpanan() {
		return tempatPenyimpanan;
	}

	public void setTempatPenyimpanan(String tempatPenyimpanan) {
		this.tempatPenyimpanan = tempatPenyimpanan;
	}
}
