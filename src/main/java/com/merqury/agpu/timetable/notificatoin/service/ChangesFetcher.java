package com.merqury.agpu.timetable.notificatoin.service;

import com.merqury.agpu.timetable.DTO.TimetableDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.memory.TimetableMemory;
import com.merqury.agpu.timetable.service.GetSearchIdService;
import com.merqury.agpu.timetable.service.GetTimetableService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final GetSearchIdService getSearchIdService;
    private final TimetableMemory timetableMemory;
    private final TimetableChangesPublisher timetableChangesPublisher;

    @Autowired
    public ChangesFetcher(
            GetTimetableService getTimetableService,
            GetSearchIdService getSearchIdService,
            TimetableMemory timetableMemory
    ){
        this.getTimetableService = getTimetableService;
        this.getSearchIdService = getSearchIdService;
        this.timetableMemory = timetableMemory;
        this.timetableChangesPublisher = TimetableChangesPublisher.singleton();
        startDaemonForRegularTimetableFetching();
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
        for(Groups faculty: getSearchIdService.getAllGroups())
            fetchTimetableForFaculty(faculty);
        log.info("Fetching of timetable changes complete on {} ms", System.currentTimeMillis() - timestampOfStartFetching);
    }

    private void fetchTimetableForFaculty(Groups faculty) throws IOException {
        for (String group: faculty.getGroups())
            fetchTimetableForGroup(group);
    }

    private void fetchTimetableForGroup(String groupName) throws IOException {
        TimetableDay todayFromMemory = timetableMemory.getDisciplineByDate(groupName, getToday());
        TimetableDay tomorrowFromMemory = timetableMemory.getDisciplineByDate(groupName, getTomorrow());
        TimetableDay todayFromSite = getTimetableService.getDisciplines(groupName, getToday(), false, false);
        TimetableDay tomorrowFromSite = getTimetableService.getDisciplines(groupName, getTomorrow(), false, false);
        checkDayChanges(todayFromSite, todayFromMemory);
        checkDayChangesAfter(TimeUnit.SECONDS.toMillis(15), tomorrowFromSite, tomorrowFromMemory);
    }

    private void checkDayChangesAfter(long milliseconds, TimetableDay timetableDayFromSite, TimetableDay timetableDayFromMemory){
        async(() -> {
            trySleep(milliseconds);
            checkDayChanges(timetableDayFromSite, timetableDayFromMemory);
        });
    }

    private void trySleep(long milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getToday(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().format(formatter);
    }

    private String getTomorrow(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.now().plusDays(1).format(formatter);
    }

    private void checkDayChanges(TimetableDay timetableDay, TimetableDay timetableDayFromMemory){
        if(!timetableDayFromMemory.equals(timetableDay)){
            timetableChangesPublisher.publishNotification(timetableDay.getId(), timetableDay);
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
