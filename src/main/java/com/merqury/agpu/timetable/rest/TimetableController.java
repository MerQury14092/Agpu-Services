package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.GroupDay;
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
    public GroupDay getTimetable(HttpServletRequest request,
                                 @PathParam("") String date
    ) throws IOException {
        System.out.println("Request for: "+(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id")));
        if(date == null || (request.getParameter("groupId") == null && request.getParameter("id") == null)){
            if(date == null)
                return GroupDay.builder()
                        .date("Enter date, please")
                        .groupName("...")
                        .disciplines(List.of())
                        .build();
            return GroupDay.builder()
                    .date("...")
                    .groupName("Enter group name, please")
                    .disciplines(List.of())
                    .build();
        }
        if(!Pattern.matches(dateRegex, date))
            date = "Invalid date format";
        String groupName = groupIdService.getFullGroupName(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"));
        if(groupName.equals("None"))
            return GroupDay.builder()
                    .groupName("Unknown group")
                    .date(date)
                    .disciplines(List.of())
                    .build();
        if(!groupName.equals(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id")))
            return GroupDay.builder()
                    .date(date)
                    .groupName("May be you mean \""+groupName+"\"?")
                    .disciplines(List.of())
                    .build();

        if(date.equals("Invalid date format"))
            return GroupDay.builder()
                    .groupName(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"))
                    .date(date)
                    .disciplines(List.of())
                    .build();

        GroupDay res = ((GroupDay) service.getDisciplines(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"), date, false)).deleteHolidays();
        if(res.getGroupName() == null)
            res.setGroupName(groupName);
        else if(res.getGroupName().equals("None"))
            res.setGroupName("Unknown group");
        return res;
    }

    @GetMapping("/days")
    public List<GroupDay> getTimetable(
                                       @PathParam("") String startDate,
                                       @PathParam("") String endDate,
                                       HttpServletRequest request
    ) throws IOException {
        System.out.println("Request for: "+(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id")));
        if(startDate == null || endDate == null || (request.getParameter("groupId") == null && request.getParameter("id") == null)){
            String startDateMessage = startDate;
            String groupIdMessage = request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id");
            if(startDate == null)
                startDateMessage = "Enter start date, please";
            if(endDate == null)
                startDateMessage = "Enter end date, please";
            if(request.getParameter("groupId") == null && request.getParameter("id") == null)
                groupIdMessage = "Enter group name, please";
            return List.of(
                    GroupDay.builder()
                            .date(startDateMessage)
                            .groupName(groupIdMessage)
                            .disciplines(List.of())
                            .build()
            );
        }
        if(!Pattern.matches(dateRegex, startDate))
            startDate = "Invalid date format";
        if(!Pattern.matches(dateRegex, endDate))
            endDate = "Invalid date format";
        String groupName = groupIdService.getFullGroupName(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"));
        if(groupName.equals("None"))
            return List.of(GroupDay.builder()
                    .groupName("Unknown group")
                    .date(startDate)
                    .disciplines(List.of())
                    .build());
        if(!groupName.equals(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id")))
            return List.of(GroupDay.builder()
                    .date(startDate)
                    .groupName("May be you mean \""+groupName+"\"?")
                    .disciplines(List.of())
                    .build());

        if(startDate.equals("Invalid date format") || endDate.equals("Invalid date format"))
            return List.of(GroupDay.builder()
                .groupName(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"))
                .date("Invalid date format")
                .disciplines(List.of())
                .build());


        boolean removeNull = (request.getParameter("removeEmptyDays") != null);
        List<GroupDay> result = service.getDisciplines(request.getParameter("id")==null?request.getParameter("groupId"):request.getParameter("id"), startDate, endDate);
        result.forEach(GroupDay::deleteHolidays);
        if (removeNull)
            result.removeIf(GroupDay::isEmpty);
        if(result.isEmpty())
            return List.of();
        if(result.get(0).getGroupName() == null)
            result.get(0).setGroupName(groupName);
        else if (result.get(0).getGroupName().equals("None"))
            return List.of();
        return result;

    }

    @GetMapping("/weeks")
    public List<Week> getWeeks(){
        return weeksService.getEverything();
    }

    @GetMapping("/teacher/day")
    public TeacherDay getTeacherTimetable(
            HttpServletRequest request,
            @PathParam("") String date
    ) throws IOException {
        System.out.println("Request for: "+(request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id")));
        if(date == null || (request.getParameter("teacherId") == null && request.getParameter("id") == null)){
            if(date == null)
                return TeacherDay.builder()
                        .date("Enter date, please")
                        .teacherName("...")
                        .disciplines(List.of())
                        .build();
            return TeacherDay.builder()
                    .date("...")
                    .teacherName("Enter teacher name, please")
                    .disciplines(List.of())
                    .build();
        }
        if(!Pattern.matches(dateRegex, date))
            date = "Invalid date format";
        if(date.equals("Invalid date format"))
            return TeacherDay.builder()
                    .teacherName(request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id"))
                    .date(date)
                    .disciplines(List.of())
                    .build();
        TeacherDay res = ((TeacherDay) service.getDisciplines(
                request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id"),
                date,
                true
        )).deleteHolidays();
        if(res.getTeacherName().equals("None"))
            res.setTeacherName("Unknown teacher");
        return res;
    }

    @GetMapping("/teacher/days")
    public List<TeacherDay> getTimetableTeacher(
                                 @PathParam("") String startDate,
                                 @PathParam("") String endDate,
                                 HttpServletRequest request
    ) throws IOException {
        // (request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id"));
        if(startDate == null || endDate == null || (request.getParameter("teacherId") == null && request.getParameter("id") == null)){
            String startDateMessage = startDate;
            String teacherIdMessage = request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id");
            if(startDate == null)
                startDateMessage = "Enter start date, please";
            if(endDate == null)
                startDateMessage = "Enter end date, please";
            if(request.getParameter("teacherId") == null && request.getParameter("id") == null)
                teacherIdMessage = "Enter teacher name, please";
            return List.of(
                    TeacherDay.builder()
                            .date(startDateMessage)
                            .teacherName(teacherIdMessage)
                            .disciplines(List.of())
                            .build()
            );
        }
        if(!Pattern.matches(dateRegex, startDate))
            startDate = "Invalid date format";
        if(!Pattern.matches(dateRegex, endDate))
            endDate = "Invalid date format";
        boolean removeNull = (request.getParameter("removeEmptyDays") != null);
        List<TeacherDay> result = service.getDisciplinesTeacher(request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id"), startDate, endDate);
        result.forEach(TeacherDay::deleteHolidays);
        if(removeNull)
            result.removeIf(TeacherDay::isEmpty);
        if(result.isEmpty())
            return List.of();
        if(result.get(0).getTeacherName().equals("None"))
            return List.of(
                    TeacherDay.builder()
                            .date(startDate)
                            .teacherName("Unknown teacher")
                            .disciplines(List.of())
                            .build()
            );
        return result;
    }

    @GetMapping("/groups")
    public List<Groups> groups(){
        return groupIdService.getAllGroups();
    }
}
