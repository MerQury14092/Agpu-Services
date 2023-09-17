package com.merqury.agpu.timetable.service;


import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Discipline;
import lombok.extern.log4j.Log4j2;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.swing.Java2DRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Log4j2
public class ImageService {
    private final Font font;

    public ImageService() throws IOException, FontFormatException {
        this.font = Font.createFont(Font.TRUETYPE_FONT, ImageService.class.getResourceAsStream("/fonts/timetable_font.ttf"));
        this.font.deriveFont(Font.PLAIN);
    }

    public BufferedImage getImageByTimetableOf6Days(Day[] days, boolean isVertical){
        if(!isVertical)
            return getImageByTimetableOf6DaysHorizontal(days);
        return null;
    }

    public BufferedImage getImageByTimetableOfDay(Day day, boolean isVertical){
        BufferedImage res = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        return res;
    }

    public BufferedImage getImageByTimetableOfDiscipline(Discipline disc){
        BufferedImage res = new BufferedImage(400, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return res;
    }

    public BufferedImage getImageByTimetableOfSubDiscipline(Discipline disc1, Discipline disc2){
        BufferedImage res = new BufferedImage(800, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        return res;
    }


    // html methods

    private BufferedImage getImageByTimetableOf6DaysHorizontal(Day[] days){
        BufferedImage res = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        Element el = new Element("table");
        el.attr("class", "table table-sm table-bordered text-center");
        Element thead = new Element("thead");
        Element thead_tr = new Element("tr");
        thead_tr.appendChild(new Element("th").attr("scope", "col").attr("style", "background-color: #e9ecef"));
        for(Element e: getTimeRow(days))
            thead_tr.appendChild(e);
        thead.appendChild(thead_tr);
        el.appendChild(thead);

        Element tbody = new Element("tbody");
        for(Day day: days){
            tbody.appendChild(getRow(day));
        }
        el.appendChild(tbody);


        Java2DRenderer renderer = new Java2DRenderer(new W3CDom().fromJsoup(new Element("div").appendChild(el)), 2970, 520);


        return renderer.getImage();
    }

    private Element getRow(Day day){
        Element tr = new Element("tr");
        List<Element> tds = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(day.getDate(), formatter);

        String localizeDateOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("RU"));
        localizeDateOfWeek = Character.toUpperCase(localizeDateOfWeek.charAt(0)) + localizeDateOfWeek.substring(1);

        tds.add(new Element("th").attr("scope", "row").html(localizeDateOfWeek+"<br>"+day.getDate()));

        int colspans = 12;

        for(Discipline disc: day.getDisciplines()){

            Element td = new Element("td");
            td.attr("colspan", (disc.getSubgroup()>0||disc.getName().toLowerCase().contains("по выбору"))?"1":"2");
            colspans -= Integer.parseInt(td.attr("colspan"));
            Element div = new Element("div");
            div.attr("style", "overflow: hidden; margin-top: 5px;");

            div.appendChild(
                    new Element("span").text(disc.getName())
            );
            div.appendChild(new Element("br"));
            div.appendChild(
                    new Element("span").text(disc.getTeacherName()+", "+disc.getAudienceId())
            );
            div.appendChild(new Element("br"));
            div.appendChild(
                    new Element("span").text(switch (disc.getType()){
                        case exam -> "Экзамен";
                        case lec -> "Лекция";
                        case hol -> "Праздник";
                        case lab -> "Лабараторная работа";
                        case prac -> "Практика";
                        case cons -> "Конь-султан-ция";
                        case cred -> "Зачёт";
                        case fepo -> "ФЭПО";
                        case none -> "";
                    })
            );
            td.attr("style", "background-color: #" + switch (disc.getType()){
                case lab -> "d3e2d0";
                case lec -> "f7e3e7";
                case prac -> "e8e582";
                case exam -> "f6574c ";
                default -> "ffffff";
            });

            td.appendChild(div);
            tds.add(td);
        }

        while (colspans != 0) {
            tds.add(new Element("td").attr("colspan", "2"));
            colspans -= 2;
        }

        for(Element e: tds)
            tr.appendChild(e);
        return tr;
    }

    private Element[] getTimeRow(Day[] days){
        int max_discs = days[0].getDisciplines().size();
        for(Day day: days)
            if(day.getDisciplines().size() > max_discs)
                max_discs = day.getDisciplines().size();

        Element[] res = new Element[]{
                createTimeCell("I пара<br>8:00-9:30"),
                createTimeCell("II пара<br>9:40-11:10"),
                createTimeCell("III пара<br>11:40-13:10"),
                createTimeCell("IV пара<br>13:30-15:00"),
                createTimeCell("V пара<br>15:10-16:40"),
                createTimeCell("VI пара<br>16:50-18:20")
        };

        for(int i = 0; i < max_discs; i++)
            res[i].attr("colspan", "2");

        return res;

    }

    private Element createTimeCell(String time){
        Element res = new Element("th");
        res.attr("style", "background-color: #e9ecef");
        res.attr("scope", "col");
        res.attr("colspan", "2");
        Element span = new Element("span");
        span.attr("class", "py-auto");
        span.html(time);
        res.appendChild(span);
        return res;
    }
}
