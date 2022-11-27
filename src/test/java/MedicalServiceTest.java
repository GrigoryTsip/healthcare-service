import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MedicalServiceTest {

    @BeforeEach
    public void initMedicalServiceTest() {
        System.out.println("Начало теста");
    }

    @AfterEach
    public void finishMedicalServiceTest() {
        System.out.println("Конец теста");
    }

    @CsvFileSource(files = "src/test/resources/helthinfo.csv")
    @ParameterizedTest
    public void testOfMedicalService(int highPressure, int lowPressure,
                                     BigDecimal temperature,
                                     String message,
                                     String method) {

        PatientInfo id1 = new PatientInfo("id1",
                "Иван", "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))
        );

        PatientInfoRepository fileRepo = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(fileRepo.getById("id1"))
                .thenReturn(id1);
        SendAlertService alert = Mockito.mock(SendAlertServiceImpl.class);

        ArgumentCaptor<SendAlertService> argument = ArgumentCaptor.forClass((SendAlertServiceImpl.class));

        BloodPressure blood = new BloodPressure(highPressure, lowPressure);
        MedicalService medService = new MedicalServiceImpl(fileRepo, alert);


        medService.checkBloodPressure(id1.getId(), blood);
        medService.checkTemperature(id1.getId(), temperature);


        try {
            Mockito.verify(alert, Mockito.atMost(2)).send(String.valueOf(argument.capture()));
            assertEquals(message, String.valueOf(argument.getValue()));
        } catch (Exception e) {
            assertNull(message, "Нет сообщения: ошибка в методе " + method);
        }
    }
}
