package com.merqury.agpu.timetable.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.merqury.agpu.timetable.enums.DisciplineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DisciplineForTeacher {
    @JsonIgnore
    private String date;
    private String time;
    private String name;
    @JsonIgnore
    private String teacherName;
    private String audienceId;
    private int subgroup;
    private DisciplineType type;
    private String groupName;
    @JsonIgnore
    private int colspan;

    public DisciplineForTeacher() {
    }

    public static DisciplineForTeacher holiday(){
        DisciplineForTeacher res = new DisciplineForTeacher();
        res.setName("HOLIDAY");
        return res;
    }

    public DisciplineForTeacher proxy() throws CloneNotSupportedException {
        return new DisciplineForTeacher(
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
}
