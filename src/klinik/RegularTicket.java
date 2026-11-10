package klinik;

public class RegularTicket extends QueueTicket {

    public RegularTicket(String nomorAntrean, Patient patient, Doctor doctor, String status) {
        super(nomorAntrean, patient, doctor, status);
    }

    @Override
    public int getPrioritas() {
        return 2;
    }

    @Override
    public String getJenisAntrean() {
        return "REGULER";
    }
}