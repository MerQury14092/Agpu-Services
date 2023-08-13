package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.Day;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class TimetableMemory {
    private final List<Day> memory;

    public TimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(Day day){
        log.info("added day: {}", day);
        memory.add(day);
        Thread cleaner = new Thread(() -> {
            log.info("task fo remove added");
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(30));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("object now removing {}", day);
            memory.remove(day);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public Day getDisciplineByDate(String groupName, String date){
        log.info("trying get discipline by date {} and by name {}\nDisciplines in memory:", date, groupName);
        memory.forEach(log::info);

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
