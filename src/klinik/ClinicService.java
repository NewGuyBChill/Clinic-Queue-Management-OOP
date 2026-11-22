package klinik;

import java.util.ArrayList;

public class ClinicService {
    private ArrayList<Patient> daftarPasien = new ArrayList<>();
    private ArrayList<Doctor> daftarDokter = new ArrayList<>();
    private ArrayList<QueueTicket> daftarAntrean = new ArrayList<>();
    private int counterReguler = 1;
    private int counterPrioritas = 1;

    public ArrayList<Patient> getDaftarPasien() {
        return daftarPasien;
    }

    public ArrayList<Doctor> getDaftarDokter() {
        return daftarDokter;
    }

    public ArrayList<QueueTicket> getDaftarAntrean() {
        return daftarAntrean;
    }

    // 1. Kelola Data Pasien
    public void tambahPasien(Patient p) {
        daftarPasien.add(p);
    }

    public boolean ubahPasien(String id, String nama, int umur, String noTelp, String alamat, String keluhan) {
        for (int i = 0; i < daftarPasien.size(); i++) {
            Patient p = daftarPasien.get(i);
            if (p.getId().equalsIgnoreCase(id)) {
                p.setNama(nama);
                p.setUmur(umur);
                p.setNoTelp(noTelp);
                p.setAlamat(alamat);
                p.setKeluhan(keluhan);
                return true;
            }
        }
        return false;
    }

