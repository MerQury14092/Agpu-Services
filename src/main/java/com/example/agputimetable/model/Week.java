package com.example.agputimetable.model;

import lombok.Data;

import java.util.HashMap;

@Data
public class Week {
    private int id;
    private String from;
    private String to;
    private HashMap<String, String> dayNames;
}
