package com.perpustakaan.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import com.perpustakaan.exception.ResourceNotFoundException;
import com.perpustakaan.model.Buku;
import com.perpustakaan.repository.BukuRepository;
import com.perpustakaan.proto.BukuOuterClass;
import com.perpustakaan.proto.BukuServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.perpustakaan.proto.Common;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BukuServiceImpl extends BukuServiceGrpc.BukuServiceImplBase {

    @Autowired
    private BukuRepository bukuRepository; 

    @Override
    public void getAllBuku(Common.Empty request, StreamObserver<BukuOuterClass.BukuList> responseObserver) {
        try {
            List<Buku> bukuList = bukuRepository.findAll(); 

            BukuOuterClass.BukuList.Builder bukuListBuilder = BukuOuterClass.BukuList.newBuilder();
            for (Buku buku : bukuList) {
                BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                        .setIdBuku(buku.getIdBuku())
                        .setJudulBuku(buku.getJudulBuku())
                        .setPenulis(buku.getPenulis())
                        .setKuantitas(buku.getKuantitas())
                        .setTempatPenyimpanan(buku.getTempatPenyimpanan())
                        .build();

                bukuListBuilder.addBukus(grpcBuku);
            }

            responseObserver.onNext(bukuListBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void createBuku(BukuOuterClass.Buku request, StreamObserver<BukuOuterClass.Buku> responseObserver) {
        try {
            Buku newBuku = new Buku();
            newBuku.setJudulBuku(request.getJudulBuku());
            newBuku.setPenulis(request.getPenulis());
            newBuku.setKuantitas((int) request.getKuantitas());
            newBuku.setTempatPenyimpanan(request.getTempatPenyimpanan());

            Buku savedBuku = bukuRepository.save(newBuku);

            BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                    .setIdBuku(savedBuku.getIdBuku())
                    .setJudulBuku(savedBuku.getJudulBuku())
                    .setPenulis(savedBuku.getPenulis())
                    .setKuantitas(savedBuku.getKuantitas())
                    .setTempatPenyimpanan(savedBuku.getTempatPenyimpanan())
                    .build();

            responseObserver.onNext(grpcBuku);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void getBukuById(BukuOuterClass.BukuIdRequest request, StreamObserver<BukuOuterClass.Buku> responseObserver) {
        long idBuku = request.getIdBuku(); 

        try {
            Buku buku = bukuRepository.findById(idBuku)
                    .orElseThrow(() -> new ResourceNotFoundException("Buku not found with ID: " + idBuku));

            BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                    .setIdBuku(buku.getIdBuku())
                    .setJudulBuku(buku.getJudulBuku())
                    .setPenulis(buku.getPenulis())
                    .setKuantitas(buku.getKuantitas())
                    .setTempatPenyimpanan(buku.getTempatPenyimpanan())
                    .build();

            responseObserver.onNext(grpcBuku);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void updateBuku(BukuOuterClass.Buku request, StreamObserver<BukuOuterClass.Buku> responseObserver) {
        try {
            Buku existingBuku = bukuRepository.findById(request.getIdBuku())
                    .orElseThrow(() -> new IllegalArgumentException("Buku not found with id: " + request.getIdBuku()));

            existingBuku.setJudulBuku(request.getJudulBuku());
            existingBuku.setJudulBuku(request.getJudulBuku());
            existingBuku.setPenulis(request.getPenulis());
            existingBuku.setKuantitas((int) request.getKuantitas());
            existingBuku.setTempatPenyimpanan(request.getTempatPenyimpanan());

            Buku updatedBuku = bukuRepository.save(existingBuku);

            BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                    .setIdBuku(updatedBuku.getIdBuku())
                    .setJudulBuku(updatedBuku.getJudulBuku())
                    .setPenulis(updatedBuku.getPenulis())
                    .setKuantitas(updatedBuku.getKuantitas())
                    .setTempatPenyimpanan(updatedBuku.getTempatPenyimpanan())
                    .build();

            responseObserver.onNext(grpcBuku);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error updating buku: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteBuku(BukuOuterClass.BukuIdRequest request, StreamObserver<Common.Empty> responseObserver) {
        try {
            Buku bukuToDelete = bukuRepository.findById(request.getIdBuku())
                    .orElseThrow(() -> new IllegalArgumentException("Buku not found with id: " + request.getIdBuku()));

            bukuRepository.delete(bukuToDelete);
            
            responseObserver.onNext(Common.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error deleting buku: " + e.getMessage());
            e.printStackTrace();

            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }
}
