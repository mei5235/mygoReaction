package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.G2DSubtitleConfig;
import com.example.mygoReaction.model.dto.SavedLineDto;
import com.example.mygoReaction.model.dto.SearchLineForm;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.resp.GenericResp;
import com.example.mygoReaction.model.resp.GetSavedLineResp;
import com.example.mygoReaction.model.resp.GetScreenCapFromVideoResp;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import com.example.mygoReaction.repository.SavedLineRepository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import utils.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.CvUtils.*;
import static utils.FilenameUtils.removeFileExtension;

import com.example.mygoReaction.constant.Constant;

@Slf4j
@Service
public class SavedLineServiceImpl {
    private final Map<String, Font> fontCache = new ConcurrentHashMap<>();

    SubtitleExtractService subtitleExtractService;
    private final SavedLineRepository savedLineRepository;
    private final SavedSeriesRepository savedSeriesRepository;

    public SavedLineServiceImpl(
            SavedLineRepository savedLineRepository,
            SavedSeriesRepository savedSeriesRepository,
            @Qualifier("SrtSubtitleExtractServiceImpl") SubtitleExtractService subtitleExtractService) {
        this.savedLineRepository = savedLineRepository;
        this.savedSeriesRepository = savedSeriesRepository;
        this.subtitleExtractService = subtitleExtractService;
    }

    public GenericResp getSavedLines(SearchLineForm searchLineForm) {
        List<SavedLineDto> savedLineEntityResp = null;
        savedLineEntityResp = savedLineRepository.findByLineContaining(searchLineForm.getSeriesId(), searchLineForm.getLine());

        if (savedLineEntityResp.isEmpty()) {
            log.error(Constant.NO＿RECORD);
            return new GenericResp(1, Constant.NO＿RECORD);
        }

        return new GetSavedLineResp(0, Constant.SUCCESS, savedLineEntityResp);
    }

    public GenericResp getScreenCapFromVideo(Integer savedLineId, String style, boolean isForce) {
        GetScreenCapFromVideoResp form = new GetScreenCapFromVideoResp();

        if (Constant.capScreenStyle.stream().noneMatch(style::equalsIgnoreCase)){
            log.error(Constant.UNKNOW_STYLE);
            return new GenericResp(1, Constant.UNKNOW_STYLE);
        }

        SavedLineEntity findSavedLineResp = savedLineRepository.findBySavedLineId(savedLineId).orElse(null);

        if (findSavedLineResp == null) {
            log.error(Constant.NO＿RECORD);
            return new GenericResp(1, Constant.NO＿RECORD);
        }

        SavedSeriesEntity findSavedSeriesResp = savedSeriesRepository.findBySeriesId(findSavedLineResp.getSeriesId())
                .orElse(null);

        String videoFilename = findSavedSeriesResp.getSeriesName() + "-S"
                + String.format("%02d", findSavedSeriesResp.getSeason()) + "-E"
                + String.format("%02d", findSavedSeriesResp.getEpisode());
        String videoFilePath = Constant.MYGO_REACTION_ASSET + Constant.VIDEO_FOLDERNAME
                + findSavedSeriesResp.getSeriesName();
        String outputFilename = FilenameUtils.getUniqueOutputFilename(videoFilename);
        String outputFilePath = Constant.MYGO_REACTION_ASSET + Constant.SCREEN_CAP;

        boolean isStandardScreenCapEmpty = findSavedLineResp.getScreenCapPath() == null
                || findSavedLineResp.getScreenCapPath().isEmpty();
        boolean isSoapOperaScreenCapEmpty = findSavedLineResp.getSoapOperaScnCapPath() == null
                || findSavedLineResp.getSoapOperaScnCapPath().isEmpty();

        if (style.equalsIgnoreCase(Constant.capScreenStyle.get(0)) && isStandardScreenCapEmpty
                || style.equalsIgnoreCase(Constant.capScreenStyle.get(1)) && isSoapOperaScreenCapEmpty
                || isForce) {
            File videoFile = null;

            try {
                videoFile = new File(videoFilePath, videoFilename + "." + Constant.extension.MKV);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
                 Java2DFrameConverter converter = new Java2DFrameConverter()) {

                Test2Dto dto = new Test2Dto();
                dto.setSeriesId(findSavedLineResp.getSeriesId());
                dto.setStartTime(findSavedLineResp.getStartTime());
                dto.setEndTime(findSavedLineResp.getEndTime());
                Frame frame = getFrame(grabber, dto);

                if (frame == null) {
                    log.error("Error occurred when saving the screen cap.");
                    throw new FrameGrabber.Exception("No frame grabbed.");
                }

                String fontFamilyName = style.equalsIgnoreCase(Constant.capScreenStyle.get(1))
                        ? "TW-Kai-98_1.ttf"
                        : "SourceHanSansHK-Bold.otf";

                BufferedImage bufferedImage = converter.getBufferedImage(frame);

                Graphics2D g2d = bufferedImage.createGraphics();
                // Enable anti-aliasing for better text quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String[] lines = findSavedLineResp.getLine().split(System.lineSeparator());
                G2DSubtitleConfig config = new G2DSubtitleConfig(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        5, Color.BLACK, Color.WHITE, 1, 1);

                if(style.equalsIgnoreCase(Constant.capScreenStyle.get(1))){
                    config.setInnerColor(Color.YELLOW);
                    config.setYScale(0.9);
                }

                drawSubtitle(g2d, lines, config, fontFamilyName);
                g2d.dispose();


                ImageIO.write(bufferedImage, "png", new File(outputFilePath, outputFilename + "." + Constant.extension.PNG));
                log.info("Frame extracted and saved as " + outputFilename + "." + Constant.extension.PNG);

                grabber.stop();
                String sssss = "/static/screen_cap/" + outputFilename + "." + Constant.extension.PNG;
                String screenCapPath = ServletUriComponentsBuilder.fromCurrentContextPath().path(sssss).toUriString();
                form.setPath(screenCapPath);

                SavedLineEntity.Builder test = findSavedLineResp.toBuilder();
                if (style.equalsIgnoreCase(Constant.capScreenStyle.get(0))){
                    test.screenCapPath(sssss);
                }else {
                    test.soapOperaScnCapPath(sssss);
                }
                savedLineRepository.save(test.build());
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("Error processing video: {}", e.getMessage());
                return new GenericResp(1, "Error processing video: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error: {}", e.getMessage());
                return new GenericResp(1, "Unexpected error: " + e.getMessage());
            }
        } else {
            String path = style.equalsIgnoreCase(Constant.capScreenStyle.get(0))
                    ? findSavedLineResp.getScreenCapPath()
                    : findSavedLineResp.getSoapOperaScnCapPath();

            if(!new File(path.replace("/static/", Constant.MYGO_REACTION_ASSET)).exists()){
                return this.getScreenCapFromVideo(savedLineId, style, true);
            } else {
                String screenCapPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(path).toUriString();
                form.setPath(screenCapPath);
            }
        }
        form.setCode(0);
        form.setMessage("success");
        return form;
    }

