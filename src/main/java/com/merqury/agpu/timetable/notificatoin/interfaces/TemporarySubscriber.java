package com.merqury.agpu.timetable.notificatoin.interfaces;


import com.merqury.agpu.timetable.DTO.Day;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.merqury.agpu.AgpuTimetableApplication.*;

public class TemporarySubscriber implements Subscriber {
    private volatile Day result;
    private final int secondsForWaitNotification;
    private final String expectedGroupName;

    public TemporarySubscriber(int secondsForWaitNotification, String expectedGroupName) {
        this.secondsForWaitNotification = secondsForWaitNotification;
        this.expectedGroupName = expectedGroupName;
        this.result = null;
        async(this::startDaemon);
    }

    private void startDaemon(){
       trySleep(TimeUnit.SECONDS.toMillis(secondsForWaitNotification));
       setResultToNone();
    }

    private void trySleep(long milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setResultToNone(){
        result = Day.builder()
                .date("NOT")
                .build();
    }

    @Override
    public void handleNotification(String id, Day chagedDay) {
        if(Objects.equals(id, expectedGroupName))
            result = chagedDay;
    }

    public Day get(){
        sleepWhileResultEqualsNull();
        if(Objects.equals(result.getDate(), "NOT"))
            return null;
        return result;
    }

    private void sleepWhileResultEqualsNull(){
        while (result == null)
            Thread.onSpinWait();
    }
}
