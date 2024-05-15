package com.perpustakaan.service;

import com.perpustakaan.model.Notifikasi;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotifikasiScheduler {

    private final DendaService dendaService;

    public NotifikasiScheduler(DendaService dendaService) {
        this.dendaService = dendaService;
    }

    @Scheduled(fixedRate = 5000) // Per 5 detik
//    @Scheduled(cron = "0 0 7 * * *") // kirim setiap 24 jam sekali, pada jam 7 pagi
    public void sendDailyNotifikasi() {
        dendaService.kirimNotifikasi();
    }
}
