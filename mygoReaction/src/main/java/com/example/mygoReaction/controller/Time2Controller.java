package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.model.dto.SavedSeriesDto;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.service.Impl.Test2ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("time2")
public class Time2Controller {

    private static final Logger log = LoggerFactory.getLogger(Time2Controller.class);

    private final Test2ServiceImpl test2ServiceImpl;
    private final SavedSeriesRepository savedSeriesRepository;
    public Time2Controller(
            Test2ServiceImpl test2ServiceImpl,
            SavedSeriesRepository savedSeriesRepository
    ) {
        this.test2ServiceImpl = test2ServiceImpl;
        this.savedSeriesRepository = savedSeriesRepository;
    }

//    @RequestMapping(method = RequestMethod.POST, value = "/hehehehehehe")
//    public ResponseEntity<String> hehehehehehe(@RequestBody SavedSeriesDto savedSeriesDto){
//        SavedSeriesEntity s = savedSeriesRepository.findBySeriesNameAndSeasonAndEpisode(
//                savedSeriesDto.getSeries_name(),
//                savedSeriesDto.getSeason(),
//                savedSeriesDto.getEpisode()
//        ).orElse(null);
//        return new ResponseEntity<String>(s == null ?"No Records found.":s.getSeriesId().toString(),HttpStatus.OK);
//    }

    @RequestMapping(method = RequestMethod.POST, value="/findBySeriesIdAndKeyword")
    public ResponseEntity<String> findBySeriesIdAndKeyword(@RequestBody Test2Dto t2d){
        return test2ServiceImpl.findBySeriesIdAndKeyword(t2d);
    }

//    @RequestMapping(method = RequestMethod.GET, value="/findByDateBetween")
//    public List<Test2Entity> findByDateBetween(@RequestParam("start_date") String startDateStr, @RequestParam("end_date") String endDateStr){
//        return test2ServiceImpl.findByDateBetween(startDateStr, endDateStr);
//    }

    @RequestMapping(method = RequestMethod.GET, value="/getScreenCapFromVideoByTimestamp")
    public void getScreenCapFromVideoByTimestamp(){
        test2ServiceImpl.hehe();
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

//    @RequestMapping("/saveImage2DB")
//    public ResponseEntity<String> saveImage2DB(@RequestParam("filename") String filename){
//        ResponseEntity<String> resp = null;
//        try {
//            test2ServiceImpl.saveImage2DB(filename);
//        } catch (Exception e) {
//            log.error("error");
//            log.error(e.getMessage(),e);
//        }
//        return resp;
//    }


}
