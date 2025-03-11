package com.example.mygoReaction.service.Impl;


import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.form.GenericForm;
import com.example.mygoReaction.model.form.GetSavedLineForm;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.repository.SavedLineRepository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static utils.FilenameUtils.removeFileExtension;

import com.example.mygoReaction.constant.SavedLineConstant;

@Slf4j
@Service
public class SavedLineServiceImpl {
    @Autowired
    ResourceLoader resourceLoader;

    SubtitleExtractService subtitleExtractService;

    private final SavedLineRepository savedLineRepository;
    private final SavedSeriesRepository savedSeriesRepository;

    public SavedLineServiceImpl(
            SavedLineRepository savedLineRepository,
            SavedSeriesRepository savedSeriesRepository,
            @Qualifier("SrtSubtitleExtractServiceImpl") SubtitleExtractService subtitleExtractService
    ) {
        this.savedLineRepository = savedLineRepository;
        this.savedSeriesRepository = savedSeriesRepository;
        this.subtitleExtractService = subtitleExtractService;
    }

    public ResponseEntity<String> findBySeriesIdAndKeyword(Test2Dto t2d) {
        List<SavedLineEntity> resp = savedLineRepository.findBySeriesIdAndLineContaining(t2d.getSeries_id(), t2d.getLine());
        return ResponseEntity.ok().body(resp.stream()
                .map(savedLineEntity -> "Id: " + savedLineEntity.getSavedLineId() + " seriesId: " + savedLineEntity.getSeriesId() + " line: " + savedLineEntity.getLine())
                .toList().toString());
    }


    /*
    get the list of record in saved_line by either line or timestamp
     */
    public GenericForm getSavedLine(SearchLineForm searchLineForm) {
        boolean isSearchByLine = false, isSearchByTimestamp = false;

        if (
                searchLineForm.getSeason() == null
                        || searchLineForm.getSeriesName()==null
                        || searchLineForm.getEpisode() == null
                        || searchLineForm.getSeriesName().isBlank()
        ) {
            log.error("Missing augments. Insufficient info for finding anime series.");
            return new GenericForm(1,"Missing augments. Insufficient info for finding anime series.");
        }

        Optional<SavedSeriesEntity> savedSeriesResp = savedSeriesRepository.findBySeriesNameAndSeasonAndEpisode(
                searchLineForm.getSeriesName(),
                searchLineForm.getSeason(),
                searchLineForm.getEpisode()
        );

        if (savedSeriesResp.isEmpty()) {
            log.error("No Record found.");
            return new GenericForm(1,"No Record found.");
        }

        if (!(searchLineForm.getLine()==null||searchLineForm.getLine().isBlank())) isSearchByLine = true;
        if (!(searchLineForm.getStartTime() == null)) isSearchByTimestamp = true;
        if (!isSearchByLine && !isSearchByTimestamp) {
            log.error("Missing augments. Cannot determine searching criteria since both line and timestamp is empty.");
            return new GenericForm(1,"Missing augments. Cannot determine searching criteria since both line and timestamp is empty.");
        }
        List<SavedLineEntity> savedLineEntityResp = null;
        if (isSearchByLine) {
            savedLineEntityResp = savedLineRepository.findBySeriesIdAndLineContaining(savedSeriesResp.get()
                    .getSeriesId(), searchLineForm.getLine());
        }
        if (isSearchByTimestamp) {
            savedLineEntityResp = savedLineRepository.findBySeriesIdAndTimestamp(savedSeriesResp.get()
                    .getSeriesId(), searchLineForm.getStartTime());
        }
        if (savedLineEntityResp.isEmpty()) {
            log.error("No Record found.");
            return new GenericForm(1,"No Record found.");
        }

        return new GetSavedLineForm(0, "success", savedLineEntityResp);
    }

