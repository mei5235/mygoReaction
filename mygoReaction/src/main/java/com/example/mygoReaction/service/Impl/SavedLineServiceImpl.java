package com.example.mygoReaction.service.Impl;


import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.form.GenericForm;
import com.example.mygoReaction.model.form.GetSavedLineForm;
import com.example.mygoReaction.model.form.HeheForm;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.repository.SavedLineRepository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_imgproc.CvFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import utils.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static utils.FilenameUtils.removeFileExtension;

import com.example.mygoReaction.constant.Constant;

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

    /**
     * List all saved_line record matching the searching criteria
     * @param t2d data form object for searching the line
     * @return response form object with the records which matching the criteria
     */
    public GenericForm findByKeyword(Test2Dto t2d) {
        List<SavedLineEntity> resp = savedLineRepository.findByLineContaining(t2d.getSeries_id(), t2d.getLine());
        return new GetSavedLineForm(0, "success", resp);
    }


    /**
     * get the list of scene info which specified by users
     * @param searchLineForm form object for storing the searching parameters
     * @return GenericForm object which store the screen cap URL and other info
     */
    public GenericForm getSavedLines(SearchLineForm searchLineForm) {
        boolean isSearchByLine = false, isSearchByTimestamp = false;

        if (!(searchLineForm.getLine()==null||searchLineForm.getLine().isBlank())) {
            isSearchByLine = true;
        }
        if (!(searchLineForm.getStartTime() == null)) {
            isSearchByTimestamp = true;
        }
        if (!isSearchByLine && !isSearchByTimestamp) {
            log.error(Constant.INSUFFICIENTAUGMENTDETERMINDSEARCHMETHOD);
            return new GenericForm(1,Constant.INSUFFICIENTAUGMENTDETERMINDSEARCHMETHOD);
        }

        Optional<SavedSeriesEntity> savedSeriesResp = savedSeriesRepository.findBySeriesNameAndSeasonAndEpisode(
                searchLineForm.getSeriesName(),
                searchLineForm.getSeason(),
                searchLineForm.getEpisode()
        );

        List<SavedLineEntity> savedLineEntityResp = null;
        if (isSearchByLine) {
            Integer id = savedSeriesResp.map(SavedSeriesEntity::getSeriesId).orElse(null);
            savedLineEntityResp = savedLineRepository.findByLineContaining(id,searchLineForm.getLine());
        }
        if (isSearchByTimestamp) {
            if (
                    searchLineForm.getSeason() == null
                            || searchLineForm.getSeriesName()==null
                            || searchLineForm.getEpisode() == null
                            || searchLineForm.getSeriesName().isBlank()
            ) {
                log.error(Constant.MISSINGSERIESINFO);
                return new GenericForm(1,Constant.MISSINGSERIESINFO);
            }

            if (savedSeriesResp.isEmpty()) {
                log.error(Constant.NORECORD);
                return new GenericForm(1,Constant.NORECORD);
            }

            savedLineEntityResp = savedLineRepository.findBySeriesIdAndTimestamp(savedSeriesResp.get()
                    .getSeriesId(), searchLineForm.getStartTime());
        }
        if (savedLineEntityResp.isEmpty()) {
            log.error(Constant.NORECORD);
            return new GenericForm(1,Constant.NORECORD);
        }

        return new GetSavedLineForm(0, Constant.SUCCESS, savedLineEntityResp);
    }

    public GenericForm getScreenCapFromVideo(Integer savedLineid) {
        HeheForm form = new HeheForm();
        SavedLineEntity findSavedLineResp = savedLineRepository.findBySavedLineId(savedLineid).orElse(null);

        if (findSavedLineResp == null) {
            log.error(Constant.NORECORD);
            return new GenericForm(1,Constant.NORECORD);
        }

        SavedSeriesEntity findSavedSeriesResp = savedSeriesRepository.findBySeriesId(findSavedLineResp.getSeriesId()).orElse(null);

        String videoFilename = findSavedSeriesResp.getSeriesName() + "-S" + String.format("%02d", findSavedSeriesResp.getSeason()) + "-E" + String.format("%02d", findSavedSeriesResp.getEpisode()) ;
        String videoFilePath = Constant.RESOURCEROOTPATH + Constant.VIDEOFOLDERNAME + findSavedSeriesResp.getSeriesName() + "/" + videoFilename + "." + Constant.extension.MKV;
        String outputFilename = FilenameUtils.getUniqueOutputFilename(videoFilename + "." + Constant.extension.PNG);
        String outputFilePath = Constant.RESOURCEROOTPATH + Constant.SCREENCAPOUTPUTFOLDERNAME + outputFilename;

        // check line presented in findSavedLineResp. if not, save the screenshot as png and write the relative path to saved_line
//        if(findSavedLineResp.getScreenCapPath() == null ||findSavedLineResp.getScreenCapPath().isEmpty()){
        if(true){
            File videoFile = null;

            try {
                videoFile = new File(videoFilePath);
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
                long second = he.until(findSavedLineResp.getStartTime(), ChronoUnit.SECONDS);
                long timestamp = startTime + second * 1000000L; // 1 minute and 123 milliseconds

                grabber.setTimestamp(timestamp);
                frame = grabber.grabImage();

//                String outputFilename = "";

                if (frame != null) {
                    BufferedImage bufferedImage = converter.getBufferedImage(frame);
                    Graphics2D g2d = bufferedImage.createGraphics();

                    // Set font
                    Font font = new Font("Source Han Sans HK", Font.BOLD, 50);
                    g2d.setFont(font);

                    String[] lines = findSavedLineResp.getLine().split(System.lineSeparator());
//                    String[] lines = {"hehe","hehe","hehe"};

                    FontMetrics fm = g2d.getFontMetrics();
                    for (int stkaskml = lines.length-1; stkaskml >= 0; stkaskml--) {
                        int width = fm.stringWidth(lines[stkaskml]);

                        int xPos = (bufferedImage.getWidth() - width) / 2;
                        int yPos = bufferedImage.getHeight() - fm.getHeight() -fm.getHeight()*stkaskml - 20 + fm.getAscent();

                        int x_offset = 5;
                        int y_offset = 5;

                        // Draw the outline
                        g2d.setColor(Color.BLACK);
                        for (int x = -x_offset; x <= x_offset; x += x_offset) {
                            for (int y = -y_offset; y <= y_offset; y += y_offset) {
                                g2d.drawString(lines[stkaskml], xPos + x, yPos + y);
                            }
                        }

                        // Draw the filled text
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(lines[stkaskml], xPos, yPos);

                    }

                    // Dispose graphics
                    g2d.dispose();


                    ImageIO.write(bufferedImage, "png", new File(outputFilePath));
                    log.info("Frame extracted and saved as " + outputFilename);
                } else {
                    log.error("Error occurred when saving the screen cap.");
                }
                grabber.stop();
                String screenCapPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/static/screen_cap/")
                        .path(outputFilename).toUriString();
                form.setPath(screenCapPath);

                SavedLineEntity.Builder test = findSavedLineResp.toBuilder().screenCapPath(screenCapPath);
                savedLineRepository.save(test.build());
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            // get path in DB directly and return
            form.setPath(findSavedLineResp.getScreenCapPath());
        }
        form.setCode(0);
        form.setMessage("success");
        return form;
    }

    @Transactional
    public ResponseEntity<String> importFromSubtitle(String subtitleFilename) {
        File subtitleFile = null;

        try {
            subtitleFile = new File(Constant.RESOURCEROOTPATH + Constant.SUBTITLEFOLDERNAME + subtitleFilename);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(
                    Constant.SUBTITLEFILENOTFOUND,
                    HttpStatusCode.valueOf(500)
            );
        }

        // information array from subtitle filename
        // 0: series_name, 1: season, 2: episode
        String[] arr = removeFileExtension(subtitleFilename).split("-");
        if (arr.length < 3) {
            return ResponseEntity
                    .badRequest()
                    .body(Constant.INAPPROPRIATESUBTITLEFILEFORMAT);
        }

        // record the series info in the save_series table
        SavedSeriesEntity.Builder sseb = SavedSeriesEntity.builder();
        sseb.seriesName(arr[0])
                .season(Integer.parseInt(arr[1].substring(1)))
                .episode(Integer.parseInt(arr[2].substring(1)))
                .created_by(Constant.CREATEDBY);
        SavedSeriesEntity savedSeriesResp = null;

        savedSeriesResp = savedSeriesRepository.save(sseb.build());

        // process the subtitle and record in test2 table
        try {
            subtitleExtractService.insertSubtitleIntoDB(subtitleFile, savedSeriesResp.getSeriesId());
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Error occurred when importing subtitles into DB.",
                    HttpStatusCode.valueOf(500)
            );
        }

        return ResponseEntity.ok().body("Success"); //fixme remove response body in service
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
