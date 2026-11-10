# Sistem Manajemen Antrean Prioritas dan Pemeriksaan Pasien Rawat Jalan pada Klinik Pratama Berbasis Java GUI

Aplikasi desktop berbasis **Java Swing (GUI)** untuk mengelola administrasi pendaftaran pasien rawat jalan, jadwal serta kuota praktik dokter, dan sistem pemanggilan antrean pemeriksaan berbasis prioritas (**Reguler** & **Prioritas**) pada Klinik Pratama. Proyek ini dikembangkan untuk memenuhi tugas **Final Project Praktikum Pemrograman Berorientasi Objek (PBO)** di Politeknik Elektronika Negeri Surabaya (PENS) PSDKU Lamongan.

---

## Fitur Utama Aplikasi

1. **Kelola Data Pasien (CRUD):** Menambahkan pasien baru, menampilkan daftar pasien pada tabel interaktif (`JTable`), mengubah data pasien, dan menghapus data pasien melalui **Tab Data Pasien**.
2. **Kelola Data Dokter & Jadwal (CRUD):** Mengelola data dokter beserta poli, jadwal praktik, dan batas kuota pasien harian melalui **Tab Data Dokter**.
3. **Pencarian Data Cepat (*Search Bar*):** Memfilter dan mencari data pasien (berdasarkan ID atau Nama) serta data dokter (berdasarkan ID, Nama, atau Poli) secara langsung pada tabel GUI.
4. **Registrasi Antrean Otomatis (Reguler & Prioritas):**
   - Mengecek ketersediaan sisa kuota dokter secara otomatis sebelum mendaftarkan antrean.
   - Otomatis menerbitkan **Tiket Prioritas (`P-00x`)** bagi pasien lansia ($\ge 60$ tahun), ibu hamil, atau kondisi darurat, serta **Tiket Reguler (`R-00x`)** bagi pasien umum.
5. **Pemanggilan Antrean Berbasis Polimorfisme:** Tombol **Panggil Berikutnya** secara otomatis mendahulukan tiket berstatus `MENUNGGU` dari kategori **Prioritas** sebelum memanggil kategori **Reguler**, serta menampilkan nomor panggilan aktif pada layar utama.
6. **Manajemen Siklus Status Antrean:** Mengubah status antrean mulai dari `MENUNGGU` $\rightarrow$ `DIPERIKSA` $\rightarrow$ `SELESAI`, atau `BATAL` (otomatis mengembalikan sisa kuota dokter).
7. **Validasi Input & *Exception Handling*:** Menangani input kosong maupun kesalahan format angka (seperti input huruf pada kolom umur atau kuota) menggunakan blok `try-catch` dan menampilkan dialog peringatan `JOptionPane` tanpa menghentikan program.
8. **Penyimpanan Permanen (*File Handling* CSV):** Menyimpan dan memuat ulang seluruh data pasien, dokter, dan antrean secara otomatis menggunakan tiga file CSV (`pasien.csv`, `dokter.csv`, dan `antrean.csv`).

---

## Penerapan Konsep OOP (Object-Oriented Programming)

Aplikasi ini dirancang secara modular menggunakan **9 tipe (class/interface)** yang menerapkan seluruh spesifikasi OOP:

| No | Konsep OOP | Implementasi pada Kode Program |
| :--- | :--- | :--- |
| **1** | **Class & Object** | Terdiri atas 9 tipe yang saling berinteraksi: `Person`, `Patient`, `Doctor`, `QueueTicket`, `RegularTicket`, `PriorityTicket`, `Storable`, `ClinicService`, dan `MainFrameGUI`. |
| **2** | **Constructor** | Seluruh kelas entitas menggunakan *constructor* berparameter dan memanggil `super(...)` untuk menginisialisasi atribut kelas induk. |
| **3** | **Encapsulation** | Seluruh atribut dideklarasikan dengan *access modifier* `private` dan diakses/dimodifikasi melalui method *getter* dan *setter*. |
| **4** | **Inheritance** | - `Patient` dan `Doctor` mewarisi (`extends`) `Person`.<br>- `RegularTicket` dan `PriorityTicket` mewarisi (`extends`) `QueueTicket`.<br>- `MainFrameGUI` mewarisi (`extends`) `javax.swing.JFrame`. |
| **5** | **Polymorphism** | *Method overriding* pada `getRole()`, `toCsv()`, `getPrioritas()`, dan `getJenisAntrean()`. Pemanggilan polimorfik diterapkan saat `ClinicService` memeriksa `getPrioritas()` pada koleksi `ArrayList<QueueTicket>`. |
| **6** | **Abstraction** | Menggunakan 2 *abstract class* (`Person` dan `QueueTicket`) serta 1 *interface* (`Storable`). |
| **7** | **Exception Handling** | Menggunakan `try-catch` (`NumberFormatException` pada validasi form GUI dan `IOException` pada operasi baca/tulis file CSV). |
| **8** | **Collection Framework** | Menggunakan `ArrayList<Patient>`, `ArrayList<Doctor>`, dan `ArrayList<QueueTicket>` di dalam `ClinicService` untuk mengelola kumpulan objek di memori. |
| **9** | **File Handling** | Menggunakan `BufferedReader`, `FileReader`, `BufferedWriter`, dan `FileWriter` untuk membaca serta menyimpan data ke dalam 3 file CSV. |

