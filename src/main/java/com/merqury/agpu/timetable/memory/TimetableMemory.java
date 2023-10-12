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
                Thread.sleep(TimeUnit.HOURS.toMillis(2));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            memory.remove(day);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public Day getDisciplineByDate(String id, String date){
        
        var res =  memory.stream()
                .filter(day -> (day.getId().equals(id) && day.getDate().equals(date)))
                .toList();
        if(!res.isEmpty())
            return res.get(0);

        return Day.builder()
                .date(date)
                .id(id)
                .disciplines(Collections.emptyList())
                .build();
    }
}
