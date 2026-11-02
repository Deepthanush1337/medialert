package medialert;

import java.util.ArrayList;

/** SearchService Module - finds records by ID. Returns null when not found. */
public class SearchService {

    Patient searchPatient(ArrayList<Patient> patientList, int patientId) {
        for (Patient p : patientList) {
            if (p.patientId == patientId) {
                return p;
            }
        }
        return null;
    }

    Doctor searchDoctor(ArrayList<Doctor> doctorList, int doctorId) {
        for (Doctor d : doctorList) {
            if (d.doctorId == doctorId) {
                return d;
            }
        }
        return null;
    }

    Appointment searchAppointment(ArrayList<Appointment> appointmentList, int appointmentId) {
        for (Appointment a : appointmentList) {
            if (a.appointmentId == appointmentId) {
                return a;
            }
        }
        return null;
    }

    Prescription searchPrescription(ArrayList<Prescription> prescriptionList, int prescriptionId) {
        for (Prescription pr : prescriptionList) {
            if (pr.prescriptionId == prescriptionId) {
                return pr;
            }
        }
        return null;
    }

    Billing searchBill(ArrayList<Billing> billingList, int billId) {
        for (Billing b : billingList) {
            if (b.billId == billId) {
                return b;
            }
        }
        return null;
    }
}
