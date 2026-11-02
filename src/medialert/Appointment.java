package medialert;

import java.io.Serializable;

/** Appointment Module - book, reschedule and cancel appointments. */
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    int appointmentId;
    int patientId;
    int doctorId;
    String appointmentDate;
    String appointmentStatus;

    void bookAppointment() {
        appointmentId = InputHelper.readInt("Appointment ID");
        patientId = InputHelper.readInt("Patient ID");
        doctorId = InputHelper.readInt("Doctor ID");
        appointmentDate = InputHelper.readLine("Date (YYYY-MM-DD)");
        appointmentStatus = "Booked";
    }

    void rescheduleAppointment() {
        appointmentDate = InputHelper.readLine("New Date (YYYY-MM-DD)");
        appointmentStatus = "Rescheduled";
    }

    void cancelAppointment() {
        appointmentStatus = "Cancelled";
    }

    void displayAppointment() {
        ConsoleUI.card("APPOINTMENT  #" + appointmentId, ConsoleUI.PURPLE, new String[][] {
            {"Appointment ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + appointmentId)},
            {"Patient ID", "" + patientId},
            {"Doctor ID", "" + doctorId},
            {"Date", ConsoleUI.bold(ConsoleUI.WHITE, appointmentDate)},
            {"Status", ConsoleUI.status(appointmentStatus)}
        });
    }
}
