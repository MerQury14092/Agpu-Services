package com.merqury.agpu.timetable.memory;

import com.merqury.agpu.timetable.DTO.TeacherDay;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class TeacherTimetableMemory {

    public TeacherTimetableMemory(){
    }

    public void addDiscipline(TeacherDay day){}

    public TeacherDay getDisciplineByDate(String teacherName, String date){
        return TeacherDay.builder()
                .date(date)
                .teacherName(teacherName)
                .disciplines(List.of())
                .build();
    }
}
