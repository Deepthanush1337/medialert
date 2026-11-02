package medialert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MediAlert - Clinic Appointment & Prescription Manager.
 * Main Module: integrates all modules through a console menu.
 *
 * Run:  java -cp out medialert.Main  [--demo] [--fast] [--no-color]
 */
public class Main {

    static final String DATA_DIR = "data";

    static ArrayList<Patient> patientList = new ArrayList<>();
    static ArrayList<Doctor> doctorList = new ArrayList<>();
    static ArrayList<Appointment> appointmentList = new ArrayList<>();
    static ArrayList<Prescription> prescriptionList = new ArrayList<>();
    static ArrayList<Billing> billingList = new ArrayList<>();

    static ValidateService validateService = new ValidateService();
    static SearchService searchService = new SearchService();
    static ReportGenerationService reportGenerationService = new ReportGenerationService();
    static AnalysisService analysisService = new AnalysisService();

    public static void main(String[] args) {
        ConsoleUI.init(args);
        ConsoleUI.banner();
        boot();
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = InputHelper.readChoice("", 8);
            running = processChoice(choice);
        }
    }

    static void boot() {
        ConsoleUI.bootStep("Java runtime", "JDK " + System.getProperty("java.version"));
        ConsoleUI.bootStep("Core modules", "Patient \u00B7 Doctor \u00B7 Appointment \u00B7 Prescription \u00B7 Billing");
        ConsoleUI.bootStep("Services", "Validate \u00B7 Search \u00B7 Report \u00B7 Analysis");
        loadRecords();
        ConsoleUI.progressBar("Booting", 30);
        ConsoleUI.info("System ready  " + ConsoleUI.dim(ConsoleUI.now()) + "   "
                + ConsoleUI.dim("type ") + ConsoleUI.fg(ConsoleUI.YELLOW, "help") + ConsoleUI.dim(" for commands"));
    }

    static void displayMenu() {
        ConsoleUI.blank();
        ConsoleUI.boxTop("MAIN MENU", ConsoleUI.bold(ConsoleUI.GREEN, "\u25CF ONLINE"));
        ConsoleUI.boxLine(ConsoleUI.option(1, "Patient Management", "register \u00B7 update \u00B7 list"));
        ConsoleUI.boxLine(ConsoleUI.option(2, "Doctor Management", "doctors \u00B7 availability"));
        ConsoleUI.boxLine(ConsoleUI.option(3, "Appointment Management", "book \u00B7 reschedule \u00B7 cancel"));
        ConsoleUI.boxLine(ConsoleUI.option(4, "Prescription Management", "create \u00B7 update \u00B7 print"));
        ConsoleUI.boxLine(ConsoleUI.option(5, "Billing Management", "bill \u00B7 invoice \u00B7 payment"));
        ConsoleUI.boxLine(ConsoleUI.option(6, "Reports", "detailed record reports"));
        ConsoleUI.boxLine(ConsoleUI.option(7, "Analytics", "charts \u00B7 revenue \u00B7 dashboard"));
        ConsoleUI.boxLine(ConsoleUI.backOption(8, "Save & Exit"));
        ConsoleUI.boxDivider();
        ConsoleUI.boxLine(stat(ConsoleUI.TEAL, patientList.size(), "patients")
                + stat(ConsoleUI.BLUE, doctorList.size(), "doctors")
                + stat(ConsoleUI.PURPLE, appointmentList.size(), "appts")
                + stat(ConsoleUI.GREEN, prescriptionList.size(), "Rx")
                + stat(ConsoleUI.CORAL, billingList.size(), "bills"));
        ConsoleUI.boxBottom();
    }

    private static String stat(int color, int n, String label) {
        return ConsoleUI.fg(color, "\u25CF ") + ConsoleUI.bold(ConsoleUI.WHITE, "" + n) + " " + ConsoleUI.dim(label) + "   ";
    }

    static boolean processChoice(int choice) {
        switch (choice) {
            case InputHelper.REDRAW: break;
            case 1: patientMenu(); break;
            case 2: doctorMenu(); break;
            case 3: appointmentMenu(); break;
            case 4: prescriptionMenu(); break;
            case 5: billingMenu(); break;
            case 6: reportsMenu(); break;
            case 7: analyticsMenu(); break;
            case 8:
                saveAndExit();
                return false;
            default:
                ConsoleUI.fail("Invalid choice '" + choice + "'. Pick 1 - 8.");
        }
        return true;
    }

    private static void invalid() {
        ConsoleUI.fail("Invalid choice. Please try again.");
    }

    // ------------------------------------------------------------------ 1. Patient

    static void patientMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("PATIENT MANAGEMENT", new String[][] {
                {"Register Patient", "add a new patient"},
                {"Update Patient", "edit by Patient ID"},
                {"Display All Patients", "table view"}
            });
            int choice = InputHelper.readChoice("patients", 4);

            if (choice == 1) {
                Patient p = new Patient();
                p.registerPatient();
                if (!validateService.validatePatientId(p.patientId)) {
                    ConsoleUI.fail("Patient ID must be greater than 0. Patient not added.");
                } else if (!validateService.validateMobileNumber(p.mobileNumber)) {
                    ConsoleUI.fail("Mobile number must be exactly 10 digits. Patient not added.");
                } else if (searchService.searchPatient(patientList, p.patientId) != null) {
                    ConsoleUI.fail("Patient ID " + p.patientId + " already exists. Patient not added.");
                } else {
                    patientList.add(p);
                    ConsoleUI.ok("Patient " + ConsoleUI.bold(ConsoleUI.WHITE, p.patientName)
                            + " registered " + ConsoleUI.dim("(ID " + p.patientId + ")"));
                }
            } else if (choice == 2) {
                int id = InputHelper.readInt("Patient ID to update");
                Patient p = searchService.searchPatient(patientList, id);
                if (p != null) {
                    p.updatePatient();
                    ConsoleUI.ok("Patient #" + id + " updated.");
                } else {
                    ConsoleUI.fail("Patient #" + id + " not found.");
                }
            } else if (choice == 3) {
                displayAllPatients();
            } else if (choice == 4) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    static void displayAllPatients() {
        if (patientList.isEmpty()) {
            ConsoleUI.warn("No patients found.");
            return;
        }
        List<String[]> rows = ConsoleUI.rows();
        for (Patient p : patientList) {
            rows.add(new String[] {
                ConsoleUI.bold(ConsoleUI.YELLOW, "" + p.patientId),
                ConsoleUI.fg(ConsoleUI.WHITE, ConsoleUI.cut(p.patientName, 22)),
                "" + p.age, p.gender, "" + p.mobileNumber,
                ConsoleUI.fg(ConsoleUI.BLUE, ConsoleUI.cut(p.email, 24))
            });
        }
        ConsoleUI.table(new String[] {"ID", "Name", "Age", "Gender", "Mobile", "Email"}, rows);
    }

    // ------------------------------------------------------------------ 2. Doctor

    static void doctorMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("DOCTOR MANAGEMENT", new String[][] {
                {"Add Doctor", "new doctor profile"},
                {"Update Doctor", "edit by Doctor ID"},
                {"Update Availability", "available / away"},
                {"Display All Doctors", "table view"}
            });
            int choice = InputHelper.readChoice("doctors", 5);

            if (choice == 1) {
                Doctor d = new Doctor();
                d.addDoctor();
                if (!validateService.validateDoctorId(d.doctorId)) {
                    ConsoleUI.fail("Doctor ID must be greater than 0. Doctor not added.");
                } else if (!validateService.validateBillAmount(d.consultationFee)) {
                    ConsoleUI.fail("Consultation fee cannot be negative. Doctor not added.");
                } else if (searchService.searchDoctor(doctorList, d.doctorId) != null) {
                    ConsoleUI.fail("Doctor ID " + d.doctorId + " already exists. Doctor not added.");
                } else {
                    doctorList.add(d);
                    ConsoleUI.ok(ConsoleUI.bold(ConsoleUI.WHITE, d.doctorName) + " added "
                            + ConsoleUI.dim("(" + d.specialization + ")"));
                }
            } else if (choice == 2) {
                int id = InputHelper.readInt("Doctor ID to update");
                Doctor d = searchService.searchDoctor(doctorList, id);
                if (d != null) {
                    d.updateDoctor();
                    ConsoleUI.ok("Doctor #" + id + " updated.");
                } else {
                    ConsoleUI.fail("Doctor #" + id + " not found.");
                }
            } else if (choice == 3) {
                int id = InputHelper.readInt("Doctor ID");
                Doctor d = searchService.searchDoctor(doctorList, id);
                if (d != null) {
                    d.updateAvailability();
                    ConsoleUI.ok(d.doctorName + " is now " + ConsoleUI.yesNo(d.availability));
                } else {
                    ConsoleUI.fail("Doctor #" + id + " not found.");
                }
            } else if (choice == 4) {
                displayAllDoctors();
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    static void displayAllDoctors() {
        if (doctorList.isEmpty()) {
            ConsoleUI.warn("No doctors found.");
            return;
        }
        List<String[]> rows = ConsoleUI.rows();
        for (Doctor d : doctorList) {
            rows.add(new String[] {
                ConsoleUI.bold(ConsoleUI.YELLOW, "" + d.doctorId),
                ConsoleUI.fg(ConsoleUI.WHITE, ConsoleUI.cut(d.doctorName, 22)),
                ConsoleUI.cut(d.specialization, 20),
                ConsoleUI.money(d.consultationFee),
                ConsoleUI.yesNo(d.availability)
            });
        }
        ConsoleUI.table(new String[] {"ID", "Name", "Specialization", "Fee", "Status"}, rows);
    }

    // ------------------------------------------------------------------ 3. Appointment

    static void appointmentMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("APPOINTMENT MANAGEMENT", new String[][] {
                {"Book Appointment", "patient + doctor + date"},
                {"Reschedule Appointment", "pick a new date"},
                {"Cancel Appointment", "mark as cancelled"},
                {"Display All Appointments", "table view"}
            });
            int choice = InputHelper.readChoice("appointments", 5);

            if (choice == 1) {
                Appointment a = new Appointment();
                a.bookAppointment();
                if (searchService.searchAppointment(appointmentList, a.appointmentId) != null) {
                    ConsoleUI.fail("Appointment ID " + a.appointmentId + " already exists. Not booked.");
                } else {
                    if (searchService.searchPatient(patientList, a.patientId) == null) {
                        ConsoleUI.warn("Patient #" + a.patientId + " is not registered yet.");
                    }
                    if (searchService.searchDoctor(doctorList, a.doctorId) == null) {
                        ConsoleUI.warn("Doctor #" + a.doctorId + " is not in the system yet.");
                    }
                    appointmentList.add(a);
                    ConsoleUI.ok("Appointment #" + a.appointmentId + " booked for "
                            + ConsoleUI.bold(ConsoleUI.WHITE, a.appointmentDate) + "  " + ConsoleUI.status(a.appointmentStatus));
                }
            } else if (choice == 2) {
                int id = InputHelper.readInt("Appointment ID");
                Appointment a = searchService.searchAppointment(appointmentList, id);
                if (a != null) {
                    a.rescheduleAppointment();
                    ConsoleUI.ok("Appointment #" + id + " moved to "
                            + ConsoleUI.bold(ConsoleUI.WHITE, a.appointmentDate) + "  " + ConsoleUI.status(a.appointmentStatus));
                } else {
                    ConsoleUI.fail("Appointment #" + id + " not found.");
                }
            } else if (choice == 3) {
                int id = InputHelper.readInt("Appointment ID");
                Appointment a = searchService.searchAppointment(appointmentList, id);
                if (a != null) {
                    a.cancelAppointment();
                    ConsoleUI.ok("Appointment #" + id + "  " + ConsoleUI.status(a.appointmentStatus));
                } else {
                    ConsoleUI.fail("Appointment #" + id + " not found.");
                }
            } else if (choice == 4) {
                displayAllAppointments();
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    static void displayAllAppointments() {
        if (appointmentList.isEmpty()) {
            ConsoleUI.warn("No appointments found.");
            return;
        }
        List<String[]> rows = ConsoleUI.rows();
        for (Appointment a : appointmentList) {
            Patient p = searchService.searchPatient(patientList, a.patientId);
            Doctor d = searchService.searchDoctor(doctorList, a.doctorId);
            rows.add(new String[] {
                ConsoleUI.bold(ConsoleUI.YELLOW, "" + a.appointmentId),
                a.patientId + ConsoleUI.dim(p == null ? "" : " " + ConsoleUI.cut(p.patientName, 16)),
                a.doctorId + ConsoleUI.dim(d == null ? "" : " " + ConsoleUI.cut(d.doctorName, 18)),
                ConsoleUI.fg(ConsoleUI.WHITE, a.appointmentDate),
                ConsoleUI.status(a.appointmentStatus)
            });
        }
        ConsoleUI.table(new String[] {"ID", "Patient", "Doctor", "Date", "Status"}, rows);
    }

    // ------------------------------------------------------------------ 4. Prescription

    static void prescriptionMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("PRESCRIPTION MANAGEMENT", new String[][] {
                {"Create Prescription", "diagnosis + medicines"},
                {"Update Prescription", "edit by Prescription ID"},
                {"Display All Prescriptions", "table view"},
                {"Print Prescription", "printable Rx slip"}
            });
            int choice = InputHelper.readChoice("prescriptions", 5);

            if (choice == 1) {
                Prescription pr = new Prescription();
                pr.createPrescription();
                if (searchService.searchPrescription(prescriptionList, pr.prescriptionId) != null) {
                    ConsoleUI.fail("Prescription ID " + pr.prescriptionId + " already exists. Not created.");
                } else {
                    prescriptionList.add(pr);
                    ConsoleUI.ok("Prescription #" + pr.prescriptionId + " created for patient #" + pr.patientId);
                }
            } else if (choice == 2) {
                int id = InputHelper.readInt("Prescription ID");
                Prescription pr = searchService.searchPrescription(prescriptionList, id);
                if (pr != null) {
                    pr.updatePrescription();
                    ConsoleUI.ok("Prescription #" + id + " updated.");
                } else {
                    ConsoleUI.fail("Prescription #" + id + " not found.");
                }
            } else if (choice == 3) {
                displayAllPrescriptions();
            } else if (choice == 4) {
                int id = InputHelper.readInt("Prescription ID");
                Prescription pr = searchService.searchPrescription(prescriptionList, id);
                if (pr != null) {
                    pr.printPrescription();
                } else {
                    ConsoleUI.fail("Prescription #" + id + " not found.");
                }
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    static void displayAllPrescriptions() {
        if (prescriptionList.isEmpty()) {
            ConsoleUI.warn("No prescriptions found.");
            return;
        }
        List<String[]> rows = ConsoleUI.rows();
        for (Prescription pr : prescriptionList) {
            rows.add(new String[] {
                ConsoleUI.bold(ConsoleUI.YELLOW, "" + pr.prescriptionId),
                "" + pr.patientId, "" + pr.doctorId,
                ConsoleUI.fg(ConsoleUI.WHITE, ConsoleUI.cut(pr.diagnosis, 18)),
                ConsoleUI.fg(ConsoleUI.CYAN, ConsoleUI.cut(pr.medicines, 22)),
                ConsoleUI.cut(pr.dosage, 18)
            });
        }
        ConsoleUI.table(new String[] {"ID", "Patient", "Doctor", "Diagnosis", "Medicines", "Dosage"}, rows);
    }

    // ------------------------------------------------------------------ 5. Billing

    static void billingMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("BILLING MANAGEMENT", new String[][] {
                {"Calculate Bill", "fee + medicine charge"},
                {"Generate Invoice", "printable invoice"},
                {"Receive Payment", "mark bill as paid"},
                {"Display All Bills", "table view"}
            });
            int choice = InputHelper.readChoice("billing", 5);

            if (choice == 1) {
                Billing b = new Billing();
                b.calculateBill();
                if (!validateService.validateBillAmount(b.consultationFee)
                        || !validateService.validateBillAmount(b.medicineCharge)) {
                    ConsoleUI.fail("Amounts cannot be negative. Bill not added.");
                } else if (searchService.searchBill(billingList, b.billId) != null) {
                    ConsoleUI.fail("Bill ID " + b.billId + " already exists. Bill not added.");
                } else {
                    billingList.add(b);
                    ConsoleUI.ok("Bill #" + b.billId + " total = "
                            + ConsoleUI.bold(ConsoleUI.CORAL, ConsoleUI.money(b.totalBill)));
                }
            } else if (choice == 2) {
                int id = InputHelper.readInt("Bill ID");
                Billing b = searchService.searchBill(billingList, id);
                if (b != null) {
                    b.generateInvoice();
                } else {
                    ConsoleUI.fail("Bill #" + id + " not found.");
                }
            } else if (choice == 3) {
                int id = InputHelper.readInt("Bill ID");
                Billing b = searchService.searchBill(billingList, id);
                if (b != null) {
                    b.receivePayment();
                } else {
                    ConsoleUI.fail("Bill #" + id + " not found.");
                }
            } else if (choice == 4) {
                displayAllBills();
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    static void displayAllBills() {
        if (billingList.isEmpty()) {
            ConsoleUI.warn("No bills found.");
            return;
        }
        List<String[]> rows = ConsoleUI.rows();
        for (Billing b : billingList) {
            rows.add(new String[] {
                ConsoleUI.bold(ConsoleUI.YELLOW, "" + b.billId),
                "" + b.patientId,
                ConsoleUI.money(b.consultationFee),
                ConsoleUI.money(b.medicineCharge),
                ConsoleUI.bold(ConsoleUI.CORAL, ConsoleUI.money(b.totalBill)),
                b.paid ? ConsoleUI.bold(ConsoleUI.GREEN, "\u25CF PAID") : ConsoleUI.bold(ConsoleUI.YELLOW, "\u25CF PENDING")
            });
        }
        ConsoleUI.table(new String[] {"Bill", "Patient", "Consultation", "Medicines", "Total", "Payment"}, rows);
    }

    // ------------------------------------------------------------------ 6. Reports

    static void reportsMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("REPORTS", new String[][] {
                {"Patient Report", "by Patient ID"},
                {"Appointment Report", "by Appointment ID"},
                {"Prescription Report", "by Prescription ID"},
                {"Billing Report", "by Bill ID"}
            });
            int choice = InputHelper.readChoice("reports", 5);

            if (choice == 1) {
                Patient p = searchService.searchPatient(patientList, InputHelper.readInt("Patient ID"));
                if (p != null) {
                    reportGenerationService.generatePatientReport(p);
                } else {
                    ConsoleUI.fail("Patient not found.");
                }
            } else if (choice == 2) {
                Appointment a = searchService.searchAppointment(appointmentList, InputHelper.readInt("Appointment ID"));
                if (a != null) {
                    reportGenerationService.generateAppointmentReport(a);
                } else {
                    ConsoleUI.fail("Appointment not found.");
                }
            } else if (choice == 3) {
                Prescription pr = searchService.searchPrescription(prescriptionList, InputHelper.readInt("Prescription ID"));
                if (pr != null) {
                    reportGenerationService.generatePrescriptionReport(pr);
                } else {
                    ConsoleUI.fail("Prescription not found.");
                }
            } else if (choice == 4) {
                Billing b = searchService.searchBill(billingList, InputHelper.readInt("Bill ID"));
                if (b != null) {
                    reportGenerationService.generateBillingReport(b);
                } else {
                    ConsoleUI.fail("Bill not found.");
                }
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    // ------------------------------------------------------------------ 7. Analytics

    static void analyticsMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.subMenu("ANALYTICS", new String[][] {
                {"Doctor-wise Consultations", "bar chart"},
                {"Most Visited Doctor", "top doctor"},
                {"Calculate Daily Revenue", "collected vs pending"},
                {"Summary", "live dashboard"}
            });
            int choice = InputHelper.readChoice("analytics", 5);

            if (choice == 1) {
                analysisService.doctorWiseConsultations(appointmentList, doctorList);
            } else if (choice == 2) {
                analysisService.identifyMostVisitedDoctor(appointmentList, doctorList);
            } else if (choice == 3) {
                analysisService.calculateDailyRevenue(billingList);
            } else if (choice == 4) {
                analysisService.generateSummary(patientList, doctorList, appointmentList,
                        prescriptionList, billingList);
            } else if (choice == 5) {
                back = true;
            } else if (choice != InputHelper.REDRAW) {
                invalid();
            }
        }
    }

    // ------------------------------------------------------------------ 8. Save & Exit

    static void saveAndExit() {
        ConsoleUI.blank();
        ConsoleUI.info("Writing records to ./" + DATA_DIR + "/");
        boolean ok = saveList(patientList, "patientList.dat")
                & saveList(doctorList, "doctorList.dat")
                & saveList(appointmentList, "appointmentList.dat")
                & saveList(prescriptionList, "prescriptionList.dat")
                & saveList(billingList, "billingList.dat");
        if (ok) {
            ConsoleUI.ok("All records saved.");
        }
        exitApplication();
    }

    static void exitApplication() {
        ConsoleUI.blank();
        ConsoleUI.heartbeat();
        ConsoleUI.println("  " + ConsoleUI.bold(ConsoleUI.CYAN, "Thank you for using MediAlert.")
                + ConsoleUI.dim("  Stay healthy!  ") + ConsoleUI.fg(ConsoleUI.CORAL, "\u2665"));
        ConsoleUI.println("  " + ConsoleUI.dim("session closed  \u00B7  " + ConsoleUI.now()));
        ConsoleUI.blank();
    }

    static void loadRecords() {
        patientList = loadList("patientList.dat");
        doctorList = loadList("doctorList.dat");
        appointmentList = loadList("appointmentList.dat");
        prescriptionList = loadList("prescriptionList.dat");
        billingList = loadList("billingList.dat");
        int total = patientList.size() + doctorList.size() + appointmentList.size()
                + prescriptionList.size() + billingList.size();
        ConsoleUI.bootStep("Data store", total > 0
                ? total + " records restored from ./" + DATA_DIR
                : "fresh start - no saved records");
    }

    private static boolean saveList(ArrayList<?> list, String fileName) {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            ConsoleUI.fail("Could not create the '" + DATA_DIR + "' folder.");
            return false;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(dir, fileName)))) {
            out.writeObject(list);
            ConsoleUI.bootStep(fileName, list.size() + (list.size() == 1 ? " record" : " records"));
            return true;
        } catch (IOException e) {
            ConsoleUI.fail("Could not save " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ArrayList<T> loadList(String fileName) {
        File file = new File(DATA_DIR, fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<T>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            ConsoleUI.warn("Could not read " + fileName + " - starting with an empty list.");
            return new ArrayList<>();
        }
    }
}
