package com.merqury.agpu.timetable.service;

import com.merqury.agpu.timetable.DTO.Groups;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Service
public class GetGroupIdService {
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
}
