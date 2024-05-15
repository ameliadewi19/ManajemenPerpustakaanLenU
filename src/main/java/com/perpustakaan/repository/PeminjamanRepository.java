package com.perpustakaan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.perpustakaan.model.Peminjaman;

@Repository
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai repository menggunakan JPA untuk data Peminjaman
 */
public interface PeminjamanRepository extends JpaRepository<Peminjaman, Long>{
	@Query("SELECT COUNT(p) FROM Peminjaman p WHERE p.mahasiswa.id = :mahasiswaId AND MONTH(p.tanggalPeminjaman) = :bulanPeminjaman")
    int countByMahasiswaIdAndBulanPeminjaman(@Param("mahasiswaId") long mahasiswaId, @Param("bulanPeminjaman") int bulanPeminjaman);
	
	@Query("SELECT p FROM Peminjaman p WHERE p.tanggalPengembalian > p.batasPeminjaman")
    List<Peminjaman> findOverduePeminjaman();
}
