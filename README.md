# Manajemen Perpustakaan LenU

Project ini merupakan layanan untuk mendokumentasikan history peminjaman pada perpustakaan. Mahasiswa hanya bisa meminjam 10 buku di bulan yang sama. Sistem akan memberikan notifikasi setiap harinya, untuk menginformasikan denda keterlambatan.\n
Ada aturan denda keterlambatan pengembalian dengan ketentuan sebagai berikut: 
- Terlambat sampai dengan 2 hari pertama dikenakan denda Rp 1.000/hari untuk 1 buku.
- Keterlambatan selanjutnya denda naik Rp 1.000/hari dari denda sebelumnya dalam setiap 2 hari keterlambatan. Misal keterlambatan hari ke-3 dan ke-4 denda menjadi Rp 2.000/hari untuk 1 
buku.

## Build With
Projek ini dibangun menggunakan teknologi berikut:
 <ul>
    <li>Java Springboot</li>
    <li>Postgre</li>
 </ul>
 
# Getting Started
## Pre-requisites
Sebelum menjalankan project ini, perlu disiapkan environment yang sesuai.
<ul>
 <li>Java 17+</li>
 <li>Apache Maven 3.8.0+</li>
</ul>

## Struture Project Test
Berikut ini merupakan struktur kode beserta penjelasannya
```
{nama_proyek}
 src
   main
     java
       com
         perpustakaan
           configuration
           exception
           grpc
           model
           repository
           service
     proto
     resources
   test
```
<ul>
 <li>package configuration berisi pengaturan dari aplikasi, disini digunakan untuk konfigurasi web socket</li>
 <li>package exception digunakan untuk custom pesan kesalahan</li>
 <li>package grpc berisi implementasi dari gRPC</li>
 <li>package model berisi class yang digunakan sebagai obejct dari struktur data yang digunakan pada pengujian</li>
 <li>package repository merupakan antarmuka database menggunakan JPA </li>
 <li>package service berisi logika bisnis dari aplikasi, disini digunakan untuk notifikasi denda keterlambatan</li>
</ul>

## Run Projek
1. Clone repository dengan git
   ```
   git clone https://github.com/ameliadewi19/ManajemenPerpustakaanLenU
   ```
2. Jalankan perintah berikut untuk menginstall artifak yang didefinisikan
   ```
   mvn clean install
   ```
3. Jalankan perintah berikut untuk menjalankan projek
   ```
   mvn spring-boot:run
   ```
4. Contoh test api menggunakan BloomRPC
   ![image](https://github.com/ameliadewi19/ManajemenPerpustakaanLenU/assets/95133748/3c2aff83-0382-45d0-a153-8cfbfccfae56)

## API gRPC
Berikut ini merupakan list api yang tersedia:

#### Mahasiswa
```
1. GetAllMahasiswa
2. CreateMahasiswa
3. GetMahasiswaById
4. UpdateMahasiswa
5. DeleteMahasiswa
```
#### Buku
```
1. GetAllBuku
2. CreateBuku
3. GetBukuById
4. UpdateBuku
5. DeleteBuku
```
#### Peminjaman
```
1. GetPeminjaman
2. CreatePeminjaman
3. GetPeminjamanById
4. UpdatePeminjaman
5. DeletePeminjaman
```
#### Pengembalian
```
1. CreatePengembalian
```

## Author
[Amelia Dewi Agustiani](https://github.com/ameliadewi19) 
_Mahasiswa D4 Teknik Informatika Politeknik Negeri Bandung_
