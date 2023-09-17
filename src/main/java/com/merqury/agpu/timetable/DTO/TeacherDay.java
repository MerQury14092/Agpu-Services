package com.merqury.agpu.timetable.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class TeacherDay {
    String date;
    String teacherName;
    List<Discipline> disciplines;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public TeacherDay deleteHolidays(){
        disciplines = disciplines.stream()
                .filter(discipline -> !discipline.getName().equals("HOLIDAY"))
                .collect(Collectors.toList());
        return this;
    }

    public TeacherDay proxy(){
        List<Discipline> proxyList = new ArrayList<>();

        for (Discipline disc: disciplines) {
            Discipline proxyDisc;
            try {
                proxyDisc = disc.proxy();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            proxyList.add(proxyDisc);
        }

        return TeacherDay.builder()
                .date(date)
                .teacherName(teacherName)
                .disciplines(proxyList)
                .build();
    }
}
