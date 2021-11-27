package com.example.rehab.service.impl.unit;


import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.enums.PatientStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.impl.ProceduresAndCuresServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;
import static com.example.rehab.models.enums.TypeOfAppointment.CURE;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProceduresAndCuresImplUnitTests {

    List<ProceduresAndCures> proceduresList = Arrays.asList(
            new ProceduresAndCures(1L,"Procedure1", TypeOfAppointment.PROCEDURE),
            new ProceduresAndCures(2L,"Procedure2", TypeOfAppointment.PROCEDURE),
            new ProceduresAndCures(3L,"Procedure3", TypeOfAppointment.PROCEDURE)
    );

    List<ProceduresAndCures> curesList = Arrays.asList(
            new ProceduresAndCures(1L,"Cure1", TypeOfAppointment.CURE),
            new ProceduresAndCures(2L,"Cure2", TypeOfAppointment.CURE),
            new ProceduresAndCures(3L,"Cure3", TypeOfAppointment.CURE)
    );

    ProceduresAndCuresRepository proceduresAndCuresRepository = Mockito.mock(ProceduresAndCuresRepository.class);
    ProceduresAndCuresServiceImpl proceduresAndCuresService = new ProceduresAndCuresServiceImpl(proceduresAndCuresRepository);

    @Test
    void testFindAllProcedures() {
        Mockito.when(proceduresAndCuresRepository.findAllByTypeOfAppointment(PROCEDURE)).thenReturn(proceduresList);
        List<ProceduresAndCures> list = proceduresAndCuresService.findAllProcedures();
        for (ProceduresAndCures item:list) {
            assertEquals(PROCEDURE,item.getTypeOfAppointment());
        }
    }

    @Test
    void testFindAllCures() {
        Mockito.when(proceduresAndCuresRepository.findAllByTypeOfAppointment(TypeOfAppointment.CURE)).thenReturn(curesList);
        List<ProceduresAndCures> list = proceduresAndCuresService.findAllProcedures();
        for (ProceduresAndCures item:list) {
            assertEquals(CURE,item.getTypeOfAppointment());
        }
    }

    @Test
    void testAddValue() {
        Mockito.doAnswer(invocationOnMock -> {
            ProceduresAndCures item = invocationOnMock.getArgument(0);
            assertEquals(PROCEDURE,item.getTypeOfAppointment());
            assertEquals("TestProcedure",item.getName());
            return null;
        }).when(proceduresAndCuresRepository).save(any(ProceduresAndCures.class));
        proceduresAndCuresService.addValue("procedure","TestProcedure");
        
        Mockito.doAnswer(invocationOnMock -> {
            ProceduresAndCures item = invocationOnMock.getArgument(0);
            assertEquals(CURE,item.getTypeOfAppointment());
            assertEquals("TestCure",item.getName());
            return null;
        }).when(proceduresAndCuresRepository).save(any(ProceduresAndCures.class));
        proceduresAndCuresService.addValue("cure","TestCure");
    }

}
