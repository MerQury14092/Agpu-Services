package com.example.agputimetable.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Discipline {
    private String date;
    private String time;
    private String name;
    private String teacherName;
    private String audienceId;
    private int subgroup;
    private String groupName;
    @JsonIgnore
    private int colspan;

    public Discipline(String date) {
        this.date = date;
    }

    public Discipline() {
    }
}
