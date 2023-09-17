package com.merqury.agpu.timetable.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public abstract class Day {
    List<Discipline> disciplines;

    @JsonIgnore
    public boolean isEmpty(){
        return disciplines.isEmpty();
    }

    public abstract Day proxy();


}
