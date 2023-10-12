package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.TimetableDay;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

@Component
public class TimetableMemory {
    private final List<TimetableDay> memory;
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

    public void addDiscipline(TimetableDay timetableDay){
        memory.add(timetableDay);
        addTaskForCleanDayFromMemory(timetableDay);
    }

    private void addTaskForCleanDayFromMemory(TimetableDay timetableDay){
        async(() -> {
            trySleep(memoryExpirationTime);
            memory.remove(timetableDay);
        });
    }

    private void trySleep(long milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public TimetableDay getDisciplineByDate(String id, String date){
        var res =  getFilteredByIdAndDateTimetableDays(id, date);
        if(!res.isEmpty())
            return res.get(0);
        return getEmptyDateWith(id, date);
    }

    private List<TimetableDay> getFilteredByIdAndDateTimetableDays(String id, String date){
        return memory.stream()
                .filter(day -> (day.getId().equals(id) && day.getDate().equals(date)))
                .toList();
    }

    private TimetableDay getEmptyDateWith(String id, String date){
        return TimetableDay.builder()
                .date(date)
                .id(id)
                .disciplines(Collections.emptyList())
                .build();
    }
}
