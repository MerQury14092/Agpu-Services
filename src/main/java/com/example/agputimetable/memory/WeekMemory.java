package com.example.agputimetable.memory;

import com.example.agputimetable.model.Week;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeekMemory {
    private List<Week> memory;

    public WeekMemory(){
        memory = new ArrayList<>();
    }

    public List<Week> getEverything(){
        return memory;
    }

    public void addAll(List<Week> list){
        memory.addAll(list);
    }
}
