package klinik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class MainFrameGUI extends JFrame {
    private final ClinicService service = new ClinicService();

    // Komponen Tampilan Utama
    private JTabbedPane tabbedPane;
    private JLabel lblNomorPanggilan;
    private JLabel lblDetailPanggilan;
    private QueueTicket tiketSedangAktif = null;

    // Komponen Tab Antrean
    private JTable tableAntrean;
    private DefaultTableModel modelAntrean;
    private JComboBox<PatientItem> cbPasienAntrean;
    private JComboBox<DoctorItem> cbDokterAntrean;
    private JCheckBox chkPrioritas;
    private JTextField txtAlasanPrioritas;
    private JComboBox<String> cbFilterStatusAntrean;

    // Komponen Tab Pasien
    private JTable tablePasien;
    private DefaultTableModel modelPasien;
    private JTextField txtCariPasien;
    private JTextField txtPasienId;
    private JTextField txtPasienNama;
    private JTextField txtPasienUmur;
    private JTextField txtPasienTelp;
    private JTextField txtPasienAlamat;
    private JTextField txtPasienKeluhan;

    // Komponen Tab Dokter
    private JTable tableDokter;
    private DefaultTableModel modelDokter;
    private JTextField txtCariDokter;
    private JTextField txtDokterId;
    private JTextField txtDokterNama;
    private JTextField txtDokterUmur;
    private JTextField txtDokterTelp;
    private JComboBox<String> cbDokterPoli;
    private JTextField txtDokterJadwal;
    private JTextField txtDokterKuota;

    // Helper item wrapper untuk JComboBox
    private static class PatientItem {
        final Patient patient;
        PatientItem(Patient patient) { this.patient = patient; }
        @Override
        public String toString() {
            return patient.getId() + " - " + patient.getNama() + " (" + patient.getUmur() + " thn)" 
                    + (patient.isLansia() ? " [Lansia]" : "");
        }
    }

    private static class DoctorItem {
        final Doctor doctor;
        DoctorItem(Doctor doctor) { this.doctor = doctor; }
        @Override
        public String toString() {
            return doctor.getId() + " - " + doctor.getNama() + " [" + doctor.getPoli() + "] (Sisa: " 
                    + (doctor.getKuota() - doctor.getTerisi()) + "/" + doctor.getKuota() + ")";
        }
    }

    public MainFrameGUI() {
        inisialisasiDataAwal();
        inisialisasiKomponenGUI();
        refreshSemuaData();
    }

    private void inisialisasiDataAwal() {
        try {
            service.muatSemuaData();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat data dari file CSV: " + e.getMessage() + "\nMenggunakan data default.",
                "Pemberitahuan", JOptionPane.WARNING_MESSAGE);
            service.inisialisasiDataAwal();
        }
    }

    public void inisialisasiKomponenGUI() {
        setTitle("Sistem Manajemen Antrean dan Pemeriksaan Pasien - Klinik Pratama");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // 1. Panel Atas: Header & Panel Panggilan Aktif
        JPanel topPanel = buatPanelHeaderDanPanggilan();
        add(topPanel, BorderLayout.NORTH);

        // 2. Panel Tengah: JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("📋 Pendaftaran & Antrean", buatTabAntrean());
        tabbedPane.addTab("👤 Kelola Data Pasien", buatTabPasien());
        tabbedPane.addTab("🩺 Kelola Data Dokter", buatTabDokter());
        add(tabbedPane, BorderLayout.CENTER);

        // 3. Panel Bawah: Footer & Tombol Simpan/Muat File CSV
        JPanel bottomPanel = buatPanelFooter();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // BAGIAN HEADER & PANGGILAN AKTIF
    // ==========================================
    private JPanel buatPanelHeaderDanPanggilan() {
        JPanel container = new JPanel(new BorderLayout(8, 8));
        container.setBorder(new EmptyBorder(10, 15, 0, 15));

        // Judul Klinik
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("KLINIK PRATAMA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(new Color(24, 76, 120));

        JLabel lblSubtitle = new JLabel("Sistem Antrean Prioritas & Manajemen Pemeriksaan Pasien Rawat Jalan");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.DARK_GRAY);

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        // Card Display Panggilan Antrean Aktif
        JPanel callCard = new JPanel(new BorderLayout(15, 10));
        callCard.setBackground(new Color(236, 244, 253));
        callCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 198, 240), 2, true),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel callInfoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        callInfoPanel.setOpaque(false);

        JLabel lblTag = new JLabel("📢 NOMOR PANGGILAN AKTIF SAAT INI:");
        lblTag.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTag.setForeground(new Color(40, 80, 130));

        lblNomorPanggilan = new JLabel("- BELUM ADA PANGGILAN -");
        lblNomorPanggilan.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblNomorPanggilan.setForeground(new Color(192, 41, 43));

        lblDetailPanggilan = new JLabel("Klik 'Panggil Antrean Berikutnya' untuk mendahulukan pasien prioritas.");
        lblDetailPanggilan.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDetailPanggilan.setForeground(new Color(50, 50, 50));

        callInfoPanel.add(lblTag);
        callInfoPanel.add(lblNomorPanggilan);

        // Tombol Aksi Panggilan
        JPanel actionCallPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionCallPanel.setOpaque(false);

        JButton btnPanggil = new JButton("📢 Panggil Berikutnya");
        btnPanggil.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPanggil.setBackground(new Color(41, 128, 185));
        btnPanggil.setForeground(Color.WHITE);
        btnPanggil.setFocusPainted(false);
        btnPanggil.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPanggil.addActionListener(e -> aksiPanggilAntreanBerikutnya());

        JButton btnSelesai = new JButton("✅ Selesaikan Pemeriksaan");
        btnSelesai.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSelesai.setBackground(new Color(39, 174, 96));
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelesai.addActionListener(e -> aksiSelesaikanPemeriksaan());

        actionCallPanel.add(btnPanggil);
        actionCallPanel.add(btnSelesai);

        callCard.add(callInfoPanel, BorderLayout.WEST);
        callCard.add(lblDetailPanggilan, BorderLayout.CENTER);
        callCard.add(actionCallPanel, BorderLayout.EAST);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(callCard, BorderLayout.SOUTH);

        return container;
    }

    // ==========================================
    // TAB 1: PENDAFTARAN & ANTREAN
    // ==========================================
    private JPanel buatTabAntrean() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Form Pendaftaran Antrean (Kiri)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(360, 400));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrasi Antrean Baru",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        cbPasienAntrean = new JComboBox<>();
        cbDokterAntrean = new JComboBox<>();
        chkPrioritas = new JCheckBox("Kategori Prioritas (Lansia / Bumil / Darurat)");
        txtAlasanPrioritas = new JTextField();
        txtAlasanPrioritas.setEnabled(false);

        cbPasienAntrean.addActionListener(e -> {
            PatientItem item = (PatientItem) cbPasienAntrean.getSelectedItem();
            if (item != null) {
                if (item.patient.isLansia()) {
                    chkPrioritas.setSelected(true);
                    chkPrioritas.setEnabled(false);
                    txtAlasanPrioritas.setText("Lansia");
                    txtAlasanPrioritas.setEnabled(true);
                } else {
                    chkPrioritas.setEnabled(true);
                    if ("Lansia".equalsIgnoreCase(txtAlasanPrioritas.getText())) {
                        chkPrioritas.setSelected(false);
                        txtAlasanPrioritas.setText("");
                        txtAlasanPrioritas.setEnabled(false);
                    }
                }
            }
        });

        chkPrioritas.addActionListener(e -> {
            if (chkPrioritas.isSelected()) {
                txtAlasanPrioritas.setEnabled(true);
                if (txtAlasanPrioritas.getText().trim().isEmpty()) {
                    txtAlasanPrioritas.setText("Kondisi Mendesak");
                }
            } else {
                txtAlasanPrioritas.setEnabled(false);
                txtAlasanPrioritas.setText("");
            }
        });

        formPanel.add(buatBarisInput("Pilih Pasien:", cbPasienAntrean));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(buatBarisInput("Pilih Dokter & Poli:", cbDokterAntrean));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(chkPrioritas);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buatBarisInput("Alasan Prioritas:", txtAlasanPrioritas));
        formPanel.add(Box.createVerticalStrut(15));

        JButton btnDaftar = new JButton("➕ Daftarkan Antrean");
        btnDaftar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDaftar.setBackground(new Color(46, 117, 182));
        btnDaftar.setForeground(Color.WHITE);
        btnDaftar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDaftar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnDaftar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDaftar.addActionListener(e -> aksiDaftarAntrean());
        formPanel.add(btnDaftar);

        // Info box panduan
        formPanel.add(Box.createVerticalGlue());
        JTextArea txtInfo = new JTextArea("Catatan:\n• Pasien umur ≥ 60 thn otomatis masuk antrean PRIORITAS (P-00x).\n• Antrean Prioritas otomatis dipanggil lebih awal dari Reguler (R-00x).\n• Kuota dokter berkurang otomatis.");
        txtInfo.setEditable(false);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setLineWrap(true);
        txtInfo.setBackground(new Color(245, 247, 250));
        txtInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(8, 8, 8, 8)
        ));
        formPanel.add(txtInfo);

        // Tabel Antrean (Kanan)
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Daftar Antrean Pemeriksaan",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        // Toolbar filter tabel antrean
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.add(new JLabel("Filter Status:"));
        cbFilterStatusAntrean = new JComboBox<>(new String[]{"SEMUA", "MENUNGGU", "DIPERIKSA", "SELESAI", "BATAL"});
        cbFilterStatusAntrean.addActionListener(e -> refreshTabelAntrean());
        filterPanel.add(cbFilterStatusAntrean);

        JButton btnBatalAntrean = new JButton("❌ Batalkan Antrean Terpilih");
        btnBatalAntrean.addActionListener(e -> aksiBatalkanAntreanTerpilih());
        filterPanel.add(btnBatalAntrean);

        JButton btnRefreshAntrean = new JButton("🔄 Refresh");
        btnRefreshAntrean.addActionListener(e -> refreshTabelAntrean());
        filterPanel.add(btnRefreshAntrean);

        tableContainer.add(filterPanel, BorderLayout.NORTH);

        String[] headerAntrean = {"No. Tiket", "Jenis", "ID Pasien", "Nama Pasien", "Dokter", "Poli", "Status", "Alasan Prioritas"};
        modelAntrean = new DefaultTableModel(headerAntrean, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAntrean = new JTable(modelAntrean);
        aturTampilanTabel(tableAntrean);
        tableContainer.add(new JScrollPane(tableAntrean), BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 2: KELOLA DATA PASIEN (CRUD)
    // ==========================================
    private JPanel buatTabPasien() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Form Pasien (Kiri)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(320, 450));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Data Pasien",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        txtPasienId = new JTextField();
        txtPasienNama = new JTextField();
        txtPasienUmur = new JTextField();
        txtPasienTelp = new JTextField();
        txtPasienAlamat = new JTextField();
        txtPasienKeluhan = new JTextField();

        formPanel.add(buatBarisInput("ID Pasien (misal: P04):", txtPasienId));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Nama Pasien:", txtPasienNama));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Umur (Tahun):", txtPasienUmur));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("No. Telepon / HP:", txtPasienTelp));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Alamat Domisili:", txtPasienAlamat));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Keluhan / Gejala Awal:", txtPasienKeluhan));
        formPanel.add(Box.createVerticalStrut(12));

        // Tombol CRUD Pasien
        JPanel btnCrudPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        JButton btnTambah = new JButton("➕ Tambah");
        JButton btnUbah = new JButton("✏️ Ubah");
        JButton btnHapus = new JButton("🗑️ Hapus");
        JButton btnReset = new JButton("🧹 Bersihkan");

        btnTambah.addActionListener(e -> aksiTambahPasien());
        btnUbah.addActionListener(e -> aksiUbahPasien());
        btnHapus.addActionListener(e -> aksiHapusPasien());
        btnReset.addActionListener(e -> resetFormPasien());

        btnCrudPanel.add(btnTambah);
        btnCrudPanel.add(btnUbah);
        btnCrudPanel.add(btnHapus);
        btnCrudPanel.add(btnReset);
        formPanel.add(btnCrudPanel);
        formPanel.add(Box.createVerticalGlue());

        // Tabel Pasien (Kanan)
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Daftar Pasien Terdaftar",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        // Search Bar Pasien
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.add(new JLabel("Cari Pasien (ID / Nama):"));
        txtCariPasien = new JTextField(18);
        JButton btnCari = new JButton("🔍 Cari");
        JButton btnResetCari = new JButton("Reset");

        btnCari.addActionListener(e -> cariDataPasien());
        btnResetCari.addActionListener(e -> {
            txtCariPasien.setText("");
            refreshTabelPasien();
        });

        searchPanel.add(txtCariPasien);
        searchPanel.add(btnCari);
        searchPanel.add(btnResetCari);
        tableContainer.add(searchPanel, BorderLayout.NORTH);

        String[] headerPasien = {"ID", "Nama Pasien", "Umur", "No. Telepon", "Alamat", "Keluhan", "Kategori Lansia"};
        modelPasien = new DefaultTableModel(headerPasien, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePasien = new JTable(modelPasien);
        aturTampilanTabel(tablePasien);

        tablePasien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePasien.getSelectedRow();
                if (row >= 0) {
                    txtPasienId.setText(tablePasien.getValueAt(row, 0).toString());
                    txtPasienNama.setText(tablePasien.getValueAt(row, 1).toString());
                    txtPasienUmur.setText(tablePasien.getValueAt(row, 2).toString());
                    txtPasienTelp.setText(tablePasien.getValueAt(row, 3).toString());
                    txtPasienAlamat.setText(tablePasien.getValueAt(row, 4).toString());
                    txtPasienKeluhan.setText(tablePasien.getValueAt(row, 5).toString());
                    txtPasienId.setEditable(false);
                }
            }
        });

        tableContainer.add(new JScrollPane(tablePasien), BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 3: KELOLA DATA DOKTER (CRUD)
    // ==========================================
    private JPanel buatTabDokter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Form Dokter (Kiri)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(330, 450));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Data Dokter & Jadwal",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        txtDokterId = new JTextField();
        txtDokterNama = new JTextField();
        txtDokterUmur = new JTextField();
        txtDokterTelp = new JTextField();
        cbDokterPoli = new JComboBox<>(new String[]{"Poli Umum", "Poli Gigi", "Poli Anak", "Poli Penyakit Dalam", "Poli Mata", "Poli THT"});
        cbDokterPoli.setEditable(true);
        txtDokterJadwal = new JTextField();
        txtDokterKuota = new JTextField();

        formPanel.add(buatBarisInput("ID Dokter (misal: D04):", txtDokterId));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Nama Dokter & Gelar:", txtDokterNama));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Umur (Tahun):", txtDokterUmur));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("No. Telepon / Kontak:", txtDokterTelp));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Spesialisasi / Poli:", cbDokterPoli));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Jadwal Praktik:", txtDokterJadwal));
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(buatBarisInput("Kuota Harian Pasien:", txtDokterKuota));
        formPanel.add(Box.createVerticalStrut(12));

        // Tombol CRUD Dokter
        JPanel btnCrudPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        JButton btnTambah = new JButton("➕ Tambah");
        JButton btnUbah = new JButton("✏️ Ubah");
        JButton btnHapus = new JButton("🗑️ Hapus");
        JButton btnReset = new JButton("🧹 Bersihkan");

        btnTambah.addActionListener(e -> aksiTambahDokter());
        btnUbah.addActionListener(e -> aksiUbahDokter());
        btnHapus.addActionListener(e -> aksiHapusDokter());
        btnReset.addActionListener(e -> resetFormDokter());

        btnCrudPanel.add(btnTambah);
        btnCrudPanel.add(btnUbah);
        btnCrudPanel.add(btnHapus);
        btnCrudPanel.add(btnReset);
        formPanel.add(btnCrudPanel);
        formPanel.add(Box.createVerticalGlue());

        // Tabel Dokter (Kanan)
        JPanel tableContainer = new JPanel(new BorderLayout(8, 8));
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Daftar Dokter & Kuota Praktik",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)
        ));

        // Search Bar Dokter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.add(new JLabel("Cari Dokter (ID / Nama / Poli):"));
        txtCariDokter = new JTextField(18);
        JButton btnCari = new JButton("🔍 Cari");
        JButton btnResetCari = new JButton("Reset");

        btnCari.addActionListener(e -> cariDataDokter());
        btnResetCari.addActionListener(e -> {
            txtCariDokter.setText("");
            refreshTabelDokter();
        });

        searchPanel.add(txtCariDokter);
        searchPanel.add(btnCari);
        searchPanel.add(btnResetCari);
        tableContainer.add(searchPanel, BorderLayout.NORTH);

        String[] headerDokter = {"ID", "Nama Dokter", "Umur", "No. Telepon", "Poli", "Jadwal Praktik", "Kuota", "Terisi", "Sisa"};
        modelDokter = new DefaultTableModel(headerDokter, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableDokter = new JTable(modelDokter);
        aturTampilanTabel(tableDokter);

        tableDokter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableDokter.getSelectedRow();
                if (row >= 0) {
                    txtDokterId.setText(tableDokter.getValueAt(row, 0).toString());
                    txtDokterNama.setText(tableDokter.getValueAt(row, 1).toString());
                    txtDokterUmur.setText(tableDokter.getValueAt(row, 2).toString());
                    txtDokterTelp.setText(tableDokter.getValueAt(row, 3).toString());
                    cbDokterPoli.setSelectedItem(tableDokter.getValueAt(row, 4).toString());
                    txtDokterJadwal.setText(tableDokter.getValueAt(row, 5).toString());
                    txtDokterKuota.setText(tableDokter.getValueAt(row, 6).toString());
                    txtDokterId.setEditable(false);
                }
            }
        });

        tableContainer.add(new JScrollPane(tableDokter), BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // FOOTER PANEL (CSV I/O & STATUS)
    // ==========================================
    private JPanel buatPanelFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(6, 15, 10, 15));

        JLabel lblCopyright = new JLabel("Praktikum PBO 2026 - D3 Teknik Informatika PENS PSDKU Lamongan");
        lblCopyright.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.GRAY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnSimpanCsv = new JButton("💾 Simpan Data ke CSV");
        btnSimpanCsv.setFocusPainted(false);
        btnSimpanCsv.addActionListener(e -> simpanDataKeCsv());

        JButton btnMuatCsv = new JButton("📂 Muat Ulang CSV");
        btnMuatCsv.setFocusPainted(false);
        btnMuatCsv.addActionListener(e -> muatUlangDariCsv());

        btnPanel.add(btnSimpanCsv);
        btnPanel.add(btnMuatCsv);

        footer.add(lblCopyright, BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);
        return footer;
    }

    // ==========================================
    // LOGIKA AKSI & EVENT HANDLERS
    // ==========================================
    private void aksiPanggilAntreanBerikutnya() {
        QueueTicket tiket = service.panggilAntreanBerikutnya();
        if (tiket != null) {
            tiketSedangAktif = tiket;
            lblNomorPanggilan.setText(tiket.getNomorAntrean() + " [" + tiket.getJenisAntrean() + "]");
            lblDetailPanggilan.setText("Pasien: " + tiket.getPatient().getNama() + " (" + tiket.getPatient().getUmur() + " thn)  →  Dokter: " 
                    + tiket.getDoctor().getNama() + " [" + tiket.getDoctor().getPoli() + "]");
            
            refreshTabelAntrean();
            JOptionPane.showMessageDialog(this, 
                "📢 PANGGILAN ANTREAN:\n\n" + tiket.cetakTiket() + "\nSilakan menuju ke " + tiket.getDoctor().getPoli() + ".",
                "Panggilan Antrean Pasien", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Tidak ada antrean berstatus MENUNGGU saat ini.", 
                "Antrean Kosong", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void aksiSelesaikanPemeriksaan() {
        if (tiketSedangAktif == null || !"DIPERIKSA".equals(tiketSedangAktif.getStatus())) {
            // Cek apakah ada antrean yang dipilih di tabel
            int selectedRow = tableAntrean.getSelectedRow();
            if (selectedRow >= 0) {
                String nomor = tableAntrean.getValueAt(selectedRow, 0).toString();
                service.ubahStatusAntrean(nomor, "SELESAI");
                refreshTabelAntrean();
                JOptionPane.showMessageDialog(this, "Status tiket " + nomor + " telah diubah menjadi SELESAI.");
                return;
            }
            JOptionPane.showMessageDialog(this, 
                "Belum ada pasien yang sedang aktif diperiksa. Silakan panggil antrean atau pilih baris pada tabel.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tiketSedangAktif.setStatus("SELESAI");
        JOptionPane.showMessageDialog(this, 
            "Pemeriksaan untuk antrean " + tiketSedangAktif.getNomorAntrean() + " (" + tiketSedangAktif.getPatient().getNama() + ") telah SELESAI.",
            "Pemeriksaan Selesai", JOptionPane.INFORMATION_MESSAGE);
        
        lblNomorPanggilan.setText("- SELESAI DIPERIKSA -");
        lblDetailPanggilan.setText("Pemeriksaan sebelumnya telah tuntas. Silakan panggil antrean berikutnya.");
        tiketSedangAktif = null;
        refreshTabelAntrean();
    }

    private void aksiDaftarAntrean() {
        PatientItem pItem = (PatientItem) cbPasienAntrean.getSelectedItem();
        DoctorItem dItem = (DoctorItem) cbDokterAntrean.getSelectedItem();

        if (pItem == null || dItem == null) {
            JOptionPane.showMessageDialog(this, "Pilih pasien dan dokter terlebih dahulu!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Doctor d = dItem.doctor;
        if (d.isKuotaPenuh()) {
            JOptionPane.showMessageDialog(this, 
                "Maaf, kuota antrean untuk " + d.getNama() + " sudah penuh (" + d.getTerisi() + "/" + d.getKuota() + ").", 
                "Kuota Penuh", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isPrioritas = chkPrioritas.isSelected();
        String alasan = txtAlasanPrioritas.getText().trim();
        if (isPrioritas && alasan.isEmpty()) {
            alasan = "Prioritas Khusus";
        }

        QueueTicket tiket = service.daftarAntrean(pItem.patient, d, isPrioritas, alasan);
        if (tiket != null) {
            JOptionPane.showMessageDialog(this, 
                "Tiket Antrean Berhasil Diterbitkan!\n\n" + tiket.cetakTiket() + "\nSisa Kuota Dokter: " 
                + (d.getKuota() - d.getTerisi()) + "/" + d.getKuota(),
                "Pendaftaran Berhasil", JOptionPane.INFORMATION_MESSAGE);
            
            refreshTabelAntrean();
            refreshDropdownDokter();
            refreshTabelDokter();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mendaftarkan antrean.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiBatalkanAntreanTerpilih() {
        int selectedRow = tableAntrean.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tiket antrean pada tabel yang ingin dibatalkan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomor = tableAntrean.getValueAt(selectedRow, 0).toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin membatalkan antrean " + nomor + "?", 
            "Konfirmasi Pembatalan", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            boolean sukses = service.ubahStatusAntrean(nomor, "BATAL");
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Tiket " + nomor + " berhasil dibatalkan dan kuota dikembalikan.");
                refreshTabelAntrean();
                refreshDropdownDokter();
                refreshTabelDokter();
            }
        }
    }

    // CRUD PASIEN
    private void aksiTambahPasien() {
        try {
            String id = txtPasienId.getText().trim();
            String nama = txtPasienNama.getText().trim();
            String strUmur = txtPasienUmur.getText().trim();
            String telp = txtPasienTelp.getText().trim();
            String alamat = txtPasienAlamat.getText().trim();
            String keluhan = txtPasienKeluhan.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || strUmur.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Nama, dan Umur wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.cariPasienById(id) != null) {
                JOptionPane.showMessageDialog(this, "Pasien dengan ID " + id + " sudah terdaftar!", "ID Duplikat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int umur = Integer.parseInt(strUmur);
            if (umur < 0) {
                JOptionPane.showMessageDialog(this, "Umur tidak boleh negatif!", "Input Salah", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Patient p = new Patient(id, nama, umur, telp, alamat, keluhan);
            service.tambahPasien(p);
            JOptionPane.showMessageDialog(this, "Data pasien berhasil ditambahkan!");
            resetFormPasien();
            refreshTabelPasien();
            refreshDropdownPasien();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Umur harus berupa bilangan bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiUbahPasien() {
        try {
            String id = txtPasienId.getText().trim();
            String nama = txtPasienNama.getText().trim();
            String strUmur = txtPasienUmur.getText().trim();
            String telp = txtPasienTelp.getText().trim();
            String alamat = txtPasienAlamat.getText().trim();
            String keluhan = txtPasienKeluhan.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data pasien dari tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int umur = Integer.parseInt(strUmur);
            boolean berhasil = service.ubahPasien(id, nama, umur, telp, alamat, keluhan);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data pasien berhasil diperbarui!");
                resetFormPasien();
                refreshTabelPasien();
                refreshDropdownPasien();
            } else {
                JOptionPane.showMessageDialog(this, "Pasien tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Umur harus berupa bilangan bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiHapusPasien() {
        String id = txtPasienId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pasien dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus pasien " + id + " - " + txtPasienNama.getText() + "?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            boolean berhasil = service.hapusPasien(id);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data pasien berhasil dihapus.");
                resetFormPasien();
                refreshTabelPasien();
                refreshDropdownPasien();
            } else {
                JOptionPane.showMessageDialog(this, "Pasien gagal dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetFormPasien() {
        txtPasienId.setText("");
        txtPasienId.setEditable(true);
        txtPasienNama.setText("");
        txtPasienUmur.setText("");
        txtPasienTelp.setText("");
        txtPasienAlamat.setText("");
        txtPasienKeluhan.setText("");
        tablePasien.clearSelection();
    }

    private void cariDataPasien() {
        String keyword = txtCariPasien.getText().trim();
        if (keyword.isEmpty()) {
            refreshTabelPasien();
            return;
        }
        ArrayList<Patient> hasil = service.cariPasien(keyword);
        modelPasien.setRowCount(0);
        for (Patient p : hasil) {
            modelPasien.addRow(new Object[]{
                p.getId(), p.getNama(), p.getUmur(), p.getNoTelp(), p.getAlamat(), p.getKeluhan(),
                p.isLansia() ? "Lansia (≥60)" : "Reguler"
            });
        }
    }

    // CRUD DOKTER
    private void aksiTambahDokter() {
        try {
            String id = txtDokterId.getText().trim();
            String nama = txtDokterNama.getText().trim();
            String strUmur = txtDokterUmur.getText().trim();
            String telp = txtDokterTelp.getText().trim();
            String poli = cbDokterPoli.getSelectedItem() != null ? cbDokterPoli.getSelectedItem().toString().trim() : "";
            String jadwal = txtDokterJadwal.getText().trim();
            String strKuota = txtDokterKuota.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || strKuota.isEmpty() || poli.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Nama, Poli, dan Kuota wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.cariDokterById(id) != null) {
                JOptionPane.showMessageDialog(this, "Dokter dengan ID " + id + " sudah terdaftar!", "ID Duplikat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int umur = strUmur.isEmpty() ? 0 : Integer.parseInt(strUmur);
            int kuota = Integer.parseInt(strKuota);
            if (kuota <= 0) {
                JOptionPane.showMessageDialog(this, "Kuota harus lebih besar dari 0!", "Input Salah", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Doctor d = new Doctor(id, nama, umur, telp, poli, jadwal, kuota);
            service.tambahDokter(d);
            JOptionPane.showMessageDialog(this, "Data dokter berhasil ditambahkan!");
            resetFormDokter();
            refreshTabelDokter();
            refreshDropdownDokter();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Umur dan Kuota harus berupa bilangan bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiUbahDokter() {
        try {
            String id = txtDokterId.getText().trim();
            String nama = txtDokterNama.getText().trim();
            String poli = cbDokterPoli.getSelectedItem() != null ? cbDokterPoli.getSelectedItem().toString().trim() : "";
            String jadwal = txtDokterJadwal.getText().trim();
            String strKuota = txtDokterKuota.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data dokter dari tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int kuota = Integer.parseInt(strKuota);
            boolean berhasil = service.ubahDokter(id, nama, poli, jadwal, kuota);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data dokter berhasil diperbarui!");
                resetFormDokter();
                refreshTabelDokter();
                refreshDropdownDokter();
            } else {
                JOptionPane.showMessageDialog(this, "Dokter tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kuota harus berupa bilangan bulat!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aksiHapusDokter() {
        String id = txtDokterId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data dokter dari tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus dokter " + id + " - " + txtDokterNama.getText() + "?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            boolean berhasil = service.hapusDokter(id);
            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Data dokter berhasil dihapus.");
                resetFormDokter();
                refreshTabelDokter();
                refreshDropdownDokter();
            } else {
                JOptionPane.showMessageDialog(this, "Dokter gagal dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetFormDokter() {
        txtDokterId.setText("");
        txtDokterId.setEditable(true);
        txtDokterNama.setText("");
        txtDokterUmur.setText("");
        txtDokterTelp.setText("");
        cbDokterPoli.setSelectedIndex(0);
        txtDokterJadwal.setText("");
        txtDokterKuota.setText("");
        tableDokter.clearSelection();
    }

    private void cariDataDokter() {
        String keyword = txtCariDokter.getText().trim();
        if (keyword.isEmpty()) {
            refreshTabelDokter();
            return;
        }
        ArrayList<Doctor> hasil = service.cariDokter(keyword);
        modelDokter.setRowCount(0);
        for (Doctor d : hasil) {
            int sisa = d.getKuota() - d.getTerisi();
            modelDokter.addRow(new Object[]{
                d.getId(), d.getNama(), d.getUmur(), d.getNoTelp(), d.getPoli(),
                d.getJadwalPraktik(), d.getKuota(), d.getTerisi(), sisa
            });
        }
    }

    // ==========================================
    // REFRESH DATA & TABEL
    // ==========================================
    public void refreshSemuaData() {
        refreshTabelPasien();
        refreshTabelDokter();
        refreshTabelAntrean();
        refreshDropdownPasien();
        refreshDropdownDokter();
    }

    public void refreshTabelPasien() {
        if (modelPasien == null) return;
        modelPasien.setRowCount(0);
        for (Patient p : service.getDaftarPasien()) {
            modelPasien.addRow(new Object[]{
                p.getId(), p.getNama(), p.getUmur(), p.getNoTelp(), p.getAlamat(), p.getKeluhan(),
                p.isLansia() ? "Lansia (≥60)" : "Reguler"
            });
        }
    }

    public void refreshTabelDokter() {
        if (modelDokter == null) return;
        modelDokter.setRowCount(0);
        for (Doctor d : service.getDaftarDokter()) {
            int sisa = d.getKuota() - d.getTerisi();
            modelDokter.addRow(new Object[]{
                d.getId(), d.getNama(), d.getUmur(), d.getNoTelp(), d.getPoli(),
                d.getJadwalPraktik(), d.getKuota(), d.getTerisi(), sisa
            });
        }
    }

    public void refreshTabelAntrean() {
        if (modelAntrean == null) return;
        modelAntrean.setRowCount(0);
        String filter = cbFilterStatusAntrean != null ? (String) cbFilterStatusAntrean.getSelectedItem() : "SEMUA";

        for (QueueTicket t : service.getDaftarAntrean()) {
            if ("SEMUA".equalsIgnoreCase(filter) || t.getStatus().equalsIgnoreCase(filter)) {
                String alasan = "-";
                if (t instanceof PriorityTicket) {
                    alasan = ((PriorityTicket) t).getAlasanPrioritas();
                }
                modelAntrean.addRow(new Object[]{
                    t.getNomorAntrean(),
                    t.getJenisAntrean(),
                    t.getPatient().getId(),
                    t.getPatient().getNama(),
                    t.getDoctor().getNama(),
                    t.getDoctor().getPoli(),
                    t.getStatus(),
                    alasan
                });
            }
        }
    }

    private void refreshDropdownPasien() {
        if (cbPasienAntrean == null) return;
        cbPasienAntrean.removeAllItems();
        for (Patient p : service.getDaftarPasien()) {
            cbPasienAntrean.addItem(new PatientItem(p));
        }
    }

    private void refreshDropdownDokter() {
        if (cbDokterAntrean == null) return;
        cbDokterAntrean.removeAllItems();
        for (Doctor d : service.getDaftarDokter()) {
            cbDokterAntrean.addItem(new DoctorItem(d));
        }
    }

    // ==========================================
    // CSV FILE HANDLING UI
    // ==========================================
    private void simpanDataKeCsv() {
        try {
            service.simpanSemuaData();
            JOptionPane.showMessageDialog(this, 
                "Seluruh data pasien, dokter, dan antrean berhasil disimpan ke folder 'data/' (CSV)!",
                "Penyimpanan Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal menyimpan data ke CSV: " + e.getMessage(), 
                "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatUlangDariCsv() {
        try {
            service.muatSemuaData();
            refreshSemuaData();
            JOptionPane.showMessageDialog(this, 
                "Data berhasil dimuat ulang dari file CSV!", 
                "Muat Data Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat ulang data: " + e.getMessage(), 
                "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // HELPER UI KOMPONEN
    // ==========================================
    private JPanel buatBarisInput(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(5, 2));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return p;
    }

    private void aturTampilanTabel(JTable table) {
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        }
    }

    public static void main(String[] args) {
        try {
            // Gunakan Look and Feel sistem untuk tampilan yang bersih dan modern
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrameGUI frame = new MainFrameGUI();
            frame.setVisible(true);
        });
    }
}
