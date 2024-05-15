package com.perpustakaan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perpustakaan.model.Mahasiswa;
import com.perpustakaan.repository.MahasiswaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MahasiswaService {

    @Autowired
    private MahasiswaRepository mahasiswaRepository;

    public boolean saveMahasiswa(Mahasiswa mahasiswa) {
        Mahasiswa result = mahasiswaRepository.save(mahasiswa);
        return result != null;
    }

    public List<Mahasiswa> getAllMahasiswa() {
        return mahasiswaRepository.findAll();
    }

    public Optional<Mahasiswa> getMahasiswaById(Long id) {
        return mahasiswaRepository.findById(id);
    }

    public boolean deleteMahasiswa(Long id) {
        if (mahasiswaRepository.existsById(id)) {
            mahasiswaRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
