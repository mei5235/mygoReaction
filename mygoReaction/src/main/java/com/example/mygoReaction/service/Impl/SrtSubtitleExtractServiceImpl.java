package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.constant.Test2Constant;
import com.example.mygoReaction.repository.Test2Repository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Service
@Qualifier("SrtSubtitleExtractServiceImpl")
public class SrtSubtitleExtractServiceImpl implements SubtitleExtractService {
    private final Test2Repository test2Repository;
    public SrtSubtitleExtractServiceImpl(
            Test2Repository test2Repository
    ) {
        this.test2Repository = test2Repository;
    }



    @Override
    public Boolean insertSubtitleIntoDB(File subtitleFile, Integer seriesId) throws IOException {
        try(
                FileReader fr = new FileReader(subtitleFile);
                BufferedReader bfr = new BufferedReader(fr);
        ){
//            Line Sample

//            344
//            00:20:56,129 --> 00:20:58,340
//            為什麼要演奏春日影!
//
                    String[] patterns = {
                    "\uFEFF{0,1}\\d+",
//                    "\\d+",
                    "\\d{2}:\\d{2}:\\d{2},\\d{3}\\s-->\\s\\d{2}:\\d{2}:\\d{2},\\d{3}",
                    ""
            };

            String line = "";
            StringBuilder temp = new StringBuilder();
            Test2Entity.Builder t2e = null;
            while(!((line = bfr.readLine()) == null)){
                // remove illegal character "\uFEFF" if present
                if(line.startsWith("\uFEFF"))
                    line.replaceAll("\uFEFF","");

                // mapping the info to the entity object
                if (line.matches(patterns[0])) {
                    t2e = Test2Entity.builder().seriesId(seriesId);
                    continue;
                }
                if (line.matches(patterns[1])) {
                    String[] timeArr = line.split("-->");
                    timeArr = Arrays.stream(timeArr)
                            .map(String::trim) //trim the space in each element // String::trim == str.trim()
                            .map(line1->line1.replaceAll(",","."))
                            .toArray(String[]::new);
                    t2e.startTime(Instant.parse("1970-01-01T" + timeArr[0] + "+08:00"))
                            .endTime(Instant.parse("1970-01-01T" + timeArr[1] + "+08:00"));
                    continue;
                }
                if(!(line.matches(patterns[2]))){
                    temp.append(" ").append(line);
                }else {
                    t2e.line(temp.toString())
                            .created_by(Test2Constant.createdBy)
                            .updated_by(Test2Constant.createdBy);
                    temp = new StringBuilder();
                    test2Repository.save(t2e.build());
                    t2e = null;
                }
            }
            log.info("done import");
        }catch(IOException e) {
            log.error("Cannot read the content of the given file");
            log.error(e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("unexpected error");
            log.error(e.getMessage(),e);
            throw e;
        }
        return true;
    }
}
