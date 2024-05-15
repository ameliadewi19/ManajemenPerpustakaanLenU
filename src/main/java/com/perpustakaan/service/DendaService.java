package com.perpustakaan.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.perpustakaan.model.Notifikasi;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.repository.PeminjamanRepository;

@Service
public class DendaService {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void kirimNotifikasi() {
        List<Peminjaman> peminjamans = peminjamanRepository.findOverduePeminjaman(); // Metode untuk mencari peminjaman yang terlambat

        for (Peminjaman peminjaman : peminjamans) {
            // Hitung denda sesuai aturan yang telah ditentukan
            int denda = hitungDenda(peminjaman);

            // Kirim notifikasi ke pengguna melalui Websocket
            messagingTemplate.convertAndSend("/topic/denda", "Anda memiliki denda sebesar " + denda + " untuk peminjaman dengan ID " + peminjaman.getIdPeminjaman());

            // Kirim notifikasi kepada admin
            sendNotificationToAdmin("Peminjaman dengan ID " + peminjaman.getIdPeminjaman() + " belum dikembalikan melewati batas pengembalian.\nDenda: " + denda);
        }
    }

    private int hitungDenda(Peminjaman peminjaman) {
    	int totalDenda = 0;
        int tambahanDenda = 1000;
        
        LocalDate tanggalPengembalian = peminjaman.getTanggalPengembalian();
        LocalDate batasPeminjaman = peminjaman.getBatasPeminjaman();
        
        long selisihHari = ChronoUnit.DAYS.between(batasPeminjaman, tanggalPengembalian);

        for (int i = 1; i <= selisihHari; i++) {
            totalDenda += tambahanDenda;
            if (i % 2 == 0) { // Jika sudah 2 hari terlewati, tambahan denda bertambah
                tambahanDenda += 1000;
            }
        }
        
		return totalDenda;
    }

    private void sendNotificationToAdmin(String pesan) {
        Notifikasi notifikasi = new Notifikasi();
        notifikasi.setPesan(pesan);
        
        System.out.println("Notifikasi: " + pesan);
    }
}