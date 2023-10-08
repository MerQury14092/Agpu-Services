package com.merqury.agpu.timetable.notificatoin.service;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.notificatoin.interfaces.Subscriber;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TimetableChangesPublisher {
    private final List<Subscriber> subscribers;

    public TimetableChangesPublisher(){
        subscribers = new ArrayList<>();
    }

    public void addSubscriber(Subscriber subscriber){
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber){
        subscribers.remove(subscriber);
    }

    public void publishNotification(String id, Day changes){
        for(Subscriber subscriber: subscribers)
            subscriber.handleNotification(id, changes);
    }
}
