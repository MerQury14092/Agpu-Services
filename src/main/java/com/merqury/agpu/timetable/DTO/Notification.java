package com.merqury.agpu.timetable.DTO;

import lombok.Data;

@Data
public class Notification {

    public static Notification noChanges(String date){
        return new Notification(false, date);
    }
    public static Notification thereAreChanges(String date){
        return new Notification(true, date);
    }

    private Notification(boolean hadChanges, String date){
        this.date = date;
        this.thereAreChanges = hadChanges;
    }

    private boolean thereAreChanges;
    private String date;
}
