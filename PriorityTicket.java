package klinik;

public class PriorityTicket extends QueueTicket {
    private String alasanPrioritas;

    public PriorityTicket(String nomorAntrean, Patient patient, Doctor doctor, String status, String alasanPrioritas) {
        super(nomorAntrean, patient, doctor, status);
        this.alasanPrioritas = alasanPrioritas;
    }

    public String getAlasanPrioritas() {
        return alasanPrioritas;
    }

    @Override
    public int getPrioritas() {
        return 1;
    }

    @Override
    public String getJenisAntrean() {
        return "PRIORITAS";
    }

    @Override
    public String toCsv() {
        return getNomorAntrean() + "," + getJenisAntrean() + "," + getPatient().getId() + ","
                + getDoctor().getId() + "," + getStatus() + "," + alasanPrioritas;
    }
}