package com.example.rehab.service.impl;

import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.ProceduresAndCuresService;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.rehab.models.enums.TypeOfAppointment.PROCEDURE;
import static com.example.rehab.models.enums.TypeOfAppointment.CURE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceduresAndCuresServiceImpl implements ProceduresAndCuresService {

    private final ProceduresAndCuresRepository proceduresAndCuresRepository;

    public List<ProceduresAndCures> findAllProcedures() {
      List<ProceduresAndCures> result= proceduresAndCuresRepository.findAllByTypeOfAppointment(PROCEDURE);
      Collections.sort(result, Comparator.comparing(ProceduresAndCures::getName));
      return result;
    }

    public List<ProceduresAndCures> findAllCures() {
        List<ProceduresAndCures> result= proceduresAndCuresRepository.findAllByTypeOfAppointment(CURE);
        Collections.sort(result, Comparator.comparing(ProceduresAndCures::getName));
        return result;
    }

    public void addValue(String type,String name) {
        ProceduresAndCures item = new ProceduresAndCures();
        List<ProceduresAndCures> list = proceduresAndCuresRepository.findAll();

        for(ProceduresAndCures obj:list) {
            if (obj.getName().equals(name)) throw new IllegalStateException();
        }

        item.setName(name);
        if (type.equals("procedure")) {
            item.setTypeOfAppointment(PROCEDURE);
        } else {
            item.setTypeOfAppointment(CURE);
        }
        proceduresAndCuresRepository.save(item);
        log.info(String.format("Item '%s' has been added to procedures and cures",name));
    }
}
