package com.merqury.agpu.news.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FullArticle extends Article{
    String[] images;
}
