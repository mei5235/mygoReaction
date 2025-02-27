package com.example.mygoReaction.service;


import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.repository.Test2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static utils.FilenameUtils.removeFileExtension;

@Slf4j
@Service
public class Test2Service {
    @Autowired
    ResourceLoader resourceLoader;

    private final Test2Repository test2Repository;
    private final SavedSeriesRepository savedSeriesRepository;
    public Test2Service(
            Test2Repository test2Repository,
            SavedSeriesRepository savedSeriesRepository
    ) {
        this.test2Repository = test2Repository;
        this.savedSeriesRepository = savedSeriesRepository;
    }

    public List<Test2Entity> he(){
        List<Test2Entity> resp = test2Repository.findAll();
        return resp;
    }

    public List<Test2Entity> hehe(String startDateStr, String endDateStr){
        Instant startDate = Instant.parse("1970-01-01T"+startDateStr+"+08:00");
        Instant endDate = Instant.parse("1970-01-01T"+endDateStr+"+08:00");
        List<Test2Entity> resp = test2Repository.findByTimeStamp(startDate,endDate);
        return resp;
//        return null;
    }

    public ResponseEntity<String> hehehe(String subtitleFilename){
        String pathFromResource = "my_go_subtitle/";
        File subtitleFile = null;
        BufferedReader bfr = null;

        try {
            subtitleFile = resourceLoader
                    .getResource("classpath:"+pathFromResource+subtitleFilename)
                    .getFile();
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Specified subtitle file not found",
                    HttpStatusCode.valueOf(500)
            );
        }

        // information array from subtitle filename
        // 0: series_name, 1: season, 2: episode
        String[] arr = removeFileExtension(subtitleFilename).split("-");
        if(arr.length<3) {
            return ResponseEntity
                    .badRequest()
                    .body("Inappropriate subtitle filename.");
        }

        // record the series info in the save_series table
        SavedSeriesEntity.Builder sseb = SavedSeriesEntity.builder();
        sseb.series_name(arr[0])
                .season(Integer.parseInt(arr[1].substring(1)))
                .episode(Integer.parseInt(arr[2].substring(1)))
                .created_by("Spring Boot");
        SavedSeriesEntity savedSeriesResp = null;
        try {
            savedSeriesResp = savedSeriesRepository.save(sseb.build());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

        // process the subtitle and record in test2 table
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

                Test2Entity.Builder t2e = Test2Entity.builder();
                t2e.seriesId(savedSeriesResp.getSeriesId())
                        .startTime(Instant.parse("1970-01-01T0"+subArr[1]+"+08:00")) //TODO padding the hour to 2 digit with 0
                        .endTime(Instant.parse("1970-01-01T0"+subArr[2]+"+08:00")) //TODO padding the hour to 2 digit with 0
                        .line(subArr[9])
                        .created_by("Spring Boot")
                        .updated_by("Spring Boot");
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

    public ResponseEntity<String> hehehehe(String filename){
        String pathFromResource = "image/";
        File imageTestFile = null;
        BufferedReader bfr = null;

        try {
            imageTestFile = resourceLoader
                    .getResource("classpath:"+pathFromResource+filename)
                    .getFile();
            byte[] content = null;
            try {
                content = Files.readAllBytes(imageTestFile.toPath());
            } catch (final IOException e) {
            }
            MultipartFile result = new MockMultipartFile(filename,
                    filename, "image/jpeg", content);

            Test2Entity record = test2Repository.findById(1).get();
            test2Repository.save(
                    record.toBuilder()
                            .screen_cap_thumbnail(result.getBytes())
                            .screen_cap_path(imageTestFile.toPath().toString())
                            .updated_by("update spring")
                            .build()
            );

        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Specified subtitle file not found",
                    HttpStatusCode.valueOf(500)
            );
        }
        return ResponseEntity.ok("Success");
    }
}
