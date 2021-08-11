package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;

class MedicalServiceImplTest {
    static PatientInfoRepository repoMock;
    static SendAlertService alertMock;
    static MedicalService medicalService;
    static final String PATIENT_ID = "fake-patient-ID";
    static final String ALARM_MSG = String.format("Warning, patient with id: %s, need help", PATIENT_ID);

    static final PatientInfo PATIENT_INFO = new PatientInfo(PATIENT_ID,
            "Имя", "Фамилия", LocalDate.of(2000, 1, 1),
            new HealthInfo(new BigDecimal("36.6"), new BloodPressure(110, 70)));

    @BeforeAll
    static void setUp() {
        repoMock = Mockito.mock(PatientInfoRepository.class);
        alertMock = Mockito.mock(SendAlertService.class);
        medicalService = new MedicalServiceImpl(repoMock, alertMock);
        Mockito.when(repoMock.getById(PATIENT_ID)).thenReturn(PATIENT_INFO);
    }

    @Test
    void warns_if_pressure_issue() {
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        medicalService.checkBloodPressure(PATIENT_ID, new BloodPressure(120, 80));

        Mockito.verify(alertMock, Mockito.only()).send(msgCaptor.capture());

        assertEquals(ALARM_MSG, msgCaptor.getValue());
    }

    @Test
    void warns_if_temperature_issue() {
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        medicalService.checkTemperature(PATIENT_ID, new BigDecimal("35"));

        Mockito.verify(alertMock, Mockito.only()).send(msgCaptor.capture());

        assertEquals(ALARM_MSG, msgCaptor.getValue());
    }

    @Test
    void no_warning_if_ok() {
        medicalService.checkBloodPressure(PATIENT_ID, new BloodPressure(110, 70));
        medicalService.checkTemperature(PATIENT_ID, new BigDecimal("40"));
        Mockito.verify(alertMock, Mockito.never()).send(any());
    }
}