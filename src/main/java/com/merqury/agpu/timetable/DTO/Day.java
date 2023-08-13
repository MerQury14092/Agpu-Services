package com.merqury.agpu.timetable.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class Day {
    String date;
    String groupName;
    List<Discipline> disciplines;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public Day deleteHolidays(){
        disciplines = disciplines.stream()
                .filter(discipline -> !discipline.getName().equals("HOLIDAY"))
                .collect(Collectors.toList());
        return this;
    }

    public Day proxy(){
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

        return Day.builder()
                .date(date)
                .groupName(groupName)
                .disciplines(proxyList)
                .build();
    }

    public boolean equals(Day other){

        if(!other.date.equals(this.date) || !other.groupName.equals(this.groupName) || other.disciplines.size() != this.disciplines.size())
            return false;

        for (int i = 0; i < this.disciplines.size(); i++) {
            if(!this.disciplines.get(i).equals(other.disciplines.get(i)))
                return false;
        }

        return true;
    }
}
