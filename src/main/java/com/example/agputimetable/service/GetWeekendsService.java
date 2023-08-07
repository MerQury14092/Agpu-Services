package com.example.agputimetable.service;

import com.example.agputimetable.memory.WeekendsMemory;
import com.example.agputimetable.model.Weekend;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@Log4j2
public class GetWeekendsService {

    private final WeekendsMemory memory;

    @Autowired
    public GetWeekendsService(WeekendsMemory memory) {
        this.memory = memory;
        memory.addAll(parseHtml());
    }

    private List<Weekend> parseHtml(){
        Scanner sc = new Scanner(GetWeekendsService.class.getResourceAsStream("/static/weekends"));

        StringBuilder builder = new StringBuilder();

        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }

        Document document = Jsoup.parse(builder.toString());

        List<Weekend> weekends = new ArrayList<>();

        for(Element el: document.
                getElementsByClass("col-4 mb-3")
                //.first()
                //.getElementsByClass("modal fade show")
        )
            weekends.add(parseWeekend(el));

        return weekends;
    }

    private Weekend parseWeekend(Element el){ // сюда приходит col-4 mb-3
        Weekend res = new Weekend();
        Elements properties = el.getElementsByTag("span");
        res.setId(Integer.parseInt(properties.get(0).text()));
        res.setFrom(properties.get(1).text().replace("с ",""));
        res.setTo(properties.get(2).text().replace("по ",""));
        return res;
    }
}
