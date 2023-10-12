package com.merqury.agpu.timetable.notificatoin.interfaces;

import com.merqury.agpu.timetable.DTO.Day;

public interface Subscriber {
    void handleNotification(String id, Day chagedDay);
}
