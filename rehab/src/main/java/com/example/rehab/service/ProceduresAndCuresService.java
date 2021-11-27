package com.example.rehab.service;

import com.example.rehab.models.ProceduresAndCures;

import java.util.List;

public interface ProceduresAndCuresService {

    /**
     * Finds all procedures and sorts them in alphabetic order
     * @author Dmitriy Zhiburtovich
     */
    List<ProceduresAndCures> findAllProcedures();

    /**
     * Finds all cures and sorts them in alphabetic order
     * @author Dmitriy Zhiburtovich
     */
    List<ProceduresAndCures> findAllCures();

    /**
     * Adds cure or procedure(depends on type) in the db. If name is not unique - throws exception
     * @author Dmitriy Zhiburtovich
     */
    void addValue(String type, String name);
}
