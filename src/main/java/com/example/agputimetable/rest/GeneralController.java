package com.example.agputimetable.rest;

import com.example.agputimetable.model.Day;
import com.example.agputimetable.model.Discipline;
import com.example.agputimetable.service.GetGroupIdService;
import com.example.agputimetable.service.GetTimetableService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class GeneralController {
    private final GetTimetableService service;
    private final GetGroupIdService groupIdService;

    @GetMapping("/api/timetableOfDay")
    public Day getTimetable(@PathParam("") String groupId,
                            @PathParam("") String date
    ) throws IOException {
        if(date != null)
            return service.getDisciplines(groupId, date).deleteHolidays();
        return null;
    }

    @GetMapping("/api/timetableOfDays")
    public List<Day> getTimetable(@PathParam("") String groupId,
                                  @PathParam("") String startDate,
                                  @PathParam("") String endDate
    ) throws IOException {
        if(startDate != null && endDate != null) {
            List<Day> result = service.getDisciplines(groupId, startDate, endDate);
            result.forEach(Day::deleteHolidays);
            return result;
        }
        return null;
    }

    @GetMapping("/api/allGroups")
    public List<String> groups(){
        return groupIdService.getAllGroups();
    }


}
