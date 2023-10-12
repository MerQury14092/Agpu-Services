package com.merqury.agpu.timetable.notificatoin.DTO;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.notificatoin.Webhooks;
import com.merqury.agpu.timetable.notificatoin.interfaces.Subscriber;
import com.merqury.agpu.timetable.notificatoin.service.TimetableChangesPublisher;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Webhook implements Subscriber {
    private String url, group;

    @Override
    public void handleNotification(String id, Day chagedDay) {
        if(id.equals(group))
            if(!Webhooks.sendData(url, chagedDay))
                TimetableChangesPublisher.singleton().removeSubscriber(this);
    }
}
