package medialert;

import java.util.ArrayList;

/** AnalysisService Module - consultations, most visited doctor, revenue, summary. */
public class AnalysisService {

    private int countConsultations(ArrayList<Appointment> appointmentList, int doctorId) {
        int count = 0;
        for (Appointment a : appointmentList) {
            if (a.doctorId == doctorId) {
                count++;
            }
        }
        return count;
    }

    void doctorWiseConsultations(ArrayList<Appointment> appointmentList, ArrayList<Doctor> doctorList) {
        if (doctorList.isEmpty()) {
            ConsoleUI.warn("No doctors found.");
            return;
        }
        String[] names = new String[doctorList.size()];
        int[] counts = new int[doctorList.size()];
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor d = doctorList.get(i);
            names[i] = d.doctorName;
            counts[i] = countConsultations(appointmentList, d.doctorId);
        }
        ConsoleUI.blank();
        ConsoleUI.println("  " + ConsoleUI.bold(ConsoleUI.CYAN, "DOCTOR-WISE CONSULTATIONS"));
        ConsoleUI.barChart(names, counts, "visit");
    }

    void identifyMostVisitedDoctor(ArrayList<Appointment> appointmentList, ArrayList<Doctor> doctorList) {
        int maxCount = 0;
        Doctor top = null;
        for (Doctor d : doctorList) {
            int count = countConsultations(appointmentList, d.doctorId);
            if (count > maxCount) {
                maxCount = count;
                top = d;
            }
        }
        if (top == null) {
            ConsoleUI.warn("No consultations recorded yet.");
            return;
        }
        ConsoleUI.card("MOST VISITED DOCTOR", ConsoleUI.YELLOW, new String[][] {
            {"Doctor", ConsoleUI.bold(ConsoleUI.WHITE, top.doctorName)},
            {"Specialization", top.specialization},
            {"Visits", ConsoleUI.bold(ConsoleUI.YELLOW, maxCount + "")
                    + ConsoleUI.dim("  of " + appointmentList.size() + " appointments")}
        });
    }

    void calculateDailyRevenue(ArrayList<Billing> billingList) {
        double total = 0;
        double paid = 0;
        for (Billing b : billingList) {
            total += b.totalBill;
            if (b.paid) {
                paid += b.totalBill;
            }
        }
        ConsoleUI.card("REVENUE  \u00B7  " + ConsoleUI.now().substring(0, 11), ConsoleUI.GREEN, new String[][] {
            {"Bills", "" + billingList.size()},
            {"Collected", ConsoleUI.bold(ConsoleUI.GREEN, ConsoleUI.money(paid))},
            {"Pending", ConsoleUI.bold(ConsoleUI.YELLOW, ConsoleUI.money(total - paid))},
            {"-"},
            {"Total Revenue", ConsoleUI.bold(ConsoleUI.CORAL, ConsoleUI.money(total))}
        });
    }

    void generateSummary(ArrayList<Patient> patientList, ArrayList<Doctor> doctorList,
                         ArrayList<Appointment> appointmentList, ArrayList<Prescription> prescriptionList,
                         ArrayList<Billing> billingList) {
        ConsoleUI.blank();
        ConsoleUI.println("  " + ConsoleUI.bold(ConsoleUI.CYAN, "MEDIALERT DASHBOARD")
                + ConsoleUI.dim("  \u00B7  " + ConsoleUI.now()));
        ConsoleUI.tiles(
            new String[] {"PATIENTS", "DOCTORS", "APPOINTMENTS", "PRESCRIPTIONS", "BILLS"},
            new String[] {"" + patientList.size(), "" + doctorList.size(), "" + appointmentList.size(),
                          "" + prescriptionList.size(), "" + billingList.size()},
            new int[] {ConsoleUI.TEAL, ConsoleUI.BLUE, ConsoleUI.PURPLE, ConsoleUI.GREEN, ConsoleUI.CORAL});
    }
}
