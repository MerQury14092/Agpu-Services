package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.AgpuTimetableApplication;
import com.merqury.agpu.timetable.DTO.Statistics;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class StatMemory {
    private final List<Statistics> statistics;
    public static Statistics currentStatistics;
    public StatMemory(){
        statistics = new ArrayList<>();
        currentStatistics = new Statistics();
        AgpuTimetableApplication.async(() -> {
            for(;;){
                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(60));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                endStatisticsAndBeginNext();
            }
        });
    }

    private void endStatisticsAndBeginNext(){
        log.info("Statistics updating...");
        currentStatistics.endRecording();
        statistics.add(currentStatistics);
        currentStatistics = new Statistics();
    }

    public List<Statistics> getStatistics(){
        List<Statistics> res = new ArrayList<>(statistics);
        res.add(currentStatistics);
        return res;
    }
}
