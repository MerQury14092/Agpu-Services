package com.merqury.agpu.timetable.notificatoin.service;

import com.merqury.agpu.timetable.notificatoin.DTO.Webhook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebhookRegistryService {
    private final TimetableChangesPublisher changesPublisher;
    private final List<Webhook> memory;


    public WebhookRegistryService(TimetableChangesPublisher changesPublisher) {
        this.memory = new ArrayList<>();
        this.changesPublisher = changesPublisher;
    }

    public int addWebhook(Webhook webhook){
        if(hasWebhook(webhook))
            return 1;
        changesPublisher.addSubscriber(webhook);
        memory.add(webhook);
        return 0;
    }

    public List<Webhook> getWebhooks(String url){
        return memory.stream().filter(el -> el.getUrl().equals(url)).toList();
    }

    public boolean hasWebhook(Webhook wbhk){
        for(Webhook webhook: getWebhooks(wbhk.getUrl()))
            if(webhook.getGroup().equals(wbhk.getGroup()))
                return true;
        return false;
    }

    public void removeWebhook(String url){
        memory.removeIf(el -> {
            if(el.getUrl().equals(url)){
                changesPublisher.removeSubscriber(el);
                return true;
            }
            return false;
        });
    }
}
