package com.example.rehab.service;

import com.example.rehab.models.ProceduresAndCures;

import java.util.List;

public interface ProceduresAndCuresService {

    List<ProceduresAndCures> findAllProcedures();

    List<ProceduresAndCures> findAllCures();

    void addValue(String type, String name);
}
