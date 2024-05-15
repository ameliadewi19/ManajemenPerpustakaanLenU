package com.perpustakaan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perpustakaan.model.Buku;

@Repository
public interface BukuRepository extends JpaRepository<Buku, Long> {
    // You can add custom query methods here if needed
}