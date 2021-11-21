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
                          List<String> time, Integer period, String dose,
                          TypeOfAppointment typeOfAppointment, String timePattern) {
        this.patientId = patientId;
        this.value = value;
        this.weekDays = weekDays;
        this.time = time;
        this.period = period;

        this.timePattern = timePattern;
        this.dose = dose;
        this.typeOfAppointment = typeOfAppointment;
        this.isActive = true;
        this.isCanceled = false;
        this.isCompleted = false;
        this.canceledEvents = 0;
    }

    private Long id;

    private Long patientId;

    private String value;

    private List<String> weekDays;

    private List<String> time;

    private Integer period;

    private Integer canceledEvents;

    private String timePattern;

    private TypeOfAppointment typeOfAppointment;

    private String dose;

    boolean isActive;

    boolean isCompleted;

    boolean isCanceled;

}
