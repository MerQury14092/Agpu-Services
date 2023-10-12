package com.merqury.agpu.timetable.notificatoin.service;

import com.merqury.agpu.timetable.notificatoin.DTO.Webhook;

import java.util.ArrayList;
import java.util.List;

public class WebhookRegistryService {
    private final TimetableChangesPublisher changesPublisher;
    private final List<Webhook> memory;
    private static final WebhookRegistryService singleton;

    static {
        singleton = new WebhookRegistryService();
    }

    public static WebhookRegistryService singleton(){
        return singleton;
    }

    private WebhookRegistryService() {
        this.memory = new ArrayList<>();
        this.changesPublisher = TimetableChangesPublisher.singleton();
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
