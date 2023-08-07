package com.example.agputimetable.memory;

import com.example.agputimetable.model.Weekend;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeekendsMemory {
    private List<Weekend> memory;

    public WeekendsMemory(){
        memory = new ArrayList<>();
    }

    public void addWeekend(Weekend weekend){
        memory.add(weekend);
    }

    public Weekend getWeekendById(int id){
        List<Weekend> filtered = memory.stream().filter(w -> w.getId() == id).toList();
        if(filtered.isEmpty())
            return null;
        return filtered.get(0);
    }

    public List<Weekend> getEverything(){
        return memory;
    }

    public void addAll(List<Weekend> list){
        memory.addAll(list);
    }
}
