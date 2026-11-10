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

    // Method File Handling CSV akan dilengkapi pada Minggu 3
    public void simpanSemuaData() {
    }

    public void muatSemuaData() {
    }
}