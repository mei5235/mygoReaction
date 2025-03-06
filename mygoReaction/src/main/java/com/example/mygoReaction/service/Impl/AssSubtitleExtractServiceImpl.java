package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.repository.Test2Repository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Service
@Qualifier("AssSubtitleExtractServiceImpl")
public class AssSubtitleExtractServiceImpl implements SubtitleExtractService {
    private final Test2Repository test2Repository;
    public AssSubtitleExtractServiceImpl(
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
                t2e.seriesId(seriesId)
                        .startTime(Instant.parse("1970-01-01T0"+subArr[1]+"+08:00")) //TODO padding the hour to 2 digit with 0
                        .endTime(Instant.parse("1970-01-01T0"+subArr[2]+"+08:00")) //TODO padding the hour to 2 digit with 0
                        .line(subArr[9])
                        .created_by("Spring Boot")
                        .updated_by("Spring Boot");
                test2Repository.save(t2e.build());
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
