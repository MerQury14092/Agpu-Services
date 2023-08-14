package com.merqury.agpu.timetable.notification;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.memory.TimetableMemory;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.service.GetTimetableService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class Updater implements Runnable{
    private final GetTimetableService timetableService;
    private final GetGroupIdService groupIdService;
    private final Notificator notificator;
    private final TimetableMemory timetableMemory;

    @Autowired
    public Updater(
            GetTimetableService getTimetableService,
            GetGroupIdService getGroupIdService,
            Notificator notificator,
            TimetableMemory memory
    ){
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 0, 3, TimeUnit.MINUTES);
        timetableService = getTimetableService;
        groupIdService = getGroupIdService;
        this.notificator = notificator;
        timetableMemory = memory;
    }

    @SneakyThrows
    @Override
    public void run() {
        log.info("Start updating");
        long start = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String today = LocalDateTime.now().format(formatter);
        String tomorrow = LocalDateTime.now().plusDays(1).format(formatter);
        List<Groups> groups = groupIdService.getAllGroups();
        ExecutorService service = Executors.newFixedThreadPool(50);
        for(Groups faculty: groups){
            for (String group : faculty.getGroups()) {
                service.submit(() -> {
                    try{
                        Day real;
                        Day cache = timetableMemory.getDisciplineByDate(group, today);
                        real = timetableService.getDisciplinesWithoutCache(group, today);
                        if (!cache.equals(real)) {
                            notificator.notifyWebhooks(group, real);
                            timetableMemory.rm(cache);
                            timetableMemory.addDiscipline(real);
                            if(group.equals("ВМ-ИВТ-1-1"))
                                log.error("DONT EQ: \n\nREAL: {}\nCACHE: {}\n", real, cache);
                        }
                        cache = timetableMemory.getDisciplineByDate(group, tomorrow);
                        real = timetableService.getDisciplinesWithoutCache(group, tomorrow);
                        if (!cache.equals(real)) {
                            notificator.notifyWebhooks(group, real);
                            timetableMemory.rm(cache);
                            timetableMemory.addDiscipline(real);
                        }
                    } catch (IOException e){
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        log.info("Finish updating in {} sec", (System.currentTimeMillis()-start)/1000);
    }
}
