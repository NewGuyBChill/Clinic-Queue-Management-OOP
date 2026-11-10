package klinik;

public class Doctor extends Person {
    private String poli;
    private String jadwalPraktik;
    private int kuota;
    private int terisi;

    public Doctor(String id, String nama, int umur, String noTelp, String poli, String jadwalPraktik, int kuota) {
        super(id, nama, umur, noTelp);
        this.poli = poli;
        this.jadwalPraktik = jadwalPraktik;
        this.kuota = kuota;
        this.terisi = 0;
    }

    public String getPoli() {
        return poli;
    }

    public void setPoli(String poli) {
        this.poli = poli;
    }

    public String getJadwalPraktik() {
        return jadwalPraktik;
    }

    public void setJadwalPraktik(String jadwalPraktik) {
        this.jadwalPraktik = jadwalPraktik;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    public int getTerisi() {
        return terisi;
    }

    public void setTerisi(int terisi) {
        this.terisi = terisi;
    }

    public boolean isKuotaPenuh() {
        return terisi >= kuota;
    }

    public void tambahTerisi() {
        terisi = terisi + 1;
    }

    public void kurangiTerisi() {
        if (terisi > 0) {
            terisi = terisi - 1;
        }
    }

    @Override
    public String getRole() {
        return "Dokter";
    }

    @Override
    public String toCsv() {
        return getId() + "," + getNama() + "," + getUmur() + "," + getNoTelp() + "," + poli + "," + jadwalPraktik + "," + kuota + "," + terisi;
    }
}