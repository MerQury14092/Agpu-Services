package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.DTO.TeacherDay;
import com.merqury.agpu.timetable.DTO.Week;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.service.GetTimetableService;
import com.merqury.agpu.timetable.service.GetWeeksService;
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
@RequestMapping("/api/timetable")
public class TimetableController {
    private final GetTimetableService service;
    private final GetGroupIdService groupIdService;
    private final GetWeeksService weeksService;

    @GetMapping("/day")
    public Day getTimetable(@PathParam("") String groupId,
                            @PathParam("") String date
    ) throws IOException {
        if(date != null)
            return service.getDisciplines(groupId, date).deleteHolidays();
        return null;
    }

    @GetMapping("/days")
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

    @GetMapping("/weeks")
    public List<Week> getWeeks(){
        return weeksService.getEverything();
    }

    @GetMapping("/teacher/day")
    public TeacherDay getTeacherTimetable(
            @PathParam("") String teacherId,
            @PathParam("") String date
    ) throws IOException {
        return service.getDisciplinesByTeacher(
                teacherId,
                date
        ).deleteHolidays();
    }

    @GetMapping("/teacher/days")
    public List<TeacherDay> getTimetableTeacher(@PathParam("") String teacherId,
                                 @PathParam("") String startDate,
                                 @PathParam("") String endDate,
                                 HttpServletRequest request
    ) throws IOException {
        if(startDate != null && endDate != null) {
            boolean removeNull = (request.getParameter("removeEmptyDays") != null);
            List<TeacherDay> result = service.getDisciplinesTeacher(teacherId, startDate, endDate);
            result.forEach(TeacherDay::deleteHolidays);
            if(removeNull)
                result.removeIf(TeacherDay::isEmpty);
            return result;
        }
        return null;
    }

    @GetMapping("/groups")
    public List<Groups> groups(){
        return groupIdService.getAllGroups();
    }
}
