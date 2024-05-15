package com.perpustakaan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.perpustakaan.exception.ResourceNotFoundException;
import com.perpustakaan.model.Mahasiswa;
import com.perpustakaan.service.MahasiswaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mahasiswa")
public class MahasiswaController {

    @Autowired
    private MahasiswaService mahasiswaService;

    @GetMapping("/")
    public List<Mahasiswa> getAllMahasiswa() {
        return mahasiswaService.getAllMahasiswa();
    }

    @PostMapping("/")
    public boolean createMahasiswa(@RequestBody Mahasiswa mahasiswa) {
        return mahasiswaService.saveMahasiswa(mahasiswa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mahasiswa> getMahasiswaById(@PathVariable Long id) {
        Mahasiswa mahasiswa = mahasiswaService.getMahasiswaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with id: " + id));
        return ResponseEntity.ok(mahasiswa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateMahasiswa(@PathVariable Long id, @RequestBody Mahasiswa mahasiswaDetails) {
        Mahasiswa mahasiswa = mahasiswaService.getMahasiswaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with id: " + id));

        mahasiswa.setNamaMahasiswa(mahasiswaDetails.getNamaMahasiswa());
        mahasiswa.setNim(mahasiswaDetails.getNim());
        mahasiswa.setJurusan(mahasiswaDetails.getJurusan());

        boolean updatedMahasiswa = mahasiswaService.saveMahasiswa(mahasiswa);
        return ResponseEntity.ok(updatedMahasiswa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteMahasiswa(@PathVariable Long id) {
        if (mahasiswaService.deleteMahasiswa(id)) {
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Mahasiswa not found with id: " + id);
        }
    }
}

