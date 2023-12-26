package com.merqury.agpu.timetable.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class Statistics {
    private String startTime, endTime;
    private Map<String, Integer> map;
    public Statistics(){
        this.startTime = getCurrentTime();
        this.endTime = "now";
        map = new HashMap<>();
    }

    public void endRecording(){
        endTime = getCurrentTime();
    }

    private static String getCurrentTime(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        return time.format(formatter);
    }

    public void addRecord(String group){
        if(!map.containsKey(group))
            map.put(group, 1);
        else
            map.put(group, map.get(group)+1);
    }
}
