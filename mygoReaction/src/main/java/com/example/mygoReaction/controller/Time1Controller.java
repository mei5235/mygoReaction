package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Time1Entity;
import com.example.mygoReaction.constant.Constant;
import com.example.mygoReaction.repository.Time1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

//@Slf4j
@RestController
@RequestMapping("time1")
public class Time1Controller {
    private final Time1Repository time1Repository;
    private static final Logger log = LoggerFactory.getLogger(Time1Controller.class);

    public Time1Controller(Time1Repository time1Repository) {
        this.time1Repository = time1Repository;
    }
    @RequestMapping(method = RequestMethod.GET, value="/findById")
    public String hehe(){
        return "no record found";
    }

    public Time1Entity hehe(@RequestParam("id") Integer id){
        Time1Entity t1 = time1Repository.findById(id).orElse(null);
        if(t1==null){
            log.info("t1 is null");
        }else{
//            log.info("t1: "+t1.getTime1().toString());
        }
        return t1;
    }

    @RequestMapping(method = RequestMethod.GET, value="/findAll")
    public List<Time1Entity> hehehe(){
        return time1Repository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value="saveOne")
    public String hehehehe(@RequestParam("time") String time){
        Time1Entity. Time1EntityBuilder t1b =  Time1Entity.builder();
        t1b.created_by(Constant.createdBy)
                .timestamp1(Instant.parse("1970-01-01T"+time+"Z"))
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
