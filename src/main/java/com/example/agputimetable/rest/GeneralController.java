package com.example.agputimetable.rest;

import com.example.agputimetable.model.Discipline;
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

    @GetMapping("/api/timetableOfDay")
    public List<Discipline> getTimetable(@PathParam("") String groupId,
                                         @PathParam("") String date
    ) throws IOException {
        if(date != null)
            return service.getDisciplines(groupId, date);
        return null;
    }

    @GetMapping("/api/timetableOfDays")
    public List<List<Discipline>> getTimetable(@PathParam("") String groupId,
                                               @PathParam("") String startDate,
                                               @PathParam("") String endDate
    ) throws IOException {
        if(startDate != null && endDate != null)
            return service.getDisciplines(groupId, startDate, endDate);
        return null;
    }


}
