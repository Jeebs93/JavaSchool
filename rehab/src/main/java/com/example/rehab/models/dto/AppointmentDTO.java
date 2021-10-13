package com.example.rehab.models.dto;

import com.example.rehab.models.enums.TypeOfAppointment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
@NoArgsConstructor
public class AppointmentDTO {

    public AppointmentDTO(Long patientId, String value, List<String> weekDays,
                          List<String> time, String period, String dose, TypeOfAppointment typeOfAppointment) {
        this.patientId = patientId;
        this.value = value;
        this.weekDays = weekDays;
        this.time = time;
        this.period = period;

        this.timePattern = weekDays.size() == 1 ?
                "One day in a week at " + time.toString() + " for " + period + " weeks." :
                weekDays.size() + " days in a week at " + time.toString() + " for " + period + " weeks.";
        this.dose = dose;
        this.typeOfAppointment = typeOfAppointment;
    }

    private Long id;

    private Long patientId;

    private String value;

    private List<String> weekDays;

    private List<String> time;

    private String period;

    private String timePattern;

    private TypeOfAppointment typeOfAppointment;

    private String dose;


}
