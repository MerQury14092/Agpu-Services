package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.Discipline;
import com.merqury.agpu.timetable.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/timetable/image")
public class ImageManagerController {
    private final ImageService service;

    public ImageManagerController(ImageService service) {
        this.service = service;
    }

    @PostMapping(value = "/discipline", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] discipline(
            @RequestBody Discipline disc1,
            @RequestBody(required = false) Discipline disc2
    ) throws IOException {
        if(disc2 != null) {
            BufferedImage res = service.getImageByTimetableOfSubDiscipline(disc1, disc2);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(res, "PNG", baos);
            return baos.toByteArray();
        }
        BufferedImage res = service.getImageByTimetableOfDiscipline(disc1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(res, "PNG", baos);
        return baos.toByteArray();
    }

    @PostMapping(value = "/day", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] day(
            @RequestBody Day day,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {
        if(request.getParameter("vertical") == null && request.getParameter("horizontal") == null)
            response.sendError(400);
        else if(request.getParameter("vertical") != null && request.getParameter("horizontal") != null)
            response.sendError(400);

        // TODO: realize vertical column
        if(request.getParameter("vertical") != null){
            response.sendError(501);
            return null;
        }

        BufferedImage res = service.getImageByTimetableOfDay(day, request.getParameter("vertical") != null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(res, "PNG", baos);
        return baos.toByteArray();
    }

    @PostMapping(value = "/6days", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] days(
            @RequestBody Day[] days,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {
        if(request.getParameter("vertical") == null && request.getParameter("horizontal") == null)
            response.sendError(400);
        else if(request.getParameter("vertical") != null && request.getParameter("horizontal") != null)
            response.sendError(400);

        // TODO: realize vertical table
        if(request.getParameter("vertical") != null){
            response.sendError(501);
            return null;
        }

        BufferedImage res = service.getImageByTimetableOf6Days(days, request.getParameter("vertical") != null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(res, "PNG", baos);
        return baos.toByteArray();
    }
}
