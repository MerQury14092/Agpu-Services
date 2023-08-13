package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.Day;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TimetableMemory {
    private final List<Day> memory;

    public TimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(Day day){
        memory.add(day);
        Thread cleaner = new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(30));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            memory.remove(day);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public Day getDisciplineByDate(String groupName, String date){

        var res =  memory.stream()
                .filter(day -> (day.getGroupName().equals(groupName) && day.getDate().equals(date)))
                .toList();
        if(!res.isEmpty())
            return res.get(0);

        return Day.builder()
                .date(date)
                .groupName(groupName)
                .disciplines(Collections.emptyList())
                .build();
    }

    public void rm(Day day){
        memory.remove(day);
    }
}
