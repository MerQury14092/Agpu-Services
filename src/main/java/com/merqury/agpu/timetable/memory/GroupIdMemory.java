package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

@Component
@Log4j2
public class GroupIdMemory {
    private final GetGroupIdService groupSearchIdService;

    private final HashMap<String, Integer> groupSearchIdMap;

    public static boolean isUpdatedAllGroups;

    public int getSearchId(String groupName){
        if(isUpdatedAllGroups)
            return groupSearchIdMap.get(groupName);
        return 0;
    }

    public GroupIdMemory(GetGroupIdService groupSearchIdService) {
        this.groupSearchIdService = groupSearchIdService;
        this.groupSearchIdMap = new HashMap<>();
        fetchData();
        startDaemonForUpdatingGroups();

    }

    private void startDaemonForUpdatingGroups(){
        async(() -> {
            while (true){
                waitWeek();
                fetchData();
            }
        });
    }


    private void fetchData(){
        groupSearchIdMap.clear();
        isUpdatedAllGroups = false;
        log.info("start updating group names");
        fetchDataFromService();
        isUpdatedAllGroups = true;
        log.info("group names is updated");
    }

    private void fetchDataFromService(){
        for(Groups faculty: groupSearchIdService.getAllGroups())
            fetchGroupsFromFaculty(faculty);
    }

    private void fetchGroupsFromFaculty(Groups faculty){
        for(String groupName: faculty.getGroups())
            putInMemory(groupName);
    }

    private void putInMemory(String groupName){
        int searchId = groupSearchIdService.getId(groupName);
        groupSearchIdMap.put(groupName, searchId);
    }

    private void waitWeek(){
        try {
            Thread.sleep(TimeUnit.DAYS.toMillis(7));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
