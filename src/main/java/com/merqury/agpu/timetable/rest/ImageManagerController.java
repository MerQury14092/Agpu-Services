package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.Discipline;
import com.merqury.agpu.timetable.DTO.GroupDay;
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
            @RequestBody Discipline[] disc,
            HttpServletResponse response
    ) throws IOException {
        if(disc.length == 2) {
            BufferedImage res = service.getImageByTimetableOfSubDiscipline(disc[0], disc[1], 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(res, "PNG", baos);
            return baos.toByteArray();
        }
        else if(disc.length == 1) {
            BufferedImage res = service.getImageByTimetableOfDiscipline(disc[0], 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(res, "PNG", baos);
            return baos.toByteArray();
        }
        response.sendError(400);
        return null;
    }

    @PostMapping(value = "/day", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] day(
            @RequestBody GroupDay day,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {
        if(request.getParameter("vertical") == null && request.getParameter("horizontal") == null)
            response.sendError(400);
        else if(request.getParameter("vertical") != null && request.getParameter("horizontal") != null)
            response.sendError(400);

        if(request.getParameter("vertical") == null) {
            BufferedImage res = service.getImageByTimetableOfDayHorizontal(day, 600, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(res, "PNG", baos);
            return baos.toByteArray();
        }
        BufferedImage res = service.getImageByTimetableOfDayVertical(day, 600, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(res, "PNG", baos);
        return baos.toByteArray();
    }

    @PostMapping(value = "/6days", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] days(
            @RequestBody GroupDay[] days,
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

        if(request.getParameter("vertical") == null) {
            BufferedImage res = service.getImageByTimetableOf6DaysHorizontal(days, 600, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(res, "PNG", baos);
            return baos.toByteArray();
        }
        return null;
    }
}
