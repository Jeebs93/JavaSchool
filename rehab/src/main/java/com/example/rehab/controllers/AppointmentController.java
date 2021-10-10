package com.example.rehab.controllers;

import com.example.rehab.models.ProceduresAndCures;
import com.example.rehab.models.dto.PatientDTO;
import com.example.rehab.models.enums.TypeOfAppointment;
import com.example.rehab.repo.ProceduresAndCuresRepository;
import com.example.rehab.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AppointmentController {

    private PatientService patientService;
    private ProceduresAndCuresRepository proceduresAndCuresRepository;

    @Autowired
    public AppointmentController (PatientService patientService,
                                  ProceduresAndCuresRepository proceduresAndCuresRepository) {
        this.patientService = patientService;
        this.proceduresAndCuresRepository = proceduresAndCuresRepository;
    }


    @GetMapping("/working-with-patients/{id}/add-procedure")
    public String addProcedure(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        model.addAttribute("patient", result);
        List<ProceduresAndCures> proceduresAndCures =
                proceduresAndCuresRepository
                        .findAllByTypeOfAppointment(Enum.valueOf(TypeOfAppointment.class, "PROCEDURE"));
        model.addAttribute("procedures", proceduresAndCures);
        return "add-procedure";
    }

    @GetMapping("/working-with-patients/{id}/add-cure")
    public String addCure(@PathVariable(value = "id") long id, Model model) {
        PatientDTO result = patientService.getPatientDTObyID(id);
        model.addAttribute("patient", result);
        List<ProceduresAndCures> proceduresAndCures =
                proceduresAndCuresRepository
                        .findAllByTypeOfAppointment(Enum.valueOf(TypeOfAppointment.class, "CURE"));
        model.addAttribute("cures", proceduresAndCures);
        return "add-cure";
    }


}
