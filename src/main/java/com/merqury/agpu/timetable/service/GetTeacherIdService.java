package com.merqury.agpu.timetable.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.SearchProduct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class GetTeacherIdService {
    private final String url;
    private final ObjectMapper objectMapper;

    public GetTeacherIdService(){
        this.objectMapper = new ObjectMapper();
        url = "http://www.it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=%s";
    }

    public int getId(String teacherName){
        SearchProduct[] result;
        try {
            result = objectMapper.readValue(
                    new URL(url.formatted(URLEncoder.encode(teacherName, StandardCharsets.UTF_8))),
                    SearchProduct[].class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(result.length == 0)
            return 1386;
        else
            return result[0].SearchId;
    }

    public String getFIO(String teacherName){
        SearchProduct[] result;
        try {
            result = objectMapper.readValue(
                    new URL(url.formatted(URLEncoder.encode(teacherName, StandardCharsets.UTF_8))),
                    SearchProduct[].class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(SearchProduct prod: result)
            if(prod.Type.equals("Teacher"))
                return prod.SearchContent;
        return "None";
    }
}
