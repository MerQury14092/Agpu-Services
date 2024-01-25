package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.Statistics;
import com.merqury.agpu.timetable.memory.StatMemory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class StatisticsController {
    private final StatMemory memory;

    @GetMapping("/api/stats")
    public List<Statistics> getStats(){
        return memory.getStatistics();
    }
}
