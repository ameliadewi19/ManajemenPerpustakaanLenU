package com.perpustakaan.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import com.perpustakaan.exception.ResourceNotFoundException;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.repository.BukuRepository;
import com.perpustakaan.repository.PeminjamanRepository;
import com.perpustakaan.proto.PengembalianOuterClass;
import com.perpustakaan.proto.PengembalianServiceGrpc;
import com.perpustakaan.proto.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PengembalianServiceImpl extends PengembalianServiceGrpc.PengembalianServiceImplBase {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private BukuRepository bukuRepository;

    @Override
    public void createPengembalian(PengembalianOuterClass.CreatePengembalianRequest request, StreamObserver<PengembalianOuterClass.Pengembalian> responseObserver) {
        try {
            // Ambil ID peminjaman dari request
            long idPeminjaman = request.getIdPeminjaman();

            // Temukan peminjaman berdasarkan ID
            Peminjaman peminjaman = peminjamanRepository.findById(idPeminjaman)
                    .orElseThrow(() -> new ResourceNotFoundException("Peminjaman not found with ID: " + idPeminjaman));

            // Periksa apakah peminjaman sudah dikembalikan sebelumnya
            if (peminjaman.getTanggalPengembalian() != null) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Peminjaman sudah dikembalikan").asRuntimeException());
            } else {
                // Set tanggal pengembalian dengan tanggal saat ini
                peminjaman.setTanggalPengembalian(LocalDate.now());

                // Update status peminjaman di repository
                peminjamanRepository.save(peminjaman);

                // Kembalikan satu unit buku ke stok
                Buku buku = peminjaman.getBuku();
                buku.setKuantitas(buku.getKuantitas() + 1);
                bukuRepository.save(buku);

                // Kirim respon berhasil
                PengembalianOuterClass.Pengembalian pengembalianResponse = PengembalianOuterClass.Pengembalian.newBuilder()
                        .setIdPeminjaman(peminjaman.getIdPeminjaman())
                        .setTanggalPengembalian(LocalDate.now().toString())
                        .build();
                responseObserver.onNext(pengembalianResponse);
                responseObserver.onCompleted();
            }
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }


}
