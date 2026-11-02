package medialert;

import java.io.Serializable;

/** Billing Module - calculate bill, generate invoice, receive payment. */
public class Billing implements Serializable {

    private static final long serialVersionUID = 1L;

    int billId;
    int patientId;
    double consultationFee;
    double medicineCharge;
    double totalBill;
    boolean paid;

    void calculateBill() {
        billId = InputHelper.readInt("Bill ID");
        patientId = InputHelper.readInt("Patient ID");
        consultationFee = InputHelper.readDouble("Consultation Fee");
        medicineCharge = InputHelper.readDouble("Medicine Charge");
        totalBill = consultationFee + medicineCharge;
    }

    void generateInvoice() {
        ConsoleUI.card("INVOICE  #INV-" + billId, ConsoleUI.CORAL, new String[][] {
            {"Bill ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + billId)},
            {"Patient ID", "" + patientId},
            {"-"},
            {"Consultation", ConsoleUI.padLeft(ConsoleUI.money(consultationFee), 14)},
            {"Medicines", ConsoleUI.padLeft(ConsoleUI.money(medicineCharge), 14)},
            {"-"},
            {"TOTAL", ConsoleUI.bold(ConsoleUI.CORAL, ConsoleUI.padLeft(ConsoleUI.money(totalBill), 14))},
            {"Payment", paid ? ConsoleUI.bold(ConsoleUI.GREEN, "\u25CF PAID") : ConsoleUI.bold(ConsoleUI.YELLOW, "\u25CF PENDING")}
        });
    }

    void receivePayment() {
        if (paid) {
            ConsoleUI.warn("Bill #" + billId + " is already paid.");
            return;
        }
        ConsoleUI.progressBar("Paying", 20);
        paid = true;
        ConsoleUI.ok("Payment of " + ConsoleUI.bold(ConsoleUI.GREEN, ConsoleUI.money(totalBill))
                + " received for Bill #" + billId);
    }

    void displayBill() {
        ConsoleUI.card("BILL  #" + billId, ConsoleUI.CORAL, new String[][] {
            {"Bill ID", ConsoleUI.bold(ConsoleUI.YELLOW, "" + billId)},
            {"Patient ID", "" + patientId},
            {"Consultation Fee", ConsoleUI.money(consultationFee)},
            {"Medicine Charge", ConsoleUI.money(medicineCharge)},
            {"Total Bill", ConsoleUI.bold(ConsoleUI.CORAL, ConsoleUI.money(totalBill))},
            {"Payment", paid ? ConsoleUI.bold(ConsoleUI.GREEN, "\u25CF PAID") : ConsoleUI.bold(ConsoleUI.YELLOW, "\u25CF PENDING")}
        });
    }
}
