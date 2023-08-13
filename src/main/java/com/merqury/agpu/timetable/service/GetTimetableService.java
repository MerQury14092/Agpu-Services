package com.merqury.agpu.timetable.service;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Discipline;
import com.merqury.agpu.timetable.DTO.DisciplineForTeacher;
import com.merqury.agpu.timetable.DTO.TeacherDay;
import com.merqury.agpu.timetable.enums.DisciplineType;
import com.merqury.agpu.timetable.memory.TeacherTimetableMemory;
import com.merqury.agpu.timetable.memory.TimetableMemory;
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
    private final TimetableMemory studentTimetableMemory;
    private final TeacherTimetableMemory teacherTimetableMemory;
    private final GetTeacherIdService getTeacherIdService;

    public GetTimetableService(GetGroupIdService getGroupIdService, TimetableMemory memory, TeacherTimetableMemory teacherMemory, GetTeacherIdService getTeacherIdService) {
        this.getGroupIdService = getGroupIdService;
        this.studentTimetableMemory = memory;
        this.teacherTimetableMemory = teacherMemory;
        this.getTeacherIdService = getTeacherIdService;
    }

    private static final int ownerId = 118;
    private static final String url = "http://www.it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=%d&SearchId=%d&SearchString=None&Type=%s&WeekId=%d";

    public List<Day> getDisciplines(String groupName, String startDate, String endDate) throws IOException {
        List<Day> result = new ArrayList<>();
        for(String date: getDatesBetween(startDate, endDate)) {
            result.add(getDisciplines(groupName, date));
        }
        return proxyList(result);
    }

    public List<TeacherDay> getDisciplinesTeacher(String teacherName, String startDate, String endDate) throws IOException {
        List<TeacherDay> result = new ArrayList<>();
        for(String date: getDatesBetween(startDate, endDate)) {
            result.add(getDisciplinesByTeacher(teacherName, date));
        }
        return proxyListTeacher(result);
    }

    public Day getDisciplines(String groupName, String date) throws IOException {


        int mappingWeekId = 3100;

        long weekId = mappingWeekId + countDays(date) / 7;

        return parseHtml(String.format(
                        url,
                        ownerId,
                        getGroupIdService.getId(groupName),
                        "Group",
                        weekId),
                date,
                groupName,
                true
        ).proxy();
    }

    public Day getDisciplinesWithoutCache(String groupName, String date) throws IOException {


        int mappingWeekId = 3100;

        long weekId = mappingWeekId + countDays(date) / 7;

        return parseHtml(String.format(
                        url,
                        ownerId,
                        getGroupIdService.getId(groupName),
                        "Group",
                        weekId),
                date,
                groupName,
                false
        ).proxy();
    }

    public TeacherDay getDisciplinesByTeacher(String teacherName, String date) throws IOException{
        int mappingWeekId = 3100;

        long weekId = mappingWeekId + countDays(date) / 7;

        return parseHtmlTeacher(String.format(
                        url,
                        ownerId,
                        getTeacherIdService.getId(teacherName),
                        "Teacher",
                        weekId),
                date,
                teacherName
        ).proxy();
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
            if (result.isEmpty())
                break;
            if (result.get(result.size() - 1) == null)
                result.remove(result.size() - 1);
            else
                break;
        }
    }
    private void dataFilterForTeacher(List<DisciplineForTeacher> result, List<Discipline> allDisciplines, String cur) {
        for (Discipline discipline : allDisciplines) {
            if (discipline.getDate().equals(cur)) {
                if (discipline.getName() != null)
                    result.add(discipline.mapForTeacher());
                else
                    result.add(null);
            }
        }
        for (; ; ) {
            if (result.isEmpty())
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

    private long countDays(String endDate) {
        String[] part = "29.08.2022".split("\\.");
        Date dt = new Date(
                Integer.parseInt(part[2]),
                Integer.parseInt(part[1]) - 1,
                Integer.parseInt(part[0])
        );
        long currentTime = dt.getTime();
        long mappingWeek = currentTime / 1000 / 60 / 60 / 24;


        part = endDate.split("\\.");
        dt = new Date(
                Integer.parseInt(part[2]),
                Integer.parseInt(part[1]) - 1,
                Integer.parseInt(part[0])
        );
        currentTime = dt.getTime();
        long currentWeek = (long) (currentTime / 1000d / 60 / 60 / 24);

        return (currentWeek - mappingWeek);
    }


    private Day parseHtml(String url, String date, String groupName, boolean cache) throws IOException {
        Day day = studentTimetableMemory.getDisciplineByDate(groupName, date);
        List<Discipline> result = day.getDisciplines();
        if(cache){
            if(!result.isEmpty()) {
                log.info("info: memory call");
                return day;
            }

        }
        result = new ArrayList<>();
        log.info("info: it-institut call");
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

        Document doc = Jsoup.parse(html.toString());

        Elements elements = Objects.requireNonNull(Objects.requireNonNull(doc
                                .getElementsByClass("table")
                                .first())
                        .getElementsByTag("tbody")
                        .first())
                .getElementsByTag("tr");

        Elements times = Objects.requireNonNull(Objects.requireNonNull(doc
                                .getElementsByClass("thead-light")
                                .first())
                        .getElementsByTag("tr")
                        .first())
                .getElementsByTag("th");

        Integer[] col = parseCol(times);

        List<Discipline> allDisciplines = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            parseDay(elements.get(i), allDisciplines);
        }

        if(cache){
            for(String tmpDate: getDatesBetween(allDisciplines.get(0).getDate(), allDisciplines.get(allDisciplines.size()-1).getDate())){
                List<Discipline> tmpArray = new ArrayList<>();
                if(tmpDate.equals(date))
                    continue;
                dataFilter(tmpArray, allDisciplines, tmpDate);
                assignMissingDataAndCaching(groupName, tmpArray, col, tmpDate);
            }
        }

        dataFilter(result, allDisciplines, date);

        if(cache)
            assignMissingDataAndCaching(groupName, result, col, date);

        String groupNama;
        if(!result.isEmpty())
            groupNama = result.get(0).getGroupName();
        else
            groupNama = groupName;

        if(result.isEmpty()) {
            result.add(Discipline.holiday());
        }

        return Day.builder()
                .date(date)
                .groupName(groupNama)
                .disciplines(result)
                .build();
    }

    private TeacherDay parseHtmlTeacher(String url, String date, String teacherName) throws IOException {

        String fio = getTeacherIdService.getFIO(teacherName).split(",")[0];

        if(fio.equals("None"))
            return TeacherDay.builder()
                    .teacherName("None")
                    .date(date)
                    .disciplines(List.of(DisciplineForTeacher.holiday()))
                    .build();
        teacherName = fio;

        TeacherDay day = teacherTimetableMemory.getDisciplineByDate(teacherName, date);
        List<DisciplineForTeacher> result = day.getDisciplines();
        if(!result.isEmpty()) {
            log.info("info: memory call");
            return day;
        }
        result = new ArrayList<>();

        log.info("info: it-institut call");
        URL url1 = new URL(url);

        Document doc = Jsoup.parse(url1, 15000);

        Elements elements = Objects.requireNonNull(Objects.requireNonNull(doc
                                .getElementsByClass("table")
                                .first())
                        .getElementsByTag("tbody")
                        .first())
                .getElementsByTag("tr");

        Elements times = Objects.requireNonNull(Objects.requireNonNull(doc
                                .getElementsByClass("thead-light")
                                .first())
                        .getElementsByTag("tr")
                        .first())
                .getElementsByTag("th");

        Integer[] col = parseCol(times);

        List<Discipline> allDisciplines = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            parseDay(elements.get(i), allDisciplines);
        }

        for(String tmpDate: getDatesBetween(allDisciplines.get(0).getDate(), allDisciplines.get(allDisciplines.size()-1).getDate())){
            List<DisciplineForTeacher> tmpArray = new ArrayList<>();
            if(tmpDate.equals(date))
                continue;
            dataFilterForTeacher(tmpArray, allDisciplines, tmpDate);
            assignMissingDataAndCachingForTeacher(teacherName, tmpArray, col, tmpDate);
        }



        dataFilterForTeacher(result, allDisciplines, date);

        assignMissingDataAndCachingForTeacher(teacherName, result, col, date);
        String teacherNama = "";

        if(!result.isEmpty())
            teacherNama = result.get(0).getTeacherName();
        if(result.isEmpty() || result.get(0).getName().equals("HOLIDAY"))
            teacherNama = teacherName;

        if(result.isEmpty()) {
            result.add(DisciplineForTeacher.holiday());
        }
        return TeacherDay.builder()
                .teacherName(teacherNama)
                .date(date)
                .disciplines(result)
                .build();
    }

    private void assignMissingDataAndCaching(String groupName, List<Discipline> result, Integer[] col, String date) {
        Integer[] pairs = new Integer[result.size()];
        for (int i = 0; i < pairs.length; i++) {
            if(result.get(i) == null) {
                pairs[i] = col[i];
                continue;
            }
            pairs[i] = result.get(i).getColspan();
        }

        Integer[] res = res(col, pairs);

        for (int i = 0; i < result.size(); i++) {
            if(result.get(i) == null)
                continue;
            result.get(i).setTime(timeByIndex(res[i]));
        }

        result.removeIf(Objects::isNull);

        result.forEach(el -> {
            String d_name = el.getName();
            if(d_name.toLowerCase().contains("лек."))
                el.setType(DisciplineType.lec);
            else if(d_name.toLowerCase().contains("прак."))
                el.setType(DisciplineType.prac);
            else if(d_name.toLowerCase().contains("экз."))
                el.setType(DisciplineType.exam);
            else if(d_name.toLowerCase().contains("лаб."))
                el.setType(DisciplineType.lab);
            else if(d_name.toLowerCase().contains("каникулы"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("выходной"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("зач."))
                el.setType(DisciplineType.cred);
            else if(d_name.toLowerCase().contains("конс."))
                el.setType(DisciplineType.cons);
            else if(d_name.toLowerCase().contains("фэпо"))
                el.setType(DisciplineType.fepo);
            else
                el.setType(DisciplineType.none);
        });


        if(!result.isEmpty()){
            Day check = studentTimetableMemory.getDisciplineByDate(groupName, result.get(0).getDate());
            if(!check.isEmpty()) {
                return;
            }
        }

        String groupNama;
        if(!result.isEmpty())
            groupNama = result.get(0).getGroupName();
        else
            groupNama = groupName;

        if(result.isEmpty()) {
            result.add(Discipline.holiday());
        }


        studentTimetableMemory.addDiscipline(Day.builder()
                .groupName(groupNama)
                .date(date)
                .disciplines(result)
                .build()
        );
    }
    private void assignMissingDataAndCachingForTeacher(String teacherName, List<DisciplineForTeacher> result, Integer[] col, String date) {
        Integer[] pairs = new Integer[result.size()];
        for (int i = 0; i < pairs.length; i++) {
            if(result.get(i) == null) {
                pairs[i] = col[i];
                continue;
            }
            pairs[i] = result.get(i).getColspan();
        }

        Integer[] res = res(col, pairs);

        for (int i = 0; i < result.size(); i++) {
            if(result.get(i) == null)
                continue;
            result.get(i).setTime(timeByIndex(res[i]));
        }

        result.removeIf(Objects::isNull);

        result.forEach(el -> {
            String d_name = el.getName();
            if(d_name.toLowerCase().contains("лек."))
                el.setType(DisciplineType.lec);
            else if(d_name.toLowerCase().contains("прак."))
                el.setType(DisciplineType.prac);
            else if(d_name.toLowerCase().contains("экз."))
                el.setType(DisciplineType.exam);
            else if(d_name.toLowerCase().contains("лаб."))
                el.setType(DisciplineType.lab);
            else if(d_name.toLowerCase().contains("каникулы"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("выходной"))
                el.setType(DisciplineType.hol);
            else if(d_name.toLowerCase().contains("зач."))
                el.setType(DisciplineType.cred);
            else if(d_name.toLowerCase().contains("конс."))
                el.setType(DisciplineType.cons);
            else if(d_name.toLowerCase().contains("фэпо"))
                el.setType(DisciplineType.fepo);
            else
                el.setType(DisciplineType.none);
        });


        if(!result.isEmpty()){
            TeacherDay check = teacherTimetableMemory.getDisciplineByDate(teacherName, result.get(0).getDate());
            if(!check.isEmpty()) {
                return;
            }
        }

        if(result.isEmpty())
            result.add(DisciplineForTeacher.holiday());
        teacherTimetableMemory.addDiscipline(TeacherDay.builder()
                .teacherName(teacherName)
                .date(date)
                .disciplines(result)
                .build()
        );
    }

    private List<Day> proxyList(List<Day> source){
        List<Day> res = new ArrayList<>();
        for(Day day: source)
            res.add(day.proxy());
        return res;
    }
    private List<TeacherDay> proxyListTeacher(List<TeacherDay> source){
        List<TeacherDay> res = new ArrayList<>();
        for(TeacherDay day: source)
            res.add(day.proxy());
        return res;
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
            assert nameOfClass != null;
            parseDiscipline(disciplines.get(i), disc, nameOfClass.html().split("\n")[1]);
        }
    }

    private void parseDiscipline(Element el, List<Discipline> disc, String date) { // сюда приходит тег td
        Discipline result = new Discipline();
        Elements spans = el.getElementsByTag("span");
        if(spans.isEmpty()) {
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
        } else if (prepodAndAudience.length == 2) {
            result.setTeacherName(prepodAndAudience[0].trim());
            result.setAudienceId(prepodAndAudience[1].trim());
        }
        if (spans.size() < 4)
            result.setSubgroup(0);
        else
            result.setSubgroup(spans.get(3).text().contains("1") ? 1 : 2);
        result.setDate(date);
        result.setGroupName(spans.get(2).text().replace("(", "").replace(")",""));
        result.setColspan(Integer.parseInt(el.attr("colspan")));
        disc.add(result);
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
