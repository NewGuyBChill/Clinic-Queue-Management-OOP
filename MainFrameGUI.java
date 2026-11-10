package klinik;

import javax.swing.*;

public class MainFrameGUI extends JFrame {
    private ClinicService service = new ClinicService();
    private JTabbedPane tabbedPane;
    private JTable tablePasien;
    private JTable tableDokter;
    private JTable tableAntrean;
    private JLabel lblPanggilanAktif;

    public MainFrameGUI() {
        inisialisasiKomponenGUI();
    }

    public void inisialisasiKomponenGUI() {
        setTitle("Sistem Manajemen Antrean Klinik Pratama");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void refreshTabelPasien() {
    }

    public void refreshTabelDokter() {
    }

    public void refreshTabelAntrean() {
    }

    public static void main(String[] args) {
        ClinicService service = new ClinicService();

        // 1. Uji Pembuatan Object & Inheritance Person
        Doctor d1 = new Doctor("D01", "dr. Budi", 42, "081234567", "Poli Umum", "08:00-12:00", 10);
        Patient p1 = new Patient("P01", "Andi", 22, "08111111", "Lamongan", "Demam");
        Patient p2 = new Patient("P02", "Mbah Karto", 65, "08222222", "Babat", "Cek Tensi");

        service.tambahDokter(d1);
        service.tambahPasien(p1);
        service.tambahPasien(p2);

        // 2. Uji Pendaftaran Antrean (Andi daftar dulu, Mbah Karto daftar belakangan)
        QueueTicket t1 = service.daftarAntrean(p1, d1, false, "-");
        QueueTicket t2 = service.daftarAntrean(p2, d1, false, "Lansia");

        System.out.println("=== DAFTAR ANTREAN ===");
        System.out.println(t1.cetakTiket());
        System.out.println(t2.cetakTiket());

        // 3. Uji Polimorfisme Panggilan Prioritas (Mbah Karto terpanggil lebih dulu)
        System.out.println("\n=== PANGGILAN PERTAMA ===");
        QueueTicket panggil1 = service.panggilAntreanBerikutnya();
        System.out.println(panggil1.cetakTiket());

        System.out.println("\n=== PANGGILAN KEDUA ===");
        QueueTicket panggil2 = service.panggilAntreanBerikutnya();
        System.out.println(panggil2.cetakTiket());
    }
}