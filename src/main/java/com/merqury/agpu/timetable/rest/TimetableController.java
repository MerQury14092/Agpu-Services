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
import java.util.regex.Pattern;

@RestController
@AllArgsConstructor
@RequestMapping("/api/timetable")
public class TimetableController {
    private final GetTimetableService service;
    private final GetGroupIdService groupIdService;
    private final GetWeeksService weeksService;
    private final String dateRegex = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}$";

    @GetMapping("/day")
    public Day getTimetable(@PathParam("") String groupId,
                            @PathParam("") String date
    ) throws IOException {
        if(!Pattern.matches(dateRegex, date))
            date = "Invalid date format";
        String groupName = groupIdService.getFullGroupName(groupId);
        if(groupName.equals("None"))
            return Day.builder()
                    .groupName("Unknown group")
                    .date(date)
                    .disciplines(List.of())
                    .build();
        if(!groupName.equals(groupId))
            return Day.builder()
                    .date(date)
                    .groupName("May be you mean \""+groupName+"\"?")
                    .disciplines(List.of())
                    .build();

        if(date.equals("Invalid date format"))
            return Day.builder()
                    .groupName(groupId)
                    .date(date)
                    .disciplines(List.of())
                    .build();

        if(date != null) {
            Day res = service.getDisciplines(groupId, date).deleteHolidays();
            if(res.getGroupName().equals("None"))
                res.setGroupName("Unknown group");
            return res;
        }
        return null;
    }

    @GetMapping("/days")
    public List<Day> getTimetable(@PathParam("") String groupId,
                                  @PathParam("") String startDate,
                                  @PathParam("") String endDate,
                                  HttpServletRequest request
    ) throws IOException {
        if(!Pattern.matches(dateRegex, startDate))
            startDate = "Invalid date format";
        if(!Pattern.matches(dateRegex, endDate))
            endDate = "Invalid date format";
        String groupName = groupIdService.getFullGroupName(groupId);
        if(groupName.equals("None"))
            return List.of(Day.builder()
                    .groupName("Unknown group")
                    .date(startDate)
                    .disciplines(List.of())
                    .build());
        if(!groupName.equals(groupId))
            return List.of(Day.builder()
                    .date(startDate)
                    .groupName("May be you mean \""+groupName+"\"?")
                    .disciplines(List.of())
                    .build());

        if(startDate.equals("Invalid date format") || endDate.equals("Invalid date format"))
            return List.of(Day.builder()
                .groupName(groupId)
                .date("Invalid date format")
                .disciplines(List.of())
                .build());


        boolean removeNull = (request.getParameter("removeEmptyDays") != null);
        List<Day> result = service.getDisciplines(groupId, startDate, endDate);
        result.forEach(Day::deleteHolidays);
        if (removeNull)
            result.removeIf(Day::isEmpty);

        if (result.get(0).getGroupName().equals("None"))
            return List.of();
        return result;

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
        if(!Pattern.matches(dateRegex, date))
            date = "Invalid date format";
        if(date.equals("Invalid date format"))
            return TeacherDay.builder()
                    .teacherName(teacherId)
                    .date(date)
                    .disciplines(List.of())
                    .build();
        TeacherDay res = service.getDisciplinesByTeacher(
                teacherId,
                date
        ).deleteHolidays();
        if(res.getTeacherName().equals("None"))
            res.setTeacherName("Unknown teacher");
        return res;
    }

    @GetMapping("/teacher/days")
    public List<TeacherDay> getTimetableTeacher(@PathParam("") String teacherId,
                                 @PathParam("") String startDate,
                                 @PathParam("") String endDate,
                                 HttpServletRequest request
    ) throws IOException {
        if(!Pattern.matches(dateRegex, startDate))
            startDate = "Invalid date format";
        if(!Pattern.matches(dateRegex, endDate))
            endDate = "Invalid date format";
        if(startDate != null && endDate != null) {
            boolean removeNull = (request.getParameter("removeEmptyDays") != null);
            List<TeacherDay> result = service.getDisciplinesTeacher(teacherId, startDate, endDate);
            result.forEach(TeacherDay::deleteHolidays);
            if(removeNull)
                result.removeIf(TeacherDay::isEmpty);
            if(result.get(0).getTeacherName().equals("None"))
                return List.of();
            return result;
        }
        return null;
    }

    @GetMapping("/groups")
    public List<Groups> groups(){
        return groupIdService.getAllGroups();
    }
}