    @Transactional
    public ResponseEntity<String> importFromSubtitle(String subtitleFilename) {
        File subtitleFile = null;

        try {
            subtitleFile = new File(Constant.MYGO_REACTION_ASSET + Constant.SUBTITLE＿FOLDERNAME + subtitleFilename);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(
                    Constant.SUBTITLEFILENOTFOUND,
                    HttpStatusCode.valueOf(500));
        }

        // information array from subtitle filename
        // 0: series_name, 1: season, 2: episode
        String[] arr = removeFileExtension(subtitleFilename).split("-");
        if (arr.length < 3) {
            return ResponseEntity
                    .badRequest()
                    .body(Constant.INAPPROPRIATE_SUBTITLE_FILE_FORMAT);
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
            subtitleExtractService.insertSubtitleIntoDB(subtitleFile, savedSeriesResp);
        } catch (IOException e) {
            return new ResponseEntity<>(
                    "Error occurred when importing subtitles into DB.",
                    HttpStatusCode.valueOf(500));
        }

        return ResponseEntity.ok().body("Success"); // fixme remove response body in service
    }

    // public ResponseEntity<String> saveImage2DB(String filename){
    // String pathFromResource = "image/";
    // File imageTestFile = null;
    // BufferedReader bfr = null;
    //
    // try {
    // imageTestFile = resourceLoader
    // .getResource("classpath:"+pathFromResource+filename)
    // .getFile();
    // byte[] content = null;
    // try {
    // content = Files.readAllBytes(imageTestFile.toPath());
    // } catch (final IOException e) {
    // }
    // MultipartFile result = new MockMultipartFile(filename,
    // filename, "image/jpeg", content);
    //
    // Test2Entity record = test2Repository.findById(1).get();
    // test2Repository.save(
    // record.toBuilder()
    // .screen_cap_thumbnail(result.getBytes())
    // .screen_cap_path(imageTestFile.toPath().toString())
    // .updated_by("update spring")
    // .build()
    // );
    //
    // } catch (IOException e) {
    // return new ResponseEntity<>(
    // "Specified subtitle file not found",
    // HttpStatusCode.valueOf(500)
    // );
    // }
    // return ResponseEntity.ok("Success");
    // }
}
