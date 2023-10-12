package com.merqury.agpu.timetable.notificatoin.interfaces;


import com.merqury.agpu.timetable.DTO.Day;

import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

public class TemporarySubscriber implements Subscriber {
    private volatile Day day;
    private final int seconds;
    private final String expectedGroupName;

    public TemporarySubscriber(int seconds, String expectedGroupName) {
        this.seconds = seconds;
        this.expectedGroupName = expectedGroupName;
        this.day = null;
        async(this::startDaemon);
    }

    private void startDaemon(){
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        day = Day.builder()
                .date("NOT")
                .build();
    }

    @Override
    public void handleNotification(String id, Day chagedDay) {
        if(id.equals(expectedGroupName))
            day = (Day) chagedDay;
    }

    public Day get(){
        while (day == null)
            Thread.onSpinWait();
        if(day.getDate().equals("NOT"))
            return null;
        return day;
    }
}
