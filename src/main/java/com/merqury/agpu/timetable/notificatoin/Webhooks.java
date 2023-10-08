package com.merqury.agpu.timetable.notificatoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.GroupDay;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
public class Webhooks {
    private static ObjectMapper mapper;
    static  {
        mapper = new ObjectMapper();
    }
    public static void sendData(String host, GroupDay day){
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL("http://"+host).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(mapper.writeValueAsBytes(day.deleteHolidays()));
            connection.getOutputStream().flush();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            System.out.println(connection.getResponseCode());
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
