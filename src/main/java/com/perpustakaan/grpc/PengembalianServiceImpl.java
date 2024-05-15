package com.perpustakaan.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
                    .orElseThrow(() -> new ResourceNotFoundException("Tidak ada peminjaman dengan ID: " + idPeminjaman));

            // Periksa apakah peminjaman sudah dikembalikan sebelumnya
            if (peminjaman.getTanggalPengembalian() != null) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Peminjaman sudah dikembalikan").asRuntimeException());
            } else {
                // Set tanggal pengembalian dengan tanggal yang diinputkan
                LocalDate tanggalPengembalian = LocalDate.parse(request.getTanggalPengembalian());

                // Validasi tanggal pengembalian tidak boleh sebelum tanggal peminjaman
                LocalDate tanggalPeminjaman = peminjaman.getTanggalPeminjaman();
                if (tanggalPengembalian.isBefore(tanggalPeminjaman)) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Tanggal pengembalian tidak valid").asRuntimeException());
                    return;
                }
                // Update status peminjaman di repository
                peminjaman.setTanggalPengembalian(tanggalPengembalian);
                peminjamanRepository.save(peminjaman);

                // Kembalikan satu unit buku ke stok
                Buku buku = peminjaman.getBuku();
                buku.setKuantitas(buku.getKuantitas() + 1);
                bukuRepository.save(buku);

                // Kirim respon berhasil
                PengembalianOuterClass.Pengembalian pengembalianResponse = PengembalianOuterClass.Pengembalian.newBuilder()
                        .setIdPeminjaman(peminjaman.getIdPeminjaman())
                        .setTanggalPengembalian(tanggalPengembalian.toString())
                        .build();
                responseObserver.onNext(pengembalianResponse);
                responseObserver.onCompleted();
            }
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }


}
