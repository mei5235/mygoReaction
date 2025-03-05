package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.service.Impl.Test2ServiceImpl;
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

    private final Test2ServiceImpl test2ServiceImpl;
    public Time2Controller(
            Test2ServiceImpl test2ServiceImpl
    ) {
        this.test2ServiceImpl = test2ServiceImpl;
    }

    @RequestMapping(method = RequestMethod.GET, value="/findAll")
    public List<Test2Entity> he(){
        return test2ServiceImpl.he();
    }


    @RequestMapping(method = RequestMethod.GET, value="/findByDateBetween")
    public List<Test2Entity> hehe(@RequestParam("start_date") String startDateStr, @RequestParam("end_date") String endDateStr){
        return test2ServiceImpl.hehe(startDateStr, endDateStr);
    }

    @RequestMapping(method = RequestMethod.GET, value="/s")
    public void s(){
        test2ServiceImpl.s();
        log.info("success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "importFromSubtitle")
    public ResponseEntity<String> hehehe(@RequestParam("subtitle_filename") String subtitleFileName){
        ResponseEntity<String> resp = null;
        try{
            resp = test2ServiceImpl.hehehe(subtitleFileName);
        }catch (Exception e){
            log.error("error");
        }
        return resp; // FIXME
    }

    @RequestMapping("/testSaveImage")
    public ResponseEntity<String> hehehehe(@RequestParam("filename") String filename){
        ResponseEntity<String> resp = null;
        try {
            test2ServiceImpl.hehehehe(filename);
        } catch (Exception e) {
            log.error("error");
        }
        return resp;
    }


}
