package com.merqury.agpu.news.DTO;

import lombok.Data;

@Data
public class Article {
    private int id;
    private String title;
    private String description;
    private String date;
}
