package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.Day;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

@Component
public class TimetableMemory {
    private final List<Day> memory;
    private static long memoryExpirationTime;

    static {
        memoryExpirationTime = TimeUnit.HOURS.toMillis(2);
    }

    public static void setMemoryExpirationTime(long newValue){
        memoryExpirationTime = newValue;
    }

    public TimetableMemory(){
        memory = new ArrayList<>();
    }

    public void addDiscipline(Day day){
        memory.add(day);
        addTaskForCleanDayFromMemory(day);
    }

    private void addTaskForCleanDayFromMemory(Day day){
        async(() -> {
            trySleep(memoryExpirationTime);
            memory.remove(day);
        });
    }

    private void trySleep(long milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Day getDisciplineByDate(String id, String date){
        var res =  getFilteredByIdAndDateTimetableDays(id, date);
        if(!res.isEmpty())
            return res.get(0);
        return getEmptyDateWith(id, date);
    }

    private List<Day> getFilteredByIdAndDateTimetableDays(String id, String date){
        return memory.stream()
                .filter(day -> (day.getId().equals(id) && day.getDate().equals(date)))
                .toList();
    }

    private Day getEmptyDateWith(String id, String date){
        return Day.builder()
                .date(date)
                .id(id)
                .disciplines(Collections.emptyList())
                .build();
    }
}
