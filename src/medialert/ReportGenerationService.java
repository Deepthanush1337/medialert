package medialert;

/** ReportGenerationService Module - prints detailed reports. */
public class ReportGenerationService {

    private void header(String name) {
        ConsoleUI.blank();
        ConsoleUI.info("Generating " + name + " report  " + ConsoleUI.dim(ConsoleUI.now()));
        ConsoleUI.progressBar("Report", 20);
    }

    void generatePatientReport(Patient patient) {
        header("patient");
        patient.displayPatient();
    }

    void generateAppointmentReport(Appointment appointment) {
        header("appointment");
        appointment.displayAppointment();
    }

    void generatePrescriptionReport(Prescription prescription) {
        header("prescription");
        prescription.displayPrescription();
    }

    void generateBillingReport(Billing billing) {
        header("billing");
        billing.displayBill();
    }
}
