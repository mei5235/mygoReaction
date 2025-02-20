package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.repository.Test2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("time2")
public class Time2Controller {
    @Autowired
    ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(Time2Controller.class);

    private final Test2Repository test2Repository;
    public Time2Controller(Test2Repository test2Repository) {
        this.test2Repository = test2Repository;
    }

    @RequestMapping(method = RequestMethod.GET, value="/findAll")
    public List<Test2Entity> he(){
        List<Test2Entity> resp = test2Repository.findAll();
        return resp;
    }

    @RequestMapping(method = RequestMethod.GET, value="/findByDateBetween")
    public List<Test2Entity> hehe(@RequestParam("start_date") String startDateStr, @RequestParam("end_date") String endDateStr){
        Instant startDate = Instant.parse("1970-01-01T"+startDateStr+"Z");
        Instant endDate = Instant.parse("1970-01-01T"+endDateStr+"Z");
//        List<Test2Entity> resp = test2Repository.findByDateBetween(startDate,endDate);
//        return resp;
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "importFromSubtitle")
    public ResponseEntity<String> hehehe(@RequestParam("subtitle_filename") String subtitleFileName){
        String pathFromResource = "my_go_subtitle/";
        File subtitleFile = null;
        BufferedReader bfr = null;

        try {
            subtitleFile = resourceLoader
                    .getResource("classpath:"+pathFromResource+subtitleFileName)
                    .getFile();
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Specified subtitle file not found",
                    HttpStatusCode.valueOf(500)
            );
        }

        // information array from subtitle filename
        // 0: series_name, 1: season, 2: episode
        String[] arr = subtitleFileName.split("-");
        if(arr.length<3) {
            return ResponseEntity
                    .badRequest()
                    .body("Inappropriate subtitle filename.");
        }

        try(FileReader fr = new FileReader(subtitleFile)){
            bfr = new BufferedReader(fr);
            String line = "";
            while(!((line = bfr.readLine()) == null)){
                if(!line.matches("Dialogue: [0-9:,\\.]+,Dial_CH,.+"))
                    continue;

                // line sample
                // Dialogue: 1,0:14:27.92,0:14:29.11,Dial_CH,,0,0,0,,昨天怎麼樣
                line = line.replaceAll("Dialogue: ","");
                String[] subArr = line.split(",");
                subArr = Arrays.stream(subArr)
                        .map(String::trim) //trim the space in each element // String::trim == str.trim()
                        .toArray(String[]::new);

                Test2Entity.Test2EntityBuilder t2e = Test2Entity.builder();
                t2e.series_name(arr[0])
                        .season(Integer.parseInt(arr[1].substring(1)))
                        .episode(Integer.parseInt(arr[2].substring(1)))
                        .start_Time(Instant.parse("1970-01-01T0"+subArr[1]+"Z")) //TODO padding the hour to 2 digit with 0
                        .end_Time(Instant.parse("1970-01-01T0"+subArr[2]+"Z")) //TODO padding the hour to 2 digit with 0
                        .line(subArr[9])
                        .crated_by("Spring Boot");
                test2Repository.save(t2e.build());
            }

        }catch(IOException e) {
            return new ResponseEntity<>(
                    "Error occurred when importing subtitles into DB.",
                    HttpStatusCode.valueOf(500)
            );
        }finally {
            if(!(bfr==null)){
                try {
                    bfr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.ok().body("Success");
    }
}
