package com.merqury.agpu.timetable.notificatoin.interfaces;

import com.merqury.agpu.timetable.DTO.TimetableDay;

public interface Subscriber {
    void handleNotification(String id, TimetableDay chagedTimetableDay);
}
