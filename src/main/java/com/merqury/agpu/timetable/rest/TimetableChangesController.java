package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.general.Controllers;
import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/timetable/changes")
public class TimetableChangesController {
    private final GetGroupIdService getGroupIdService;

    public TimetableChangesController(GetGroupIdService getGroupIdService) {
        this.getGroupIdService = getGroupIdService;
    }


    @GetMapping("/day")
    public GroupDay getChanges(
            @PathParam("") String groupId,
            HttpServletResponse response,
            @PathParam("") Optional<Integer> timeout
            ) throws IOException, InterruptedException {
        if(timeout.isPresent())
            System.out.println(timeout.get());
        else
            System.out.println("None timeout");
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
        if(timeout.isPresent())
            Thread.sleep(1000L *timeout.get());
        return null;
    }

}
