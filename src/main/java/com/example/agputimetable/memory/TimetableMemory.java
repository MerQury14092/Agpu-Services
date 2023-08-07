package com.example.agputimetable.memory;

import com.example.agputimetable.model.Discipline;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Log4j2
public class TimetableMemory {
    private final List<Discipline> memory;

    public TimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(Discipline discipline){
        log.info("added discipline: {}", discipline);
        memory.add(discipline);
        Thread cleaner = new Thread(() -> {
            log.info("task fo remove added");
            try {
                Thread.sleep(TimeUnit.HOURS.toMillis(4));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("object now removing {}", discipline);
            memory.remove(discipline);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public List<Discipline> getDisciplineByDate(String date){
        log.info("trying get discipline by date {}\nDisciplines in memory:", date);
        memory.forEach(log::info);

        return memory.stream().filter(discipline -> discipline.getDate().equals(date)).collect(Collectors.toList());
    }
}
