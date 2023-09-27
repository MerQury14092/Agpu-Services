package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.GroupDay;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class TimetableMemory {
    private final List<GroupDay> memory;

    public TimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(GroupDay groupDay){
        log.info("added day: {}", groupDay);
        memory.add(groupDay);
        Thread cleaner = new Thread(() -> {
            log.info("task fo remove added");
            try {
                Thread.sleep(TimeUnit.HOURS.toMillis(3));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("object now removing {}", groupDay);
            memory.remove(groupDay);
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public GroupDay getDisciplineByDate(String groupName, String date){
        log.info("trying get discipline by date {} and by name {}\nDisciplines in memory:", date, groupName);
        memory.forEach(log::info);

        var res =  memory.stream()
                .filter(groupGroupDay -> (groupGroupDay.getGroupName().equals(groupName) && groupGroupDay.getDate().equals(date)))
                .toList();
        if(!res.isEmpty())
            return res.get(0);

        return GroupDay.builder()
                .date(date)
                .groupName(groupName)
                .disciplines(Collections.emptyList())
                .build();
    }
}
