package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.AgpuTimetableApplication;
import com.merqury.agpu.general.Controllers;
import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.DTO.Notification;
import com.merqury.agpu.timetable.implementations.TemporarySubscriber;
import com.merqury.agpu.timetable.interfaces.Subscriber;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.service.TimetableChangesPublisher;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

@RestController
@RequestMapping("/api/timetable/changes")
public class TimetableChangesController {
    private final GetGroupIdService getGroupIdService;
    private final TimetableChangesPublisher changesPublisher;

    public TimetableChangesController(GetGroupIdService getGroupIdService, TimetableChangesPublisher changesPublisher) {
        this.getGroupIdService = getGroupIdService;
        this.changesPublisher = changesPublisher;
    }


    @GetMapping("/day")
    public GroupDay getChanges(
            @PathParam("") String groupId,
            HttpServletResponse response,
            @PathParam("") Optional<Integer> timeout
    ) throws IOException
    {
        if(timeout.isEmpty()){
            Controllers.sendError(400, "Expected timeout", response);
            return null;
        }
        if(groupId == null){
            Controllers.sendError(400, "Expected groupId", response);
            return null;
        }
        boolean contains = false;
        Super:
        for(Groups faculty: getGroupIdService.getAllGroups())
            for (String group: faculty.getGroups())
                if(group.equals(groupId)){
                    contains = true;
                    break Super;
                }
        if(!contains){
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }

        TemporarySubscriber subscriber = new TemporarySubscriber(timeout.get(), groupId);
        changesPublisher.addSubscriber(subscriber);
        GroupDay res = subscriber.get();
        if(res == null){
            changesPublisher.removeSubscriber(subscriber);
            return null;
        }
        return res.deleteHolidays();
    }

    @GetMapping("/day/check")
    public Notification checkChanges(
            @PathParam("") String groupId,
            HttpServletResponse response,
            @PathParam("") Optional<Integer> timeout
    ) throws IOException
    {
        GroupDay day = getChanges(groupId, response, timeout);
        if(day == null)
            return Notification.noChanges("N/A");
        return Notification.thereAreChanges(day.getDate());
    }

}
