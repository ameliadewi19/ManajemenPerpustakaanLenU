package com.perpustakaan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perpustakaan.model.Buku;

@Repository
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai repository menggunakan JPA untuk data Buku
 */
public interface BukuRepository extends JpaRepository<Buku, Long> {
    
}