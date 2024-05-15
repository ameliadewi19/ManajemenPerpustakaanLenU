package com.perpustakaan.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import com.perpustakaan.exception.ResourceNotFoundException;
import com.perpustakaan.model.Mahasiswa;
import com.perpustakaan.repository.MahasiswaRepository;
import com.perpustakaan.proto.MahasiswaOuterClass;
import com.perpustakaan.proto.MahasiswaServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.perpustakaan.proto.Common;

import java.util.List;
import java.util.stream.Collectors;

@Service
/*
 * PIC : Amelia Dewi Agustiani
 * Tanggal_Dibuat : 15/05/2024
 * Tujuan : Class ini berfungsi sebagai implementasi dari gRPC untuk data Mahasiswa
 */
public class MahasiswaServiceImpl extends MahasiswaServiceGrpc.MahasiswaServiceImplBase {

    @Autowired
    private MahasiswaRepository mahasiswaRepository; 

    @Override
    public void getAllMahasiswa(Common.Empty request, StreamObserver<MahasiswaOuterClass.MahasiswaList> responseObserver) {
        try {
            List<Mahasiswa> mahasiswaList = mahasiswaRepository.findAll(); 

            MahasiswaOuterClass.MahasiswaList.Builder mahasiswaListBuilder = MahasiswaOuterClass.MahasiswaList.newBuilder();
            for (Mahasiswa mahasiswa : mahasiswaList) {
                MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                        .setIdMahasiswa(mahasiswa.getIdMahasiswa())
                        .setNamaMahasiswa(mahasiswa.getNamaMahasiswa())
                        .setNim(mahasiswa.getNim())
                        .setJurusan(mahasiswa.getJurusan())
                        .build();

                mahasiswaListBuilder.addMahasiswas(grpcMahasiswa);
            }

            responseObserver.onNext(mahasiswaListBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void createMahasiswa(MahasiswaOuterClass.CreateMahasiswaRequest request, StreamObserver<MahasiswaOuterClass.Mahasiswa> responseObserver) {
        try {
            // Mendapatkan data dari request
            String namaMahasiswa = request.getNamaMahasiswa();
            String nim = request.getNim();
            String jurusan = request.getJurusan();

            // Membuat objek Mahasiswa
            Mahasiswa newMahasiswa = new Mahasiswa();
            newMahasiswa.setNamaMahasiswa(namaMahasiswa);
            newMahasiswa.setNim(nim);
            newMahasiswa.setJurusan(jurusan);

            // Simpan Mahasiswa ke database menggunakan repositori JPA
            Mahasiswa savedMahasiswa = mahasiswaRepository.save(newMahasiswa);

            // Mengembalikan response berupa Mahasiswa yang telah disimpan
            MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                    .setIdMahasiswa(savedMahasiswa.getIdMahasiswa())
                    .setNamaMahasiswa(savedMahasiswa.getNamaMahasiswa())
                    .setNim(savedMahasiswa.getNim())
                    .setJurusan(savedMahasiswa.getJurusan())
                    .build();

            responseObserver.onNext(grpcMahasiswa);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Mengirimkan respon error jika terjadi kesalahan
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void getMahasiswaById(MahasiswaOuterClass.MahasiswaIdRequest request, StreamObserver<MahasiswaOuterClass.Mahasiswa> responseObserver) {
        long idMahasiswa = request.getIdMahasiswa(); 

        try {
            Mahasiswa mahasiswa = mahasiswaRepository.findById(idMahasiswa)
                    .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with ID: " + idMahasiswa));

            MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                    .setIdMahasiswa(mahasiswa.getIdMahasiswa())
                    .setNamaMahasiswa(mahasiswa.getNamaMahasiswa())
                    .setNim(mahasiswa.getNim())
                    .setJurusan(mahasiswa.getJurusan())
                    .build();

            responseObserver.onNext(grpcMahasiswa);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void updateMahasiswa(MahasiswaOuterClass.Mahasiswa request, StreamObserver<MahasiswaOuterClass.Mahasiswa> responseObserver) {
        try {
        	Mahasiswa existingMahasiswa = mahasiswaRepository.findById(request.getIdMahasiswa())
                    .orElseThrow(() -> new IllegalArgumentException("Mahasiswa not found with id: " + request.getIdMahasiswa()));

            existingMahasiswa.setNamaMahasiswa(request.getNamaMahasiswa());
            existingMahasiswa.setNim(request.getNim());
            existingMahasiswa.setJurusan(request.getJurusan());

            Mahasiswa updatedMahasiswa = mahasiswaRepository.save(existingMahasiswa);

            MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                    .setIdMahasiswa(updatedMahasiswa.getIdMahasiswa())
                    .setNamaMahasiswa(updatedMahasiswa.getNamaMahasiswa())
                    .setNim(updatedMahasiswa.getNim())
                    .setJurusan(updatedMahasiswa.getJurusan())
                    .build();

            responseObserver.onNext(grpcMahasiswa);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
             System.err.println("Error updating mahasiswa: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteMahasiswa(MahasiswaOuterClass.MahasiswaIdRequest request, StreamObserver<Common.Empty> responseObserver) {
        try {
            Mahasiswa mahasiswaToDelete = mahasiswaRepository.findById(request.getIdMahasiswa())
                    .orElseThrow(() -> new IllegalArgumentException("Mahasiswa not found with id: " + request.getIdMahasiswa()));

            mahasiswaRepository.delete(mahasiswaToDelete);

            responseObserver.onNext(Common.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error deleting mahasiswa: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }
}
