package com.merqury.agpu.timetable.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Day {
    List<Discipline> disciplines;
    String date;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public abstract Day proxy();


}
