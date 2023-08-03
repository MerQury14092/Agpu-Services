package com.example.agputimetable.service;

import com.example.agputimetable.enums.DisciplineType;
import com.example.agputimetable.memory.TimetableMemory;
import com.example.agputimetable.model.Discipline;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Log4j2
public class GetTimetableService {
    private final GetGroupIdService getGroupIdService;
    private final TimetableMemory memory;

    public GetTimetableService(GetGroupIdService getGroupIdService, TimetableMemory memory) {
        this.getGroupIdService = getGroupIdService;
        this.memory = memory;
    }

    private static final int ownerId = 118;
    private static final String url = "http://www.it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=%d&SearchId=%d&SearchString=None&Type=Group&WeekId=%d";

    public List<List<Discipline>> getDisciplines(String groupName, String startDate, String endDate) throws IOException {
        List<List<Discipline>> result = new ArrayList<>();
        for(String date: getDatesBetween(startDate, endDate)) {
            result.add(getDisciplines(groupName, date));
        }
        return result;
    }

    public List<Discipline> getDisciplines(String groupName, String date) throws IOException {


        int mappingWeekId = 3100;

        long weekId = mappingWeekId + countDays("29.08.2022", date) / 7;

        log.info("log weekID: {}", weekId);

        return parseHtml(String.format(
                        url,
                        ownerId,
                        getGroupIdService.getId(groupName),
                        weekId),
                date,
                groupName
        );
    }

    private void dataFilter(List<Discipline> result, List<Discipline> allDisciplines, String cur) {
        for (Discipline discipline : allDisciplines) {
            if (discipline.getDate().equals(cur)) {
                if (discipline.getName() != null)
                    result.add(discipline);
                else
                    result.add(null);
            }
        }
        for (; ; ) {
            if (result.size() == 0)
                break;
            if (result.get(result.size() - 1) == null)
                result.remove(result.size() - 1);
            else
                break;
        }
    }


    public static List<String> getDatesBetween(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<String> dates = new ArrayList<>();
        LocalDate dateCursor = start;

        while (!dateCursor.isAfter(end)) {
            dates.add(dateCursor.format(formatter));
            dateCursor = dateCursor.plusDays(1);
        }

        return dates;
    }

    private long countDays(String startDate, String endDate) {
        String[] part = startDate.split("\\.");
        Date dt = new Date(
                Integer.parseInt(part[2]),
                Integer.parseInt(part[1]) - 1,
                Integer.parseInt(part[0])
        );
        long currentTime = dt.getTime();
        long mappingWeek = currentTime / 1000 / 60 / 60 / 24;

        log.info("m w: {}", mappingWeek);
        log.info("m w ms: {}", dt.getTime());


        part = endDate.split("\\.");
        dt = new Date(
                Integer.parseInt(part[2]),
                Integer.parseInt(part[1]) - 1,
                Integer.parseInt(part[0])
        );
        currentTime = dt.getTime();
        long currentWeek = (long) (currentTime / 1000d / 60 / 60 / 24);


        log.info("c w: {}", currentWeek);
        log.info("c w ms: {}", dt.getTime());

        log.info("difference: {}", (currentWeek - mappingWeek));

        return (currentWeek - mappingWeek);
    }


    private List<Discipline> parseHtml(String url, String date, String groupName) throws IOException {
        List<Discipline> result = memory.getDisciplineByDate(groupName, date);
        if(!result.isEmpty()) {
            return result;
        }
        result = new ArrayList<>();

        URL url1 = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        InputStream is = conn.getInputStream();


        StringBuilder html = new StringBuilder();

        Scanner sc = new Scanner(is);

        String cur = "";
        while (!cur.equals("</html>")) {
            cur = sc.nextLine();
            html.append(cur);
        }

        //log.info("source: {}", html);

        Document doc = Jsoup.parse(html.toString());

        //log.info("doc is : {}", doc.html());

        Elements elements = doc
                .getElementsByClass("table")
                .first()
                .getElementsByTag("tbody")
                .first()
                .getElementsByTag("tr");

        Elements times = doc
                .getElementsByClass("thead-light")
                .first()
                .getElementsByTag("tr")
                .first()
                .getElementsByTag("th");

        Integer[] col = parseCol(times);

        List<Discipline> allDisciplines = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            //log.info("Парсится день: {}", elements.get(i).html());
            parseDay(elements.get(i), allDisciplines);
        }

