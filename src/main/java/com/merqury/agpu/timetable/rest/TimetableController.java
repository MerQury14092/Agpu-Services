package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.general.Controllers;
import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Groups;
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
    public Day getTimetable(HttpServletRequest request,
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

        Day res = (service.getDisciplines(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"), date, false, true)).deleteHolidays();
        if (res.getId() == null)
            res.setId(groupName);
        else if (res.getId().equals("None"))
            res.setId("Unknown group");
        return res;
    }

    @GetMapping("/days")
    public List<Day> getTimetable(
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
                    Day.builder()
                            .date(startDateMessage)
                            .id(groupIdMessage)
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
        List<Day> result = service.getDisciplines(request.getParameter("id") == null ? request.getParameter("groupId") : request.getParameter("id"), startDate, endDate);
        result.forEach(Day::deleteHolidays);
        if (removeNull)
            result.removeIf(Day::isEmpty);
        if (result.isEmpty())
            return List.of();
        if (result.get(0).getId() == null)
            result.get(0).setId(groupName);
        else if (result.get(0).getId().equals("None"))
            return List.of();
        return result;

    }

    @GetMapping("/weeks")
    public List<Week> getWeeks() {
        return weeksService.getEverything();
    }

    @GetMapping("/groups")
    public List<Groups> groups() {
        return groupIdService.getAllGroups();
    }
}
