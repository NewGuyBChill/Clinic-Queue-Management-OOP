package klinik;

public class Patient extends Person {
    private String alamat;
    private String keluhan;

    public Patient(String id, String nama, int umur, String noTelp, String alamat, String keluhan) {
        super(id, nama, umur, noTelp);
        this.alamat = alamat;
        this.keluhan = keluhan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public boolean isLansia() {
        return getUmur() >= 60;
    }

    @Override
    public String getRole() {
        return "Pasien";
    }

    @Override
    public String toCsv() {
        return getId() + "," + getNama() + "," + getUmur() + "," + getNoTelp() + "," + alamat + "," + keluhan;
    }
}