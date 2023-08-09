package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.TeacherDay;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class TeacherTimetableMemory {
    private final List<TeacherDay> memory;

    public TeacherTimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(TeacherDay day){
        log.info("added day: {}", day);
        memory.add(day);
        Thread cleaner = new Thread(() -> {
            log.info("task fo remove added");
            try {
                Thread.sleep(TimeUnit.HOURS.toMillis(4));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("object now removing {}", day);
            memory.remove(day);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public TeacherDay getDisciplineByDate(String teacherName, String date){
        log.info("trying get discipline by date {} and by name {}\nDisciplines in memory:", date, teacherName);
        memory.forEach(log::info);

        var res =  memory.stream()
                .filter(day -> (day.getTeacherName().equals(teacherName) && day.getDate().equals(date)))
                .toList();
        if(!res.isEmpty())
            return res.get(0);


        return TeacherDay.builder()
                .date(date)
                .teacherName(teacherName)
                .disciplines(List.of())
                .build();
    }
}
