package com.merqury.agpu.timetable.interfaces;

import com.merqury.agpu.timetable.DTO.Day;

public interface Subscriber {
    void handleNotification(String id, Day chagedDay);
}
