package com.example.agputimetable.rest;

import com.example.agputimetable.model.Day;
import com.example.agputimetable.model.Week;
import com.example.agputimetable.service.GetGroupIdService;
import com.example.agputimetable.service.GetTimetableService;
import com.example.agputimetable.service.GetWeeksService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GeneralController {
    private final GetTimetableService service;
    private final GetGroupIdService groupIdService;
    private final GetWeeksService weeksService;

    @GetMapping("/timetableOfDay")
    public Day getTimetable(@PathParam("") String groupId,
                            @PathParam("") String date
    ) throws IOException {
        if(date != null)
            return service.getDisciplines(groupId, date).deleteHolidays();
        return null;
    }

    @GetMapping("/timetableOfDays")
    public List<Day> getTimetable(@PathParam("") String groupId,
                                  @PathParam("") String startDate,
                                  @PathParam("") String endDate,
                                  HttpServletRequest request
    ) throws IOException {
        if(startDate != null && endDate != null) {
            boolean removeNull = (request.getParameter("removeEmptyDays") != null);
            List<Day> result = service.getDisciplines(groupId, startDate, endDate);
            result.forEach(Day::deleteHolidays);
            if(removeNull)
                result.removeIf(Day::isEmpty);
            return result;
        }
        return null;
    }

    @GetMapping("/getWeeks")
    public List<Week> getWeeks(){
        return weeksService.getEverything();
    }

    @GetMapping("/allGroups")
    public List<String> groups(){
        return groupIdService.getAllGroups();
    }
}
