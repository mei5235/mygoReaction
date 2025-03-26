package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.constant.Constant;
import com.example.mygoReaction.repository.SavedLineRepository;
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

/**
 * Class for extracting line in the .ass subtitle file into DB records
 */
@Slf4j
@Service
@Qualifier("AssSubtitleExtractServiceImpl")
public class AssSubtitleExtractServiceImpl implements SubtitleExtractService {
    private final SavedLineRepository savedLineRepository;
    public AssSubtitleExtractServiceImpl(
            SavedLineRepository savedLineRepository
    ) {
        this.savedLineRepository = savedLineRepository;
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

                SavedLineEntity.Builder t2e = SavedLineEntity.builder();
                t2e.seriesId(seriesId)
                        .startTime(Instant.parse("1970-01-01T"+String.format("%02d",Integer.parseInt(subArr[1]))+"+08:00"))
                        .endTime(Instant.parse("1970-01-01T"+String.format("%02d",Integer.parseInt(subArr[2]))+"+08:00"))
                        .line(subArr[9])
                        .created_by(Constant.CREATEDBY)
                        .updated_by(Constant.CREATEDBY);
                savedLineRepository.save(t2e.build());
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
