package com.merqury.agpu.news.service;

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

@Service
@Log4j2
public class GetNewsService {
    private static final String url = "http://test.agpu.net/struktura-vuza/faculties-institutes/%s/news/news.php?PAGEN_1=%d";

    public List<PreviewArticle> getArticlesByFaculty(String faculty) throws IOException {
        List<PreviewArticle> res = new ArrayList<>();
        Document doc = Jsoup.parse(new URL(String.format(url, faculty, 1)), 5000);
        for (Element el : doc.getElementsByTag("article"))
            res.add(parseArticleElement(el));
        return res;
    }

    private PreviewArticle parseArticleElement(Element el) {
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
                "http://test.agpu.net"
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
