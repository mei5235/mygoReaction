package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.service.Test2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("time2")
public class Time2Controller {

    private static final Logger log = LoggerFactory.getLogger(Time2Controller.class);

    private final Test2Service test2Service;
    public Time2Controller(
            Test2Service test2Service
    ) {
        this.test2Service = test2Service;
    }

    @RequestMapping(method = RequestMethod.GET, value="/findAll")
    public List<Test2Entity> he(){
        return test2Service.he();
    }


    @RequestMapping(method = RequestMethod.GET, value="/findByDateBetween")
    public List<Test2Entity> hehe(@RequestParam("start_date") String startDateStr, @RequestParam("end_date") String endDateStr){
        return test2Service.hehe(startDateStr, endDateStr);
    }

    @RequestMapping(method = RequestMethod.POST, value = "importFromSubtitle")
    public ResponseEntity<String> hehehe(@RequestParam("subtitle_filename") String subtitleFileName){
        ResponseEntity<String> resp = null;
        try{
            resp = test2Service.hehehe(subtitleFileName);
        }catch (Exception e){
            log.error("error");
        }
        return resp; // FIXME
    }

    @RequestMapping("/testSaveImage")
    public ResponseEntity<String> hehehehe(@RequestParam("filename") String filename){
        ResponseEntity<String> resp = null;
        try {
            test2Service.hehehehe(filename);
        } catch (Exception e) {
            log.error("error");
        }
        return resp;
    }


}
