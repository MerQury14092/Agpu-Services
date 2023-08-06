package com.example.agputimetable.model;


import com.example.agputimetable.enums.DisciplineType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    private String groupName;
    @JsonIgnore
    private int colspan;

    public Discipline() {
    }

    public static Discipline holiday(String date, String groupName){
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
}
