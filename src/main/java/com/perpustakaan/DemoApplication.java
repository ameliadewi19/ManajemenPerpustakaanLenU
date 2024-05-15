package com.perpustakaan;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.perpustakaan.grpc.MahasiswaServiceImpl;
import com.perpustakaan.grpc.BukuServiceImpl;
import com.perpustakaan.grpc.PeminjamanServiceImpl;
import com.perpustakaan.grpc.PengembalianServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini merupakan class main 
 */
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private MahasiswaServiceImpl mahasiswaServiceImpl;
    
    @Autowired
    private BukuServiceImpl bukuServiceImpl;
    
    @Autowired
    private PeminjamanServiceImpl peminjamanServiceImpl;
    
    @Autowired
    private PengembalianServiceImpl pengembalianServiceImpl;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(mahasiswaServiceImpl)
                .addService(bukuServiceImpl)
                .addService(peminjamanServiceImpl)
                .addService(pengembalianServiceImpl)
                .build()
                .start();

        System.out.println("Server started, listening on " + server.getPort());
        server.awaitTermination();
    }
}