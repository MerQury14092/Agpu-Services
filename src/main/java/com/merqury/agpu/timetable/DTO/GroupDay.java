package com.merqury.agpu.timetable.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class GroupDay extends Day{
    String date;
    String groupName;
    List<Discipline> disciplines;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public GroupDay deleteHolidays(){
        disciplines = disciplines.stream()
                .filter(discipline -> !discipline.getName().equals("HOLIDAY"))
                .collect(Collectors.toList());
        return this;
    }

    public GroupDay proxy(){
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

        return GroupDay.builder()
                .date(date)
                .groupName(groupName)
                .disciplines(proxyList)
                .build();
    }
}
