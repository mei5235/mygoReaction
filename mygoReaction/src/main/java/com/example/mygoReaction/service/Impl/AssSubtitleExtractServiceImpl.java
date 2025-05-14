package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.constant.Constant;
import com.example.mygoReaction.entity.SavedSeriesEntity;
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
    public boolean insertSubtitleIntoDB(File subtitleFile, SavedSeriesEntity series) throws IOException {
        try(BufferedReader bfr = new BufferedReader(new FileReader(subtitleFile));){
            SavedLineEntity.Builder t2e = null;
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
                t2e = initializeBuilder(series.getSeriesId());
                t2e.startTime(parseTime(subArr[1]))
                        .endTime(parseTime(subArr[2]))
                        .line(subArr[9]);

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

    private SavedLineEntity.Builder initializeBuilder(Integer seriesId) {
        return SavedLineEntity.builder()
                .seriesId(seriesId)
                .created_by(Constant.CREATEDBY)
                .updated_by(Constant.CREATEDBY);
    }

    private Instant parseTime(String time) {
        String[] parts = time.split(":");
        String hours = String.format("%02d", Integer.parseInt(parts[0]));
        String minutes = String.format("%02d", Integer.parseInt(parts[1]));
        String secondsAndMillis = parts[2];
        return Instant.parse("1970-01-01T"+ hours + ":" + minutes + ":" + secondsAndMillis +"+08:00");
    }
}
