package medialert;

import java.io.Serializable;

/** Doctor Module - add, update doctors and their availability. */
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    int doctorId;
    String doctorName;
    String specialization;
    double consultationFee;
    boolean availability;

    void addDoctor() {
        doctorId = InputHelper.readInt("Doctor ID");
        doctorName = InputHelper.readLine("Doctor Name");
        specialization = InputHelper.readLine("Specialization");
        consultationFee = InputHelper.readDouble("Consultation Fee");
        availability = InputHelper.readBoolean("Available (y/n)");
    }

    void updateDoctor() {
        doctorName = InputHelper.readLine("Doctor Name");
        specialization = InputHelper.readLine("Specialization");
        consultationFee = InputHelper.readDouble("Consultation Fee");
    }

    void updateAvailability() {
        availability = InputHelper.readBoolean("Available (y/n)");
    }

    void displayDoctor() {
        ConsoleUI.card("DOCTOR  #" + doctorId, ConsoleUI.BLUE, new String[][] {
            {"Doctor ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + doctorId)},
            {"Name", ConsoleUI.bold(ConsoleUI.WHITE, doctorName)},
            {"Specialization", specialization},
            {"Consultation Fee", ConsoleUI.money(consultationFee)},
            {"Status", ConsoleUI.yesNo(availability)}
        });
    }
}
