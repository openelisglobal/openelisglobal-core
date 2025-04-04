package us.mn.state.health.lims.common;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.PersonService;
import us.mn.state.health.lims.patient.valueholder.Patient;

class PatientServiceTest {

    private PersonService mockPersonService;

    @BeforeEach
    void setUp() {
        mockPersonService = mock(PersonService.class);
    }

    @Test
    void savePatient_withValidPhone_shouldPass() {
        // Mock returns valid phone
        when(mockPersonService.getPhone()).thenReturn("1234567890");

        PatientService patientService = new PatientService(new Patient(), mockPersonService);

        assertDoesNotThrow(() -> {
            patientService.validatePhoneNumber();
        });
    }

    @Test
    void savePatient_withInvalidPhone_shouldThrowValidationException() {
        // Mock returns invalid phone
        when(mockPersonService.getPhone()).thenReturn("12345");

        PatientService patientService = new PatientService(new Patient(), mockPersonService);

        assertThrows(ValidationException.class, () -> {
            patientService.validatePhoneNumber();
        });
    }
}