    public boolean hapusPasien(String id) {
        for (int i = 0; i < daftarPasien.size(); i++) {
            if (daftarPasien.get(i).getId().equalsIgnoreCase(id)) {
                daftarPasien.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Patient> cariPasien(String keyword) {
        ArrayList<Patient> hasil = new ArrayList<>();
        for (int i = 0; i < daftarPasien.size(); i++) {
            Patient p = daftarPasien.get(i);
            if (p.getId().equalsIgnoreCase(keyword) || p.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // 2. Kelola Data Dokter
    public void tambahDokter(Doctor d) {
        daftarDokter.add(d);
    }

    public boolean ubahDokter(String id, String nama, String poli, String jadwalBaru, int kuotaBaru) {
        for (int i = 0; i < daftarDokter.size(); i++) {
            Doctor d = daftarDokter.get(i);
            if (d.getId().equalsIgnoreCase(id)) {
                d.setNama(nama);
                d.setPoli(poli);
                d.setJadwalPraktik(jadwalBaru);
                d.setKuota(kuotaBaru);
                return true;
            }
        }
        return false;
    }

    public boolean hapusDokter(String id) {
        for (int i = 0; i < daftarDokter.size(); i++) {
            if (daftarDokter.get(i).getId().equalsIgnoreCase(id)) {
                daftarDokter.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Doctor> cariDokter(String keyword) {
        ArrayList<Doctor> hasil = new ArrayList<>();
        for (int i = 0; i < daftarDokter.size(); i++) {
            Doctor d = daftarDokter.get(i);
            if (d.getId().equalsIgnoreCase(keyword)
                    || d.getNama().toLowerCase().contains(keyword.toLowerCase())
                    || d.getPoli().toLowerCase().contains(keyword.toLowerCase())) {
                hasil.add(d);
            }
        }
        return hasil;
    }

    // 3. Logika Pendaftaran & Pemanggilan Antrean
    public QueueTicket daftarAntrean(Patient p, Doctor d, boolean isPrioritas, String alasan) {
        if (d.isKuotaPenuh()) {
            return null;
        }

        QueueTicket tiket;
        if (p.isLansia() || isPrioritas) {
            String nomor = "P-00" + counterPrioritas;
            counterPrioritas = counterPrioritas + 1;
            if (alasan.isEmpty() && p.isLansia()) {
                alasan = "Lansia";
            }
            tiket = new PriorityTicket(nomor, p, d, "MENUNGGU", alasan);
        } else {
            String nomor = "R-00" + counterReguler;
            counterReguler = counterReguler + 1;
            tiket = new RegularTicket(nomor, p, d, "MENUNGGU");
        }

        d.tambahTerisi();
        daftarAntrean.add(tiket);
        return tiket;
    }

    public QueueTicket panggilAntreanBerikutnya() {
        // Cari antrean PRIORITAS (prioritas == 1) yang berstatus MENUNGGU
        for (int i = 0; i < daftarAntrean.size(); i++) {
            QueueTicket t = daftarAntrean.get(i);
            if (t.getStatus().equals("MENUNGGU") && t.getPrioritas() == 1) {
                t.setStatus("DIPERIKSA");
                return t;
            }
        }
        // Jika tidak ada prioritas, cari antrean REGULER (prioritas == 2) yang MENUNGGU
        for (int i = 0; i < daftarAntrean.size(); i++) {
            QueueTicket t = daftarAntrean.get(i);
            if (t.getStatus().equals("MENUNGGU") && t.getPrioritas() == 2) {
                t.setStatus("DIPERIKSA");
                return t;
            }
        }
        return null;
    }

    public boolean ubahStatusAntrean(String nomor, String statusBaru) {
        for (int i = 0; i < daftarAntrean.size(); i++) {
            QueueTicket t = daftarAntrean.get(i);
            if (t.getNomorAntrean().equalsIgnoreCase(nomor)) {
                if (statusBaru.equals("BATAL") && t.getStatus().equals("MENUNGGU")) {
                    t.getDoctor().kurangiTerisi();
                }
                t.setStatus(statusBaru);
                return true;
            }
        }
        return false;
    }

    public Patient cariPasienById(String id) {
        for (Patient p : daftarPasien) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public Doctor cariDokterById(String id) {
        for (Doctor d : daftarDokter) {
            if (d.getId().equalsIgnoreCase(id)) {
                return d;
            }
        }
        return null;
    }

    public void inisialisasiDataAwal() {
        if (daftarDokter.isEmpty() && daftarPasien.isEmpty()) {
            Doctor d1 = new Doctor("D01", "dr. Budi Santoso", 42, "08112233445", "Poli Umum", "Senin-Jumat 08:00-12:00", 15);
            Doctor d2 = new Doctor("D02", "drg. Siti Aminah", 35, "08198765432", "Poli Gigi", "Senin-Rabu 13:00-16:00", 10);
            Doctor d3 = new Doctor("D03", "dr. Hendra Sp.A", 48, "08133344455", "Poli Anak", "Selasa-Kamis 09:00-13:00", 12);
            tambahDokter(d1);
            tambahDokter(d2);
            tambahDokter(d3);

            Patient p1 = new Patient("P01", "Andi Pratama", 22, "08123456789", "Lamongan", "Demam dan Batuk");
            Patient p2 = new Patient("P02", "Mbah Karto", 65, "08223344556", "Babat", "Cek Tekanan Darah");
            Patient p3 = new Patient("P03", "Siti Rahma", 29, "08556677889", "Tikung", "Pemeriksaan Kehamilan");
            tambahPasien(p1);
            tambahPasien(p2);
            tambahPasien(p3);

            daftarAntrean(p1, d1, false, "-");
            daftarAntrean(p2, d1, true, "Lansia");
        }
    }

    // Method File Handling CSV
    public void simpanSemuaData() throws java.io.IOException {
        java.io.File folder = new java.io.File("data");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 1. Simpan Pasien
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("data/pasien.csv"))) {
            bw.write("id,nama,umur,noTelp,alamat,keluhan");
            bw.newLine();
            for (Patient p : daftarPasien) {
                bw.write(p.toCsv());
                bw.newLine();
            }
        }

        // 2. Simpan Dokter
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("data/dokter.csv"))) {
            bw.write("id,nama,umur,noTelp,poli,jadwalPraktik,kuota,terisi");
            bw.newLine();
            for (Doctor d : daftarDokter) {
                bw.write(d.toCsv());
                bw.newLine();
            }
        }

        // 3. Simpan Antrean
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("data/antrean.csv"))) {
            bw.write("nomorAntrean,jenisAntrean,idPasien,idDokter,status,alasanPrioritas");
            bw.newLine();
            for (QueueTicket t : daftarAntrean) {
                bw.write(t.toCsv());
                bw.newLine();
            }
        }
    }

    public void muatSemuaData() throws java.io.IOException {
        java.io.File filePasien = new java.io.File("data/pasien.csv");
        java.io.File fileDokter = new java.io.File("data/dokter.csv");
        java.io.File fileAntrean = new java.io.File("data/antrean.csv");

        if (!filePasien.exists() && !fileDokter.exists() && !fileAntrean.exists()) {
            inisialisasiDataAwal();
            simpanSemuaData();
            return;
        }

        daftarPasien.clear();
        daftarDokter.clear();
        daftarAntrean.clear();
        counterReguler = 1;
        counterPrioritas = 1;

        // Muat Pasien
        if (filePasien.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filePasien))) {
                String line = br.readLine(); // Header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",", -1);
                    if (data.length >= 6) {
                        String id = data[0].trim();
                        String nama = data[1].trim();
                        int umur = Integer.parseInt(data[2].trim());
                        String noTelp = data[3].trim();
                        String alamat = data[4].trim();
                        String keluhan = data[5].trim();
                        daftarPasien.add(new Patient(id, nama, umur, noTelp, alamat, keluhan));
                    }
                }
            }
        }

        // Muat Dokter
        if (fileDokter.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileDokter))) {
                String line = br.readLine(); // Header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",", -1);
                    if (data.length >= 8) {
                        String id = data[0].trim();
                        String nama = data[1].trim();
                        int umur = Integer.parseInt(data[2].trim());
                        String noTelp = data[3].trim();
                        String poli = data[4].trim();
                        String jadwal = data[5].trim();
                        int kuota = Integer.parseInt(data[6].trim());
                        int terisi = Integer.parseInt(data[7].trim());
                        Doctor doc = new Doctor(id, nama, umur, noTelp, poli, jadwal, kuota);
                        doc.setTerisi(terisi);
                        daftarDokter.add(doc);
                    }
                }
            }
        }

        // Muat Antrean
        if (fileAntrean.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileAntrean))) {
                String line = br.readLine(); // Header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",", -1);
                    if (data.length >= 6) {
                        String nomor = data[0].trim();
                        String jenis = data[1].trim();
                        String idPasien = data[2].trim();
                        String idDokter = data[3].trim();
                        String status = data[4].trim();
                        String alasan = data[5].trim();

                        Patient p = cariPasienById(idPasien);
                        Doctor d = cariDokterById(idDokter);
                        if (p != null && d != null) {
                            QueueTicket ticket;
                            if (jenis.equalsIgnoreCase("PRIORITAS")) {
                                ticket = new PriorityTicket(nomor, p, d, status, alasan);
                            } else {
                                ticket = new RegularTicket(nomor, p, d, status);
                            }
                            daftarAntrean.add(ticket);

                            // Update nomor counter
                            try {
                                String numStr = nomor.replaceAll("[^0-9]", "");
                                int numVal = Integer.parseInt(numStr);
                                if (jenis.equalsIgnoreCase("PRIORITAS") && numVal >= counterPrioritas) {
                                    counterPrioritas = numVal + 1;
                                } else if (!jenis.equalsIgnoreCase("PRIORITAS") && numVal >= counterReguler) {
                                    counterReguler = numVal + 1;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        }
    }
}