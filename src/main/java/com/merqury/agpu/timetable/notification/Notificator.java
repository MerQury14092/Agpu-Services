package com.merqury.agpu.timetable.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.memory.WebhookMemory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Log4j2
public class Notificator {
    private final WebhookMemory memory;
    private final ObjectMapper mapper;

    @Autowired
    public Notificator(WebhookMemory memory) {
        this.memory = memory;
        mapper = new ObjectMapper();
    }


    public void notifyWebhooks(String groupName, Day modifiedDay){
        for (String url : memory.urls(groupName)) {
            try{
                log.info("NOTIFICATION\nhost: {}\ngroup: {}, day: {}", url, groupName, modifiedDay);
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                PrintWriter pw = new PrintWriter(connection.getOutputStream(), true);
                pw.println(mapper.writeValueAsString(modifiedDay.deleteHolidays()));
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.getResponseCode();
            } catch (IOException e){
                log.error("error on {} webhook:\n{}", url, e.getMessage());
                if(e.getMessage().equals("Connection refused: connect")) {
                    log.info("webhook well be removed: {}", url);
                    memory.rm(url);
                }
            }
        }
    }
}