        dataFilter(result, allDisciplines, date);

        Integer[] pairs = new Integer[result.size()];
        for (int i = 0; i < pairs.length; i++) {
            if(result.get(i) == null) {
                pairs[i] = col[i];
                continue;
            }
            pairs[i] = result.get(i).getColspan();
        }

        Integer[] res = res(col, pairs);

        log.info("col = {}", Arrays.toString(col));
        log.info("pairs = {}", Arrays.toString(pairs));
        log.info("res = {}", Arrays.toString(res));

        for (int i = 0; i < result.size(); i++) {
            if(result.get(i) == null)
                continue;
            result.get(i).setTime(timeByIndex(res[i]));
        }

        result.removeIf(Objects::isNull);

        result.forEach(el -> el.setGroupName(groupName));
        result.forEach(el -> {
            String d_name = el.getName();
            if(d_name.toLowerCase().contains("лек"))
                el.setType(DisciplineType.lec);
            else if(d_name.toLowerCase().contains("прак"))
                el.setType(DisciplineType.prac);
            else if(d_name.toLowerCase().contains("экз"))
                el.setType(DisciplineType.exam);
            else if(d_name.toLowerCase().contains("лаб"))
                el.setType(DisciplineType.lab);
            else if(d_name.toLowerCase().contains("каникулы"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("выходной"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("зач"))
                el.setType(DisciplineType.cred);
            else
                el.setType(DisciplineType.none);
        });

        result.forEach(memory::addDiscipline);
        return result;
    }

    private Integer[] parseCol(Elements elements){
        List<Integer> res = new ArrayList<>();
        for(Element el: elements)
            if(el.hasAttr("colspan"))
                res.add(Integer.parseInt(el.attr("colspan")));
        return res.toArray(new Integer[0]);
    }

    private void parseDay(Element el, List<Discipline> disc) { // сюда приходит тег tr
        Element nameOfClass = el.getElementsByTag("th").first();
        Elements disciplines = el.getElementsByTag("td");
        for (int i = 0; i < 8; i++) {
            //log.info("Парсится дисциплина: {}", disciplines.get(i).html());
            parseDiscipline(disciplines.get(i), disc, nameOfClass.html().split("\n")[1]);
        }
    }

    private void parseDiscipline(Element el, List<Discipline> disc, String date) { // сюда приходит тег td
        Discipline result = new Discipline();
        Elements spans = el.getElementsByTag("span");
        if(spans.size() == 0) {
            result.setDate(date);
            result.setColspan(1);
            disc.add(result);
            return;
        }
        result.setName(spans.get(0).text());
        String[] prepodAndAudience = spans.get(1).text().split(",");
        if (prepodAndAudience.length >= 3) {
            result.setTeacherName(prepodAndAudience[0].trim());
            result.setAudienceId(prepodAndAudience[2].trim());
        }
        if (spans.size() < 4)
            result.setSubgroup(0);
        else
            result.setSubgroup(spans.get(3).text().contains("1") ? 1 : 2);
        result.setDate(date);
        result.setColspan(Integer.parseInt(el.attr("colspan")));
        disc.add(result);
        //log.info("discipline add to list:\n{}", result);
    }

    private String timeByIndex(int index){
        return switch (index){
            case 0 -> "8:00-9:30";
            case 1 -> "9:40-11:10";
            case 2 -> "11:40-13:10";
            case 3 -> "13:30-15:00";
            case 4 -> "15:10-16:40";
            case 5 -> "16:50-18:20";
            case 6 -> "18:30-20:00";
            default -> "-----------";
        };
    }

    private Integer[] res(Integer[] col, Integer[] pairs){
        ArrayList<Integer> tmp_list = new ArrayList<>();
        for (int i = 0; i < col.length; i++) {
            if(col[i] == 1)
                tmp_list.add(i);
            else {
                tmp_list.add(i);
                tmp_list.add(i);
            }
        }
        for (int i = 0; i < pairs.length; i++) {
            if(pairs[i] == 2)
                tmp_list.remove(i+1);
        }
        return tmp_list.toArray(new Integer[0]);
    }
}
