package com.merqury.agpu.timetable.rest;

import com.merqury.agpu.timetable.DTO.Day;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class TestWebhookController {
    @PostMapping("/debug/webhook")
    public void test(@RequestBody Day day){
        log.warn("TEST WEBHOOK: {}", day);
    }
}