    private void getScreenCapFromVideo(SearchLineForm t2d) {
//        String videoFilename = "sample.mp4";
        String videoFilename = t2d.getSeriesName() + "-S" + String.format("%02d", t2d.getSeason()) + "-E" + String.format("%02d", t2d.getEpisode()) + ".mkv";

        File videoFile = null;
        try {
            videoFile = new File(SavedLineConstant.assetRootPath + SavedLineConstant.videoFolderName + t2d.getSeriesName() + "/" + videoFilename);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        try (
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
                Java2DFrameConverter converter = new Java2DFrameConverter();
        ) {
            grabber.start();
            // get max timestamp of the video
            long timeLength = grabber.getLengthInTime();

            // get initial timestamp
            Frame frame = grabber.grabImage();
            long startTime = frame.timestamp;

//            int second = 60;
            Instant he = Instant.parse("1970-01-01T00:00:00.000+08:00");
//            Instant startTimestamp = Instant.parse("1970-01-01T00:01:00.123+08:00");
//            long second = he.until(startTimestamp, ChronoUnit.SECONDS);
            long second = he.until(t2d.getStartTime(), ChronoUnit.SECONDS);
            long timestamp = startTime + second * 1000000L; // 1 minute and 123 milliseconds

            grabber.setTimestamp(timestamp);
            frame = grabber.grabImage();
            if (frame != null) {
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                String outputFilename = FilenameUtils.getUniqueOutputFilename("out.png");
//                String outputFilename = FilenameUtils.getUniqueOutputFilename(t2d.getSeries_name() + "-S" + String.format("%02d",t2d.getSeason()) + "-E" + String.format("%02d",t2d.getEpisode())+t2d.getStartTime().toString()+".png");
                ImageIO.write(bufferedImage, "png", new File(SavedLineConstant.resourceRootPath + SavedLineConstant.screenCapOutputFolderName + outputFilename));
                log.info("Frame extracted and saved as " + outputFilename);
            } else {
                log.error("No frame found at the specified timestamp.");
            }
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ResponseEntity<String> importFromSubtitle(String subtitleFilename) {
        File subtitleFile = null;

        try {
            subtitleFile = new File(SavedLineConstant.assetRootPath + SavedLineConstant.subtitleFolderName + subtitleFilename);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(
                    SavedLineConstant.subtitleFileNotFound,
                    HttpStatusCode.valueOf(500)
            );
        }

        // information array from subtitle filename
        // 0: series_name, 1: season, 2: episode
        String[] arr = removeFileExtension(subtitleFilename).split("-");
        if (arr.length < 3) {
            return ResponseEntity
                    .badRequest()
                    .body(SavedLineConstant.inappropriateSubtitleFileFormat);
        }

        // record the series info in the save_series table
        SavedSeriesEntity.Builder sseb = SavedSeriesEntity.builder();
        sseb.seriesName(arr[0])
                .season(Integer.parseInt(arr[1].substring(1)))
                .episode(Integer.parseInt(arr[2].substring(1)))
                .created_by(SavedLineConstant.createdBy);
        SavedSeriesEntity savedSeriesResp = null;
        try {
            savedSeriesResp = savedSeriesRepository.save(sseb.build());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

        // process the subtitle and record in test2 table
        try {
            subtitleExtractService.insertSubtitleIntoDB(subtitleFile, savedSeriesResp.getSeriesId());
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Error occurred when importing subtitles into DB.",
                    HttpStatusCode.valueOf(500)
            );
        }

        return ResponseEntity.ok().body("Success");
    }

//    public ResponseEntity<String> saveImage2DB(String filename){
//        String pathFromResource = "image/";
//        File imageTestFile = null;
//        BufferedReader bfr = null;
//
//        try {
//            imageTestFile = resourceLoader
//                    .getResource("classpath:"+pathFromResource+filename)
//                    .getFile();
//            byte[] content = null;
//            try {
//                content = Files.readAllBytes(imageTestFile.toPath());
//            } catch (final IOException e) {
//            }
//            MultipartFile result = new MockMultipartFile(filename,
//                    filename, "image/jpeg", content);
//
//            Test2Entity record = test2Repository.findById(1).get();
//            test2Repository.save(
//                    record.toBuilder()
//                            .screen_cap_thumbnail(result.getBytes())
//                            .screen_cap_path(imageTestFile.toPath().toString())
//                            .updated_by("update spring")
//                            .build()
//            );
//
//        } catch (IOException e) {
//            return new ResponseEntity<>(
//                    "Specified subtitle file not found",
//                    HttpStatusCode.valueOf(500)
//            );
//        }
//        return ResponseEntity.ok("Success");
//    }
}