---

## Struktur Folder Proyek

```text
ClinicQueueManagement/
├── data/
│   ├── pasien.csv               # Penyimpanan data pasien
│   ├── dokter.csv               # Penyimpanan data dokter, jadwal, dan kuota
│   └── antrean.csv              # Penyimpanan data antrean pemeriksaan
├── src/
│   └── klinik/
│       ├── Storable.java        # Interface kontrak konversi objek ke CSV
│       ├── Person.java          # Abstract superclass untuk entitas orang
│       ├── Patient.java         # Subclass entitas pasien
│       ├── Doctor.java          # Subclass entitas dokter & jadwal praktik
│       ├── QueueTicket.java     # Abstract superclass untuk tiket antrean
│       ├── RegularTicket.java   # Subclass tiket antrean reguler (Prioritas 2)
│       ├── PriorityTicket.java  # Subclass tiket antrean prioritas (Prioritas 1)
│       ├── ClinicService.java   # Kelas controller/service logika & File I/O
│       └── MainFrameGUI.java    # Kelas utama antarmuka pengguna (Java Swing)
└── README.md
```

---

## Format Penyimpanan Data (CSV)

Aplikasi menggunakan tiga file CSV di dalam folder `data/` dengan struktur kolom sebagai berikut:

1. **`data/pasien.csv`**
   ```csv
   id,nama,umur,noTelp,alamat,keluhan
   P01,Andi Pratama,22,08123456789,Lamongan,Demam dan Batuk
   P02,Mbah Karto,65,08223344556,Babat,Cek Tekanan Darah
   ```
2. **`data/dokter.csv`**
   ```csv
   id,nama,umur,noTelp,poli,jadwalPraktik,kuota,terisi
   D01,dr. Budi Santoso,42,08112233445,Poli Umum,Senin-Jumat 08:00-12:00,15,2
   D02,drg. Siti Aminah,35,08198765432,Poli Gigi,Senin-Rabu 13:00-16:00,10,0
   ```
3. **`data/antrean.csv`**
   ```csv
   nomorAntrean,jenisAntrean,idPasien,idDokter,status,alasanPrioritas
   R-001,REGULER,P01,D01,MENUNGGU,-
   P-001,PRIORITAS,P02,D01,DIPERIKSA,Lansia
   ```

---

## Cara Menjalankan Aplikasi

### Prasyarat
- **Java Development Kit (JDK)** versi 8 atau lebih baru (JDK 11 / 17 / 21 direkomendasikan).
- IDE Java (VS Code, IntelliJ IDEA, Apache NetBeans, atau Eclipse).

### Menjalankan melalui Terminal / Command Prompt
1. Buka terminal pada direktori utama proyek (`ClinicQueueManagement`).
2. Kompilasi seluruh kode sumber Java di dalam folder `src`:
   ```bash
   javac -d bin src/klinik/*.java
   ```
3. Jalankan kelas utama `klinik.MainFrameGUI`:
   ```bash
   java -cp bin klinik.MainFrameGUI
   ```

---

## Tim Pengembang (Kelompok IT-A)

- **Fajrian Aprilio** – NRP. `3125521012` *(Modul Antrean, Polimorfisme & Logika Prioritas)*
- **Arvin Dzaky Nugraha** – NRP. `3125521027` *(Modul Entitas Person, Pasien, Dokter & Operasi CRUD)*
- **Muhammad Hanif Wijayanto** – NRP. `3125521029` *(Modul Antarmuka Java Swing GUI, Interface Storable & File Handling CSV)*

**Program Studi D3 Teknik Informatika PSDKU Lamongan**  
**Politeknik Elektronika Negeri Surabaya (PENS) – 2026**