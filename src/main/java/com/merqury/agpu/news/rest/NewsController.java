package com.merqury.agpu.news.rest;

import com.merqury.agpu.news.DTO.FullArticle;
import com.merqury.agpu.news.DTO.PreviewArticle;
import com.merqury.agpu.news.service.GetNewsService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/news")
@Log4j2
public class NewsController {
    private final GetNewsService service;

    @GetMapping("/{faculty}")
    public List<PreviewArticle> getArticlesByFaculty(
            @PathVariable String faculty
    ){
        try {
            return service.getArticlesByFaculty(faculty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{faculty}/{id}")
    public FullArticle getArticleById(
            @PathVariable String faculty,
            @PathVariable int id){
        try {
            return service.getArticleById(faculty, id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
