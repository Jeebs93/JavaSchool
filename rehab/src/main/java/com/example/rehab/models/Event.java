package com.example.rehab.models;

import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.models.enums.TypeOfAppointment;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Patient patient;

    private LocalDateTime date;

    private EventStatus eventStatus;

    private TypeOfAppointment typeOfAppointment;

}
