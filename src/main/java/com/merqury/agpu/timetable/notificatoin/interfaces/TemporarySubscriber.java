package com.merqury.agpu.timetable.notificatoin.interfaces;

import com.merqury.agpu.timetable.DTO.Day;
import com.merqury.agpu.timetable.DTO.GroupDay;
import com.merqury.agpu.timetable.notificatoin.interfaces.Subscriber;

import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

public class TemporarySubscriber implements Subscriber {
    private volatile GroupDay day;
    private int seconds;
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
        day = GroupDay.builder()
                .date("NOT")
                .build();
    }

    @Override
    public void handleNotification(String id, Day chagedDay) {
        if(id.equals(expectedGroupName))
            day = (GroupDay) chagedDay;
    }

    public GroupDay get(){
        while (day == null)
            Thread.onSpinWait();
        if(day.getDate().equals("NOT"))
            return null;
        return day;
    }
}
