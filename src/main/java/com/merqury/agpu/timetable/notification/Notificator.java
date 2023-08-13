package com.merqury.agpu.timetable.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.memory.NotificationMemory;
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
    private final NotificationMemory memory;
    private final ObjectMapper mapper;

    @Autowired
    public Notificator(NotificationMemory memory) {
        this.memory = memory;
        mapper = new ObjectMapper();
    }


    public void notifyWebhooks(String groupName, Day modifiedDay){
        try {
            for(String url: memory.urls(groupName)){
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                PrintWriter pw = new PrintWriter(connection.getOutputStream(), true);
                pw.println(mapper.writeValueAsString(modifiedDay));
            }
        } catch (IOException ignored){}
    }
}
