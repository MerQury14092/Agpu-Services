package com.merqury.agpu.timetable.service;


import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Discipline;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageService {
    private final Font font;

    public ImageService() throws IOException, FontFormatException {
        this.font = Font.createFont(Font.TRUETYPE_FONT, ImageService.class.getResourceAsStream("/fonts/timetable_font.ttf"));
        this.font.deriveFont(Font.PLAIN);
    }

    public BufferedImage getImageByTimetableOf7Days(Day[] day, boolean isVertical){
        BufferedImage res = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        return res;
    }

    public BufferedImage getImageByTimetableOfDay(Day days){
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
}
