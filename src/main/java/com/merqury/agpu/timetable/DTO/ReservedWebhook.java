package com.merqury.agpu.timetable.DTO;


import lombok.Builder;

@Builder
public class ReservedWebhook {
    public String url;
    public String[] groups;

    public boolean isSubscriberOn(String group){
        for(String cur: groups)
            if(cur.equals(group))
                return true;

        return false;
    }

}
