package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Time1Entity;
import com.example.mygoReaction.repository.Time1Repository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

//@Slf4j
@RestController
public class Time1Controller {
    private final Time1Repository time1Repository;
    private static final Logger log = LoggerFactory.getLogger(Time1Controller.class);

    public Time1Controller(Time1Repository time1Repository) {
        this.time1Repository = time1Repository;
    }
    @GetMapping("/findById")
    public String hehe(){
        return "no record found";
    }

    public Time1Entity hehe(@RequestParam("id") Integer id){
        Time1Entity t1 = time1Repository.findById(id).orElse(null);
        if(t1==null){
            log.info("t1 is null");
        }else{
            log.info("t1: "+t1.getTime1().toString());
        }
        return t1;
    }

    @GetMapping("/findAll")
    public List<Time1Entity> hehehe(){
        return time1Repository.findAll();
    }

    @GetMapping("saveOne")
    public String hehehehe(@RequestParam("time") String time, @RequestParam("timestamp") String timestmamp){
        Time1Entity. Time1EntityBuilder t1b =  Time1Entity.builder();
        t1b.time1(LocalTime.parse(time))
                .created_by("Spring Boot")
//                .timestamp1(Instant.now())
                .timestamp1(Instant.parse(timestmamp))
        ;
        try {
            time1Repository.save(t1b.build());
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail";
        }
        return "Success";
    }
}
