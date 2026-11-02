package medialert;

import java.io.Serializable;

/** Patient Module - register, update and display patients. */
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    int patientId;
    String patientName;
    int age;
    String gender;
    long mobileNumber;
    String email;

    void registerPatient() {
        patientId = InputHelper.readInt("Patient ID");
        readDetails();
    }

    void updatePatient() {
        readDetails();
    }

    private void readDetails() {
        patientName = InputHelper.readLine("Patient Name");
        age = InputHelper.readInt("Age");
        gender = InputHelper.readLine("Gender");
        mobileNumber = InputHelper.readLong("Mobile Number");
        email = InputHelper.readLine("Email");
    }

    void displayPatient() {
        ConsoleUI.card("PATIENT  #" + patientId, ConsoleUI.TEAL, details());
    }

    String[][] details() {
        return new String[][] {
            {"Patient ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + patientId)},
            {"Name", ConsoleUI.bold(ConsoleUI.WHITE, patientName)},
            {"Age", age + " yrs"},
            {"Gender", gender},
            {"Mobile", "+91 " + mobileNumber},
            {"Email", ConsoleUI.fg(ConsoleUI.BLUE, email)}
        };
    }
}
