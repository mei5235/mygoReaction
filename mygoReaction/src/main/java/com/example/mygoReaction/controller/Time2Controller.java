package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.service.Impl.Test2ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @RequestMapping(method = RequestMethod.GET, value="/findByDateBetween")
    public List<Test2Entity> findByDateBetween(@RequestParam("start_date") String startDateStr, @RequestParam("end_date") String endDateStr){
        return test2ServiceImpl.findByDateBetween(startDateStr, endDateStr);
    }

    @RequestMapping(method = RequestMethod.GET, value="/getScreenCapFromVideoByTimestamp")
    public void getScreenCapFromVideoByTimestamp(){
        test2ServiceImpl.getScreenCapFromVideoByTimestamp();
        log.info("success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "importFromSubtitle")
    public ResponseEntity<String> importFromSubtitle(@RequestBody String subtitleFileName){
        ResponseEntity<String> resp = null;
        try{
            resp = test2ServiceImpl.importFromSubtitle(subtitleFileName);
        }catch (Exception e){
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return resp;
    }

    @RequestMapping("/testSaveImage")
    public ResponseEntity<String> hehehehe(@RequestParam("filename") String filename){
        ResponseEntity<String> resp = null;
        try {
            test2ServiceImpl.hehehehe(filename);
        } catch (Exception e) {
            log.error("error");
            log.error(e.getMessage(),e);
        }
        return resp;
    }


}
