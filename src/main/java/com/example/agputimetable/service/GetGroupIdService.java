package com.example.agputimetable.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Service
public class GetGroupIdService {
    public int getId(String groupName){

        Scanner sc = new Scanner(Objects.requireNonNull(this.getClass().getResourceAsStream("/static/groupids")));

        StringBuilder builder = new StringBuilder();

        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }

        String line = searchLineWith(builder.toString(), groupName);

        if(line.equals("N"))
            return 0;

        return Integer.parseInt(line.split("&")[1].split("=")[1]);
    }

    public List<String> getAllGroups(){
        Scanner sc = new Scanner(Objects.requireNonNull(this.getClass().getResourceAsStream("/static/groupids")));

        StringBuilder builder = new StringBuilder();

        while (sc.hasNextLine()){
            builder.append(sc.nextLine()).append("\n");
        }

        List<String> res = new ArrayList<>();

        String[] arr = builder.toString().split("\n");
        for(String cur : arr) {
            if (cur.contains("<a class=\"btn btn-outline-primary\" href=\"/Raspisanie/SearchedRaspisanie")) {
                res.add(cur.replace("<", ">").split(">")[2]);
            }
        }
        return res;
    }

    private String searchLineWith(String text, String substring){
        String[] arr = text.split("\n");
        for(String cur : arr) {
            if (cur.contains(substring)) {
                return cur;
            }
        }
        return "N";
    }
}
