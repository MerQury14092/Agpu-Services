package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.general.Controllers;
import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.DTO.TeacherDay;
import com.merqury.agpu.timetable.DTO.Week;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.service.GetTimetableService;
import com.merqury.agpu.timetable.service.GetWeeksService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                                 @PathParam("") String date,
                                 HttpServletResponse response
    ) throws IOException {
        System.out.println("Request for: " + (request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id")));
        if (date == null || (request.getParameter("groupId") == null && request.getParameter("id") == null)) {
            if (date == null) {
                Controllers.sendError(400, "Expected date", response);
                return null;
            }
            Controllers.sendError(400, "Expected groupId|id", response);
            return null;
        }
        if (!Pattern.matches(dateRegex, date))
            date = "Invalid date format";
        String groupName = groupIdService.getFullGroupName(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"));
        if (groupName.equals("None")) {
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }
        if (!groupName.trim().equals(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"))) {
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }

        if (date.equals("Invalid date format")) {
            Controllers.sendError(400, date, response);
            return null;
        }

        GroupDay res = ((GroupDay) service.getDisciplines(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"), date, false, true)).deleteHolidays();
        if (res.getGroupName() == null)
            res.setGroupName(groupName);
        else if (res.getGroupName().equals("None"))
            res.setGroupName("Unknown group");
        return res;
    }

    @GetMapping("/days")
    public List<GroupDay> getTimetable(
            @PathParam("") String startDate,
            @PathParam("") String endDate,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        System.out.println("Request for: " + (request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id")));
        if (startDate == null || endDate == null || (request.getParameter("groupId") == null && request.getParameter("id") == null)) {
            String startDateMessage = startDate;
            String groupIdMessage = request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id");
            if (startDate == null) {
                Controllers.sendError(400, "Expected start date", response);
                return null;
            }
            if (endDate == null) {
                Controllers.sendError(400, "Expected end date", response);
                return null;
            }
            if (request.getParameter("groupId") == null && request.getParameter("id") == null) {
                Controllers.sendError(400, "Expected groupId|id", response);
                return null;
            }
            return List.of(
                    GroupDay.builder()
                            .date(startDateMessage)
                            .groupName(groupIdMessage)
                            .disciplines(List.of())
                            .build()
            );
        }
        if (!Pattern.matches(dateRegex, startDate)) {
            Controllers.sendError(400, "Invalid date format (start date)", response);
            return null;
        }
        if (!Pattern.matches(dateRegex, endDate)) {
            Controllers.sendError(400, "Invalid date format (end date)", response);
            return null;
        }
        String groupName = groupIdService.getFullGroupName(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"));
        if (groupName.equals("None")) {
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }
        if (!groupName.trim().equals(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"))) {
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }


        boolean removeNull = (request.getParameter("removeEmptyDays") != null);
        List<GroupDay> result = service.getDisciplines(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"), startDate, endDate);
        result.forEach(GroupDay::deleteHolidays);
        if (removeNull)
            result.removeIf(GroupDay::isEmpty);
        if (result.isEmpty())
            return List.of();
        if (result.get(0).getGroupName() == null)
            result.get(0).setGroupName(groupName);
        else if (result.get(0).getGroupName().equals("None"))
            return List.of();
        return result;

    }

    @GetMapping("/weeks")
    public List<Week> getWeeks() {
        return weeksService.getEverything();
    }

    @GetMapping("/teacher/day")
    public TeacherDay getTeacherTimetable(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathParam("") String date
    ) throws IOException {
        System.out.println("Request for: " + (request.getParameter("id") == null ? request.getParameter("teacherId") : request.getParameter("id")));
        if (date == null || (request.getParameter("teacherId") == null && request.getParameter("id") == null)) {
            if (date == null) {
                Controllers.sendError(400, "Expected date", response);
                return null;
            }
            Controllers.sendError(400, "Expected teacherId|id", response);
            return null;
        }
        if (!Pattern.matches(dateRegex, date)) {
            Controllers.sendError(400, "Invalid date format", response);
            return null;
        }
        TeacherDay res = ((TeacherDay) service.getDisciplines(
                request.getParameter("id") == null ? request.getParameter("teacherId") : request.getParameter("id"),
                date,
                true,
                true
        )).deleteHolidays();
        if (res.getTeacherName() == null) {
            Controllers.sendError(400, "Unknown teacher", response);
            return null;
        }
        return res;
    }

    @GetMapping("/teacher/days")
    public List<TeacherDay> getTimetableTeacher(
            @PathParam("") String startDate,
            @PathParam("") String endDate,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // (request.getParameter("id")==null?request.getParameter("teacherId"):request.getParameter("id"));
        if (startDate == null || endDate == null || (request.getParameter("teacherId") == null && request.getParameter("id") == null)) {
            if (startDate == null) {
                Controllers.sendError(400, "Expected start date", response);
                return null;
            }
            if (endDate == null) {
                Controllers.sendError(400, "Expected end date", response);
                return null;
            }
            if (request.getParameter("teacherId") == null && request.getParameter("id") == null) {
                Controllers.sendError(400, "Expected teacherId|id", response);
                return null;
            }
        }
        if (!Pattern.matches(dateRegex, startDate)) {
            Controllers.sendError(400, "Invalid start date format", response);
            return null;
        }
        if (!Pattern.matches(dateRegex, endDate)) {
            Controllers.sendError(400, "Invalid end date format", response);
            return null;
        }
        boolean removeNull = (request.getParameter("removeEmptyDays") != null);
        List<TeacherDay> result = service.getDisciplinesTeacher(request.getParameter("id") == null ? request.getParameter("teacherId") : request.getParameter("id"), startDate, endDate);
        result.forEach(TeacherDay::deleteHolidays);
        if (removeNull)
            result.removeIf(TeacherDay::isEmpty);
        if (result.isEmpty())
            return List.of();
        if (result.get(0).getTeacherName() == null) {
            Controllers.sendError(400, "Unknown teacher", response);
            return null;
        }
        return result;
    }

    @GetMapping("/groups")
    public List<Groups> groups() {
        return groupIdService.getAllGroups();
    }
}
