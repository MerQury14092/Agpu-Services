package com.merqury.agpu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgpuTimetableApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgpuTimetableApplication.class, args);
    }

    public static void async(Runnable task){
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
