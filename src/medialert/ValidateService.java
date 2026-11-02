package medialert;

/** ValidateService Module - validates IDs, mobile numbers and bill amounts. */
public class ValidateService {

    boolean validatePatientId(int patientId) {
        return patientId > 0;
    }

    boolean validateDoctorId(int doctorId) {
        return doctorId > 0;
    }

    boolean validateMobileNumber(long mobileNumber) {
        return String.valueOf(mobileNumber).length() == 10;
    }

    boolean validateBillAmount(double amount) {
        return amount >= 0;
    }
}
