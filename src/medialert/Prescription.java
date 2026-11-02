package medialert;

import java.io.Serializable;

/** Prescription Module - create, update and print prescriptions. */
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    int prescriptionId;
    int patientId;
    int doctorId;
    String diagnosis;
    String medicines;
    String dosage;

    void createPrescription() {
        prescriptionId = InputHelper.readInt("Prescription ID");
        patientId = InputHelper.readInt("Patient ID");
        doctorId = InputHelper.readInt("Doctor ID");
        readDetails();
    }

    void updatePrescription() {
        readDetails();
    }

    private void readDetails() {
        diagnosis = InputHelper.readLine("Diagnosis");
        medicines = InputHelper.readLine("Medicines");
        dosage = InputHelper.readLine("Dosage");
    }

    void displayPrescription() {
        ConsoleUI.card("PRESCRIPTION  #" + prescriptionId, ConsoleUI.GREEN, details());
    }

    void printPrescription() {
        String[][] d = details();
        String[][] slip = new String[d.length + 2][];
        System.arraycopy(d, 0, slip, 0, d.length);
        slip[d.length] = new String[] {"-"};
        slip[d.length + 1] = new String[] {"Issued", ConsoleUI.dim(ConsoleUI.now())};
        ConsoleUI.card("Rx  PRESCRIPTION SLIP", ConsoleUI.GREEN, slip);
    }

    private String[][] details() {
        return new String[][] {
            {"Prescription ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + prescriptionId)},
            {"Patient ID", "" + patientId},
            {"Doctor ID", "" + doctorId},
            {"Diagnosis", ConsoleUI.bold(ConsoleUI.WHITE, diagnosis)},
            {"Medicines", ConsoleUI.fg(ConsoleUI.CYAN, medicines)},
            {"Dosage", dosage}
        };
    }
}
