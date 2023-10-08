package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.general.Controllers;
import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.notificatoin.DTO.Notification;
import com.merqury.agpu.timetable.notificatoin.DTO.Webhook;
import com.merqury.agpu.timetable.notificatoin.Webhooks;
import com.merqury.agpu.timetable.notificatoin.interfaces.TemporarySubscriber;
import com.merqury.agpu.timetable.notificatoin.service.WebhookRegistryService;
import com.merqury.agpu.timetable.service.GetGroupIdService;
import com.merqury.agpu.timetable.notificatoin.service.TimetableChangesPublisher;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/timetable/changes")
public class TimetableChangesController {
    private final GetGroupIdService getGroupIdService;
    private final TimetableChangesPublisher changesPublisher;
    private final WebhookRegistryService webhookRegistryService;

    public TimetableChangesController(GetGroupIdService getGroupIdService) {
        this.getGroupIdService = getGroupIdService;
        webhookRegistryService = WebhookRegistryService.singleton();
        this.changesPublisher = TimetableChangesPublisher.singleton();
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

    @PostMapping(value = "webhook/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public String registerWebhook(@RequestBody Webhook webhook, HttpServletResponse response) throws IOException {
        boolean contains = false;
        Super:
        for(Groups faculty: getGroupIdService.getAllGroups())
            for (String group: faculty.getGroups())
                if(group.equals(webhook.getGroup())){
                    contains = true;
                    break Super;
                }
        if(!contains){
            Controllers.sendError(400, "Unknown group", response);
            return null;
        }
        if(!Webhooks.ping(webhook.getUrl())){
            Controllers.sendError(400, "Webhook did not return the expected response within 5 secs", response);
            return null;
        }
        int result = webhookRegistryService.addWebhook(webhook);
        if (result == 0)
            return """
                    {
                        "status": "OK"
                    }
                    """;

        Controllers.sendError(400, "webhook already registered", response);
        return null;
    }

}
