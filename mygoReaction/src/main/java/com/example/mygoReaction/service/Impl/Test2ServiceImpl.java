package com.example.mygoReaction.service.Impl;


import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.Test2Entity;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.repository.Test2Repository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utils.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static utils.FilenameUtils.removeFileExtension;

@Slf4j
@Service
public class Test2ServiceImpl {
    @Autowired
    ResourceLoader resourceLoader;

    SubtitleExtractService subtitleExtractService;

    private final Test2Repository test2Repository;
    private final SavedSeriesRepository savedSeriesRepository;
    public Test2ServiceImpl(
            Test2Repository test2Repository,
            SavedSeriesRepository savedSeriesRepository,
            @Qualifier("SrtSubtitleExtractServiceImpl") SubtitleExtractService subtitleExtractService
    ) {
        this.test2Repository = test2Repository;
        this.savedSeriesRepository = savedSeriesRepository;
        this.subtitleExtractService = subtitleExtractService;
    }

    public List<Test2Entity> findByDateBetween(String startDateStr, String endDateStr){
        Instant startDate = Instant.parse("1970-01-01T"+startDateStr+"+08:00");
        Instant endDate = Instant.parse("1970-01-01T"+endDateStr+"+08:00");
        List<Test2Entity> resp = test2Repository.findByTimeStamp(startDate,endDate);
        return resp;
//        return null;
    }

    public void getScreenCapFromVideoByTimestamp(){
        String path = "./asset/video/SampleVideo_1280x720_30mb.mp4";

        File testVideoFile = null;
        try {
            testVideoFile = new File(path);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }

        try(
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(testVideoFile);
                Java2DFrameConverter converter = new Java2DFrameConverter();
        ){
            grabber.start();
            // get max timestamp of the video
            long timeLength = grabber.getLengthInTime();

            // get initial timestamp
            Frame frame = grabber.grabImage();
            long startTime = frame.timestamp;

//            int second = 60;
            Instant he = Instant.parse("1970-01-01T00:00:00.000+08:00");
            Instant startTimestamp = Instant.parse("1970-01-01T00:01:00.123+08:00");
//            long second = startTimestamp.getEpochSecond();
            long second = he.until(startTimestamp, ChronoUnit.SECONDS);
            long timestamp = startTime + second * 1000000L; // 1 minute and 123 milliseconds

            grabber.setTimestamp(timestamp);
            frame = grabber.grabImage();
            if (frame != null) {
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                String outputFilename = FilenameUtils.getUniqueOutputFilename("out.png");
                ImageIO.write(bufferedImage, "png", new File("./asset/screen_cap/"+ outputFilename));
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
    public ResponseEntity<String> importFromSubtitle(String subtitleFilename){
        String pathFromResource = "./asset/subtitle/";
        File subtitleFile = null;

        try {
            subtitleFile = new File(pathFromResource+subtitleFilename);
        } catch (NullPointerException e) {
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
        try {
            subtitleExtractService.insertSubtitleIntoDB(subtitleFile,savedSeriesResp.getSeriesId());
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Error occurred when importing subtitles into DB.",
                    HttpStatusCode.valueOf(500)
            );
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
