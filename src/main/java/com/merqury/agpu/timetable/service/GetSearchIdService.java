package com.merqury.agpu.timetable.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merqury.agpu.timetable.DTO.Groups;
import com.merqury.agpu.timetable.DTO.SearchProduct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GetSearchIdService {
    private final String url;
    private final ObjectMapper jsonConverter;
    private final String urlToMainPage;

    public GetSearchIdService(){
        this.jsonConverter = new ObjectMapper();
        url = "http://www.it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=%s";
        urlToMainPage = "http://www.it-institut.ru/SearchString/Index/118";
    }

    public int getGroupId(String groupName){
        SearchProduct[] result = tryToGetSearchProductArrayFromUrl(url, groupName);
        return Arrays.stream(result)
                .filter(element -> Objects.equals(element.Type, "Group"))
                .map(element -> element.SearchId)
                .findFirst()
                .orElse(0);
    }

    public int getTeacherId(String teacherName){
        SearchProduct[] result = tryToGetSearchProductArrayFromUrl(url, teacherName);
        if(result.length == 0)
            return 1386;
        else
            return result[0].SearchId;
    }

    public String getTeacherFullName(String teacherName){
        SearchProduct[] result = tryToGetSearchProductArrayFromUrl(url, teacherName);
        return Arrays.stream(result)
                .filter(element -> Objects.equals(element.Type, "Teacher"))
                .map(element -> element.SearchContent)
                .findFirst()
                .orElse("None");
    }

    public String getFullGroupName(String groupName){
        SearchProduct[] result = tryToGetSearchProductArrayFromUrl(url, groupName);
        return Arrays.stream(result)
                .filter(element -> Objects.equals(element.Type, "Group"))
                .map(element -> element.SearchContent)
                .findFirst()
                .orElse("None");
    }

    private SearchProduct[] tryToGetSearchProductArrayFromUrl(String url, String searchString) {
        try {
            return getSearchProductArrayFromUrl(url, searchString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchProduct[] getSearchProductArrayFromUrl(String url, String searchString) throws IOException{
        return jsonConverter.readValue(
                new URL(url.formatted(URLEncoder.encode(searchString, StandardCharsets.UTF_8))),
                SearchProduct[].class
        );
    }

    public List<Groups> getAllGroups(){
        String dataFromMainPage = getDataFromUrl(urlToMainPage);
        List<Groups> result = new ArrayList<>();
        Document document = Jsoup.parse(dataFromMainPage);
        document.getElementsByClass("card")
                .forEach(element -> result.add(parseCardElement(element)));
        return result;
    }

    private String getDataFromUrl(String url){
        InputStream inputStream = getStreamByUrl(url);
        return readStringFromInputStream(inputStream);
    }

    private String readStringFromInputStream(InputStream stream){
        Scanner sc = new Scanner(stream);
        StringBuilder builder = new StringBuilder();
        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }
        return builder.toString();
    }

    private InputStream getStreamByUrl(String url){
        try {
            return tryToOpenStreamOnUrl(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream tryToOpenStreamOnUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }

    private Groups parseCardElement(Element el){
        Groups res = new Groups();
        res.setFacultyName(
                el.getElementsByTag("button").first().text()
        );

        el.getElementsByClass("p-2")
                .forEach(element -> res.getGroups().add(getGroupNameFromP2Element(element)));
        return res;
    }

    private String getGroupNameFromP2Element(Element element){
        return element.getAllElements().first().text();
    }
}
