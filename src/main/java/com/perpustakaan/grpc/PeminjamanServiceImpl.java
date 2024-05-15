package com.perpustakaan.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import com.perpustakaan.exception.ResourceNotFoundException;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.Mahasiswa;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.repository.BukuRepository;
import com.perpustakaan.repository.MahasiswaRepository;
import com.perpustakaan.repository.PeminjamanRepository;
import com.perpustakaan.proto.PeminjamanOuterClass;
import com.perpustakaan.proto.PeminjamanServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.perpustakaan.proto.Common;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeminjamanServiceImpl extends PeminjamanServiceGrpc.PeminjamanServiceImplBase {

    @Autowired
    private PeminjamanRepository peminjamanRepository;
    
    @Autowired
    private BukuRepository bukuRepository;

    @Autowired
    private MahasiswaRepository mahasiswaRepository;

    @Override
    public void getAllPeminjaman(Common.Empty request, StreamObserver<PeminjamanOuterClass.PeminjamanList> responseObserver) {
        try {
            List<Peminjaman> peminjamanList = peminjamanRepository.findAll();

            PeminjamanOuterClass.PeminjamanList.Builder peminjamanListBuilder = PeminjamanOuterClass.PeminjamanList.newBuilder();
            for (Peminjaman peminjaman : peminjamanList) {
                PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                        .setIdPeminjaman(peminjaman.getIdPeminjaman())
                        .setIdBuku(peminjaman.getBuku().getIdBuku())
                        .setIdMahasiswa(peminjaman.getMahasiswa().getIdMahasiswa())
                        .setTanggalPeminjaman(peminjaman.getTanggalPeminjaman().toString())
                        .setBatasPeminjaman(peminjaman.getBatasPeminjaman().toString())
                        .setTanggalPengembalian(peminjaman.getTanggalPengembalian() != null ? peminjaman.getTanggalPengembalian().toString() : "")
                        .build();

                peminjamanListBuilder.addPeminjamans(grpcPeminjaman);
            }

            responseObserver.onNext(peminjamanListBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void createPeminjaman(PeminjamanOuterClass.CreatePeminjamanRequest request, StreamObserver<PeminjamanOuterClass.Peminjaman> responseObserver) {
        try {
        	Buku buku = bukuRepository.findById(request.getIdBuku())
                    .orElseThrow(() -> new ResourceNotFoundException("Buku not found with ID: " + request.getIdBuku()));

            Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getIdMahasiswa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with ID: " + request.getIdMahasiswa()));

            Peminjaman newPeminjaman = new Peminjaman();
            newPeminjaman.setBuku(buku);
            newPeminjaman.setMahasiswa(mahasiswa);
            newPeminjaman.setTanggalPeminjaman(LocalDate.parse(request.getTanggalPeminjaman()));
            newPeminjaman.setBatasPeminjaman(LocalDate.parse(request.getBatasPeminjaman()));

            Peminjaman savedPeminjaman = peminjamanRepository.save(newPeminjaman);

            PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                    .setIdPeminjaman(savedPeminjaman.getIdPeminjaman())
                    .setIdBuku(savedPeminjaman.getBuku().getIdBuku())
                    .setIdMahasiswa(savedPeminjaman.getMahasiswa().getIdMahasiswa())
                    .setTanggalPeminjaman(savedPeminjaman.getTanggalPeminjaman().toString())
                    .setBatasPeminjaman(savedPeminjaman.getBatasPeminjaman().toString())
                    .setTanggalPengembalian(savedPeminjaman.getTanggalPengembalian() != null ? savedPeminjaman.getTanggalPengembalian().toString() : "")
                    .build();

            responseObserver.onNext(grpcPeminjaman);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void getPeminjamanById(PeminjamanOuterClass.PeminjamanIdRequest request, StreamObserver<PeminjamanOuterClass.Peminjaman> responseObserver) {
        long idPeminjaman = request.getIdPeminjaman();

        try {
            Peminjaman peminjaman = peminjamanRepository.findById(idPeminjaman)
                    .orElseThrow(() -> new ResourceNotFoundException("Peminjaman not found with ID: " + idPeminjaman));

            PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                    .setIdPeminjaman(peminjaman.getIdPeminjaman())
                    .setIdBuku(peminjaman.getBuku().getIdBuku())
                    .setIdMahasiswa(peminjaman.getMahasiswa().getIdMahasiswa())
                    .setTanggalPeminjaman(peminjaman.getTanggalPeminjaman().toString())
                    .setBatasPeminjaman(peminjaman.getBatasPeminjaman().toString())
                    .setTanggalPengembalian(peminjaman.getTanggalPengembalian() != null ? peminjaman.getTanggalPengembalian().toString() : "")
                    .build();

            responseObserver.onNext(grpcPeminjaman);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void updatePeminjaman(PeminjamanOuterClass.Peminjaman request, StreamObserver<PeminjamanOuterClass.Peminjaman> responseObserver) {
        try {
        	Buku buku = bukuRepository.findById(request.getIdBuku())
                    .orElseThrow(() -> new ResourceNotFoundException("Buku not found with ID: " + request.getIdBuku()));

            Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getIdMahasiswa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with ID: " + request.getIdMahasiswa()));

            Peminjaman existingPeminjaman = peminjamanRepository.findById(request.getIdPeminjaman())
                    .orElseThrow(() -> new IllegalArgumentException("Peminjaman not found with id: " + request.getIdPeminjaman()));

            existingPeminjaman.setBuku(buku);
            existingPeminjaman.setMahasiswa(mahasiswa);
            existingPeminjaman.setTanggalPeminjaman(LocalDate.parse(request.getTanggalPeminjaman()));
            existingPeminjaman.setBatasPeminjaman(LocalDate.parse(request.getBatasPeminjaman()));

            if (!request.getTanggalPengembalian().isEmpty()) {
                existingPeminjaman.setTanggalPengembalian(LocalDate.parse(request.getTanggalPengembalian()));
            }

            Peminjaman updatedPeminjaman = peminjamanRepository.save(existingPeminjaman);

            PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                    .setIdPeminjaman(updatedPeminjaman.getIdPeminjaman())
                    .setIdBuku(updatedPeminjaman.getBuku().getIdBuku())
                    .setIdMahasiswa(updatedPeminjaman.getMahasiswa().getIdMahasiswa())
                    .setTanggalPeminjaman(updatedPeminjaman.getTanggalPeminjaman().toString())
                    .setBatasPeminjaman(updatedPeminjaman.getBatasPeminjaman().toString())
                    .setTanggalPengembalian(updatedPeminjaman.getTanggalPengembalian() != null ? updatedPeminjaman.getTanggalPengembalian().toString() : "")
                    .build();

            responseObserver.onNext(grpcPeminjaman);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error updating peminjaman: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deletePeminjaman(PeminjamanOuterClass.PeminjamanIdRequest request, StreamObserver<Common.Empty> responseObserver) {
        try {
            Peminjaman peminjamanToDelete = peminjamanRepository.findById(request.getIdPeminjaman())
                    .orElseThrow(() -> new IllegalArgumentException("Peminjaman not found with id: " + request.getIdPeminjaman()));

            peminjamanRepository.delete(peminjamanToDelete);

            responseObserver.onNext(Common.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error deleting peminjaman: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }
}
