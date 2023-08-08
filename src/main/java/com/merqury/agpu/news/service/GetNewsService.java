package com.merqury.agpu.news.service;

import com.merqury.agpu.news.DTO.FullArticle;
import com.merqury.agpu.news.DTO.PreviewArticle;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.merqury.agpu.general.AgpuConstants.hostSite;

@Service
@Log4j2
public class GetNewsService {
    private static final String urlForEverything = hostSite+"/struktura-vuza/faculties-institutes/%s/news/news.php?PAGEN_1=%d";
    private static final String urlForArticle = hostSite+"/struktura-vuza/faculties-institutes/%s/news/news.php?ELEMENT_ID=%d";
    private final static String urlAgpuNews = "http://www.agpu.net/umu/Praktika/NEWS/Default.aspx";

    public List<PreviewArticle> getArticlesByFaculty(String faculty) throws IOException {
        List<PreviewArticle> res = new ArrayList<>();
        Document doc = Jsoup.parse(new URL(String.format(urlForEverything, faculty, 1)), 5000);
        for (Element el : doc.getElementsByTag("article"))
            res.add(parsePreviewArticleElement(el));
        return res;
    }

    public FullArticle getArticleById(String faculty, int id) throws IOException{
        Document doc;
        if(faculty.equals("agpu")) {
            doc = Jsoup.parse(new URL(urlAgpuNews + "?news=" + id), 5000);
            return parseArticlePageLegacy(Objects.requireNonNull(doc.getElementsByClass("col-md-9 md-padding main-content").first()), id);
        }
        else {
            doc = Jsoup.parse(new URL(String.format(urlForArticle, faculty, id)), 5000);
            return parseArticlePage(Objects.requireNonNull(doc.getElementsByClass("mb-3").first()));
        }
    }

    public List<PreviewArticle> getAgpuNews() throws IOException {
        Document doc = Jsoup.parse(new URL(urlAgpuNews), 5000);
        List<PreviewArticle> res = new ArrayList<>();
        for (Element el : doc.getElementsByTag("article"))
            res.add(parsePreviewArticleElementLegacy(el));
        return res;
    }

    private FullArticle parseArticlePage(Element element){
        Element el = element.getElementsByClass("news-detail-body").first();
        FullArticle res = new FullArticle();
        assert el != null;
        res.setDate(
                Objects.requireNonNull(element.getElementsByClass("news-detail-date")
                                .first())
                        .text()
        );
        res.setTitle(
                Objects.requireNonNull(el.getElementsByTag("h3")
                        .first()).text()
        );

        res.setId(
                Integer.parseInt(
                        element.attr("id")
                                .split("_")[2]
                )
        );
        StringBuilder description = new StringBuilder();
        for(Element p: el.getElementsByAttributeValue("style", "text-align: justify;"))
            description.append(p.text()).append("\n");
        res.setDescription(description.toString());

        for(Element img: el.getElementsByTag("img"))
            res.getImages().add("http://test.agpu.net"+img.attr("src"));
        return res;
    }

    private FullArticle parseArticlePageLegacy(Element element, int id){
        FullArticle res = new FullArticle();
        res.setTitle(
                element.getElementsByTag("h1")
                        .first()
                        .text()
        );
        res.setId(id);
        res.setDate(
                element.getElementsByClass("news-date")
                        .first()
                        .text()
        );
        StringBuilder description = new StringBuilder();
        for(Element el: element.getElementsByClass("news-detail")
                .first()
                .getElementsByTag("div")
                .first()
                .getElementsByTag("p")){
            if(el.attributes().isEmpty())
                description.append(el.text()).append("\n");
            else if (el.hasAttr("style")) {
                res.getImages().add(
                        el.getElementsByTag("img").attr("src")
                );
            }
        }
        res.setDescription(description.toString());
        return res;
    }

    private PreviewArticle parsePreviewArticleElement(Element el) {
        PreviewArticle res = new PreviewArticle();
        res.setId(
                Integer.parseInt(
                        Objects.requireNonNull(el.getElementsByTag("a")
                                        .first())
                                .attr("href")
                                .split("ELEMENT_ID=")[1]
                )
        );
        res.setTitle(
                Objects.requireNonNull(Objects.requireNonNull(el.getElementsByTag("h4")
                                        .first())
                                .getAllElements()
                                .first())
                        .text()
        );

        if(!el.getElementsByAttributeValue("style", "text-align: justify;").isEmpty())
            res.setDescription(
                    el.getElementsByAttributeValue("style", "text-align: justify;")
                            .first()
                            .text()
            );
        res.setPreviewImage(
                hostSite
                        +
                        el.getElementsByTag("img")
                                .first()
                                .attr("src")
        );
        res.setDate(
                el.getElementsByTag("li")
                        .get(1)
                        .text()
        );
        return res;
    }
    private PreviewArticle parsePreviewArticleElementLegacy(Element el) {
        PreviewArticle res = new PreviewArticle();
        res.setId(
                Integer.parseInt(
                        Objects.requireNonNull(el.getElementsByTag("a")
                                        .first())
                                .attr("href")
                                .split("news=")[1]
                )
        );
        res.setTitle(
                Objects.requireNonNull(Objects.requireNonNull(el.getElementsByTag("h4")
                                        .first())
                                .getAllElements()
                                .first())
                        .text()
        );

        if(!el.getElementsByTag("p").isEmpty())
            res.setDescription(
                    el.getElementsByTag("p")
                            .first()
                            .text()
            );
        res.setPreviewImage(
                "http://agpu.net"
                        +
                        el.getElementsByTag("img")
                                .first()
                                .attr("src")
        );
        res.setDate(
                el.getElementsByTag("li")
                        .get(1)
                        .text()
        );
        return res;
    }
}
