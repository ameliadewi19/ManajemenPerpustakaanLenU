package com.perpustakaan;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.perpustakaan.grpc.MahasiswaServiceImpl;
import com.perpustakaan.grpc.BukuServiceImpl;
import com.perpustakaan.grpc.PeminjamanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplicationTests implements CommandLineRunner {

    @Autowired
    private MahasiswaServiceImpl mahasiswaServiceImpl;
    
    @Autowired
    private BukuServiceImpl bukuServiceImpl;
    
    @Autowired
    private PeminjamanServiceImpl peminjamanServiceImpl;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplicationTests.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(mahasiswaServiceImpl)
                .addService(bukuServiceImpl)
                .addService(peminjamanServiceImpl)
                .build()
                .start();

        System.out.println("Server started, listening on " + server.getPort());
        server.awaitTermination();
    }
}
