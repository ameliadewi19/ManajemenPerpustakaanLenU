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
import com.perpustakaan.proto.BukuOuterClass;
import com.perpustakaan.proto.BukuServiceGrpc;
import com.perpustakaan.proto.MahasiswaOuterClass;
import com.perpustakaan.proto.MahasiswaServiceGrpc;
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
                BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                        .setIdBuku(peminjaman.getBuku().getIdBuku())
                        .setJudulBuku(peminjaman.getBuku().getJudulBuku())
                        .setPenulis(peminjaman.getBuku().getPenulis())
                        .setKuantitas(peminjaman.getBuku().getKuantitas())
                        .setTempatPenyimpanan(peminjaman.getBuku().getTempatPenyimpanan())
                        .build();

                MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                        .setIdMahasiswa(peminjaman.getMahasiswa().getIdMahasiswa())
                        .setNamaMahasiswa(peminjaman.getMahasiswa().getNamaMahasiswa())
                        .setNim(peminjaman.getMahasiswa().getNim())
                        .setJurusan(peminjaman.getMahasiswa().getJurusan())
                        .build();

                PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                        .setIdPeminjaman(peminjaman.getIdPeminjaman())
                        .setBuku(grpcBuku)
                        .setMahasiswa(grpcMahasiswa)
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
        	// memeriksa jumlah peminjaman mahasiswa dalam satu bulan
        	long idMahasiswa = request.getIdMahasiswa();
        	int jumlahPeminjamanMahasiswa = peminjamanRepository.countByMahasiswaIdAndBulanPeminjaman(idMahasiswa, LocalDate.parse(request.getTanggalPeminjaman()).getMonthValue());
        	
        	// cek apakah mahasiswa sudah meminjam lebih dari 10 kali dalam satu bulan
        	if (jumlahPeminjamanMahasiswa>10) {
        		responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Jumlah peminjaman pada bulan yang sama sudah melebihi batas").asRuntimeException());
        	} else {
	        	// memeriksa stok kesediaan buku
	            long idBuku = request.getIdBuku();
	            Buku buku = bukuRepository.findById(idBuku).orElse(null);
	
	            // jika stok tersedia maka dapat meminjam
	            if (buku != null && buku.getKuantitas() > 0) {
	                buku.setKuantitas(buku.getKuantitas() - 1);
	                bukuRepository.save(buku);
	
	                Peminjaman newPeminjaman = new Peminjaman();
	                
	                Mahasiswa mahasiswaMeminjam = new Mahasiswa();
	                
	                mahasiswaMeminjam.setIdMahasiswa(request.getIdMahasiswa());
	                newPeminjaman.setBuku(buku);
	                newPeminjaman.setMahasiswa(mahasiswaMeminjam);
	                newPeminjaman.setTanggalPeminjaman(LocalDate.parse(request.getTanggalPeminjaman()));
	                newPeminjaman.setBatasPeminjaman(LocalDate.parse(request.getBatasPeminjaman()));
	
	                Peminjaman savedPeminjaman = peminjamanRepository.save(newPeminjaman);
	
	                PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
	                	    .setIdPeminjaman(savedPeminjaman.getIdPeminjaman())
	                	    .setBuku(BukuOuterClass.Buku.newBuilder().setIdBuku(savedPeminjaman.getBuku().getIdBuku()).build())
	                	    .setMahasiswa(MahasiswaOuterClass.Mahasiswa.newBuilder().setIdMahasiswa(savedPeminjaman.getMahasiswa().getIdMahasiswa()).build())
	                	    .setTanggalPeminjaman(savedPeminjaman.getTanggalPeminjaman().toString())
	                	    .setBatasPeminjaman(savedPeminjaman.getBatasPeminjaman().toString())
	                	    .setTanggalPengembalian(savedPeminjaman.getTanggalPengembalian() != null ? savedPeminjaman.getTanggalPengembalian().toString() : "")
	                	    .build();
	
	                responseObserver.onNext(grpcPeminjaman);
	                responseObserver.onCompleted();
	            } else {
	                // Kirim pesan kesalahan bahwa stok buku tidak mencukupi
	                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Stok buku tidak mencukupi").asRuntimeException());
	            }
        	}
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

            BukuOuterClass.Buku grpcBuku = BukuOuterClass.Buku.newBuilder()
                    .setIdBuku(peminjaman.getBuku().getIdBuku())
                    .setJudulBuku(peminjaman.getBuku().getJudulBuku())
                    .setPenulis(peminjaman.getBuku().getPenulis())
                    .setKuantitas(peminjaman.getBuku().getKuantitas())
                    .setTempatPenyimpanan(peminjaman.getBuku().getTempatPenyimpanan())
                    .build();

            MahasiswaOuterClass.Mahasiswa grpcMahasiswa = MahasiswaOuterClass.Mahasiswa.newBuilder()
                    .setIdMahasiswa(peminjaman.getMahasiswa().getIdMahasiswa())
                    .setNamaMahasiswa(peminjaman.getMahasiswa().getNamaMahasiswa())
                    .setNim(peminjaman.getMahasiswa().getNim())
                    .setJurusan(peminjaman.getMahasiswa().getJurusan())
                    .build();

            PeminjamanOuterClass.Peminjaman grpcPeminjaman = PeminjamanOuterClass.Peminjaman.newBuilder()
                    .setIdPeminjaman(peminjaman.getIdPeminjaman())
                    .setBuku(grpcBuku)
                    .setMahasiswa(grpcMahasiswa)
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
            Buku buku = bukuRepository.findById(request.getBuku().getIdBuku())
                    .orElseThrow(() -> new ResourceNotFoundException("Buku not found with ID: " + request.getBuku().getIdBuku()));

            Mahasiswa mahasiswa = mahasiswaRepository.findById(request.getMahasiswa().getIdMahasiswa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with ID: " + request.getMahasiswa().getIdMahasiswa()));

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
                    .setBuku(request.getBuku())
                    .setMahasiswa(request.getMahasiswa())
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
