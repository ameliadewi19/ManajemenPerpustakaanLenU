package com.perpustakaan.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "mahasiswa")
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai objek untuk data Mahasiswa
 */
public class Mahasiswa {
	
	@Id
	@Column(name = "id_mahasiswa")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idMahasiswa;
	
	@Column(name = "nama_mahasiswa")
	private String namaMahasiswa;

	@Column(name = "nim")
	private String nim;
	
	@Column(name = "jurusan")
	private String jurusan;
	
	@OneToMany(mappedBy = "mahasiswa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Peminjaman> peminjamans;
	
	public Mahasiswa() {
		
	}
	
	public Mahasiswa(String namaMahasiswa, String nim, String jurusan) {
		this.namaMahasiswa = namaMahasiswa;
		this.nim = nim;
		this.jurusan = jurusan;
	}

	public long getIdMahasiswa() {
		return idMahasiswa;
	}

	public void setIdMahasiswa(long idMahasiswa) {
		this.idMahasiswa = idMahasiswa;
	}

	public String getNamaMahasiswa() {
		return namaMahasiswa;
	}

	public void setNamaMahasiswa(String namaMahasiswa) {
		this.namaMahasiswa = namaMahasiswa;
	}

	public String getNim() {
		return nim;
	}

	public void setNim(String nim) {
		this.nim = nim;
	}

	public String getJurusan() {
		return jurusan;
	}

	public void setJurusan(String jurusan) {
		this.jurusan = jurusan;
	}
}
