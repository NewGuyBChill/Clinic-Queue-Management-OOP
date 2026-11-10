package klinik;

public abstract class QueueTicket implements Storable {
    private String nomorAntrean;
    private Patient patient;
    private Doctor doctor;
    private String status;

    public QueueTicket(String nomorAntrean, Patient patient, Doctor doctor, String status) {
        this.nomorAntrean = nomorAntrean;
        this.patient = patient;
        this.doctor = doctor;
        this.status = status;
    }

    public String getNomorAntrean() {
        return nomorAntrean;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract int getPrioritas();
    public abstract String getJenisAntrean();

    public String cetakTiket() {
        return nomorAntrean + " [" + getJenisAntrean() + "] - Pasien: " + patient.getNama()
                + " | Dokter: " + doctor.getNama() + " | Status: " + status;
    }

    @Override
    public String toCsv() {
        return nomorAntrean + "," + getJenisAntrean() + "," + patient.getId() + ","
                + doctor.getId() + "," + status + ",-";
    }
}