package com.merqury.agpu.timetable.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.DTO.SearchProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Service
public class GetGroupIdService {
    private final String url;
    private final ObjectMapper objectMapper;
    public GetGroupIdService(){
        this.objectMapper = new ObjectMapper();
        url = "http://www.it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=%s";
    }
    public int getId(String groupName){

        Scanner sc = new Scanner(Objects.requireNonNull(this.getClass().getResourceAsStream("/static/groupids")));

        StringBuilder builder = new StringBuilder();

        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }

        String line = searchLineWith(builder.toString(), groupName);

        if(line.equals("N"))
            return 0;

        return Integer.parseInt(line.split("&")[1].split("=")[1]);
    }

    public List<Groups> getAllGroups(){
        Scanner sc = new Scanner(Objects.requireNonNull(this.getClass().getResourceAsStream("/static/groupids")));

        StringBuilder builder = new StringBuilder();

        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }

        List<Groups> res = new ArrayList<>();

        for(Element el : Jsoup.parse(builder.toString()).getElementsByClass("card")) {
            res.add(parseCardElement(el));
        }
        return res;
    }

    private Groups parseCardElement(Element el){
        Groups res = new Groups();
        res.setFacultyName(
                el.getElementsByTag("button").first().text()
        );

        for(Element p2: el.getElementsByClass("p-2")){
            res.getGroups().add(p2.getAllElements().first().text());
        }
        return res;
    }

    private String searchLineWith(String text, String substring){
        String[] arr = text.split("\n");
        for(String cur : arr) {
            if (cur.contains(substring)) {
                return cur;
            }
        }
        return "N";
    }

    public String getFullGroupName(String groupName){
        SearchProduct[] result;
        try {
            result = objectMapper.readValue(
                    new URL(url.formatted(URLEncoder.encode(groupName, StandardCharsets.UTF_8))),
                    SearchProduct[].class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(SearchProduct prod: result)
            if(prod.Type.equals("Group"))
                return prod.SearchContent;
        return "None";
    }
}
