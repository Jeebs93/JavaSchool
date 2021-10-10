package com.example.rehab.repo;

import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.enums.TypeOfAppointment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProceduresAndCuresRepository extends CrudRepository<ProceduresAndCures, Long> {

    List<ProceduresAndCures> findAll();
    List<ProceduresAndCures> findAllByTypeOfAppointment(TypeOfAppointment typeOfAppointment);

}
