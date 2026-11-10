package klinik;

public abstract class Person implements Storable {
    private String id;
    private String nama;
    private int umur;
    private String noTelp;

    public Person(String id, String nama, int umur, String noTelp) {
        this.id = id;
        this.nama = nama;
        this.umur = umur;
        this.noTelp = noTelp;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public abstract String getRole();

    public String getInfo() {
        return getRole() + ": " + id + " - " + nama + " (" + umur + " thn)";
    }
}