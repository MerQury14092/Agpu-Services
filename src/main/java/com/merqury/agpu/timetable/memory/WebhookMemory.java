package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.ReservedWebhook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class WebhookMemory {
    private final List<ReservedWebhook> webhooks;

    public WebhookMemory(){
        webhooks = new CopyOnWriteArrayList<>();
        webhooks.add(
                ReservedWebhook.builder()
                        .url("http://localhost:8080/debug/webhook")
                        .groups(List.of("ВМ-ИВТ-1-1", "ВИ-ИГ-1-1").toArray(new String[0]))
                        .build()
        );
    }

    public void add(ReservedWebhook webhook){
        webhooks.add(webhook);
    }

    public void rm(String url){
        webhooks.removeIf(webhook -> webhook.url.equals(url));
    }

    public List<String> urls(String group){
        return webhooks.stream().filter(webhook -> webhook.isSubscriberOn(group)).map(webhook -> webhook.url).collect(Collectors.toList());
    }
}
