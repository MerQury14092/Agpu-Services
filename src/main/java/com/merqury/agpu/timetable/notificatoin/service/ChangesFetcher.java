package com.merqury.agpu.timetable.notificatoin.service;

import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.memory.TimetableMemory;
import com.merqury.agpu.timetable.notificatoin.service.TimetableChangesPublisher;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.service.GetTimetableService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

@Component
@Log4j2
public class ChangesFetcher {
    private final GetTimetableService getTimetableService;
    private final GetGroupIdService getGroupIdService;
    private final TimetableMemory timetableMemory;
    private final TimetableChangesPublisher timetableChangesPublisher;

    public ChangesFetcher(
            GetTimetableService getTimetableService,
            GetGroupIdService getGroupIdService,
            TimetableMemory timetableMemory
    ){
        this.getTimetableService = getTimetableService;
        this.getGroupIdService = getGroupIdService;
        this.timetableMemory = timetableMemory;
        this.timetableChangesPublisher = TimetableChangesPublisher.singleton();
        async(() ->  {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startDaemonForRegularTimetableFetching();
        });
    }

    private void startDaemonForRegularTimetableFetching(){
        async(() -> {
            while (true){
                tryFetchTimetable();
                waitHours();
            }
        });
    }

    private void tryFetchTimetable(){
        try {
            fetchTimetable();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void fetchTimetable() throws IOException {
        log.info("Start fetching timetable");
        long timestampOfStartFetching = System.currentTimeMillis();
        for(Groups faculty: getGroupIdService.getAllGroups())
            fetchTimetableForFaculty(faculty);
        log.info("Fetching of timetable changes complete on {} ms", System.currentTimeMillis() - timestampOfStartFetching);
    }

    private void fetchTimetableForFaculty(Groups faculty) throws IOException {
        for (String group: faculty.getGroups())
            fetchTimetableForGroup(group);
    }

    private void fetchTimetableForGroup(String groupName) throws IOException {
        GroupDay todayFromMemory = timetableMemory.getDisciplineByDate(groupName, getToday());
        GroupDay tomorrowFromMemory = timetableMemory.getDisciplineByDate(groupName, getTomorrow());
        GroupDay todayFromSite = (GroupDay) getTimetableService.getDisciplines(groupName, getToday(), false, false);
        GroupDay tomorrowFromSite = (GroupDay) getTimetableService.getDisciplines(groupName, getTomorrow(), false, false);
        checkDayChanges(todayFromSite, todayFromMemory);
        async(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(15));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            checkDayChanges(tomorrowFromSite, tomorrowFromMemory);
        });
    }

    private String getToday(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().format(formatter);
    }

    private String getTomorrow(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().plusDays(1).format(formatter);
    }

    private void checkDayChanges(GroupDay day, GroupDay dayFromMemory){
        if(!dayFromMemory.equals(day)){
            timetableChangesPublisher.publishNotification(day.getGroupName(), day);
        }
    }

    private void waitHours(){
        try {
            Thread.sleep(TimeUnit.HOURS.toMillis(2));
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
