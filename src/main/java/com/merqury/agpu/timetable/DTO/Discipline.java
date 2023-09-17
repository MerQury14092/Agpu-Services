package com.merqury.agpu.timetable.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.merqury.agpu.timetable.enums.DisciplineType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Discipline {
    @JsonIgnore
    private String date;
    private String time;
    private String name;
    private String teacherName;
    private String audienceId;
    private int subgroup;
    private DisciplineType type;
    private String groupName;
    @JsonIgnore
    private int colspan;

    public Discipline() {
    }

    public static Discipline holiday(){
        Discipline res = new Discipline();
        res.setName("HOLIDAY");
        return res;
    }

    public Discipline proxy() throws CloneNotSupportedException {
        return new Discipline(
                date,
                time,
                name,
                teacherName,
                audienceId,
                subgroup,
                type,
                groupName,
                colspan
        );
    }

    public DisciplineForTeacher mapForTeacher(){
        return DisciplineForTeacher.builder()
                .date(date)
                .time(time)
                .teacherName(teacherName)
                .name(name)
                .audienceId(audienceId)
                .subgroup(subgroup)
                .type(type)
                .groupName(groupName)
                .colspan(colspan)
                .build();
    }
}
