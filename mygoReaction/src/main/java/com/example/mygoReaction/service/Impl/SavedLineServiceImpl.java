package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.G2DSybtitleConfig;
import com.example.mygoReaction.model.dto.SavedLineDto;
import com.example.mygoReaction.model.dto.SearchLineForm;
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

    /**
     * get the list of scene info which specified by users
     * 
     * @param searchLineForm form object for storing the searching parameters
     * @return GenericForm object which store the screen cap URL and other info
     */
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

        // check line presented in findSavedLineResp. if not, save the screenshot as png
        // and write the relative path to saved_line
        boolean isStandardScreenCapEmpty = findSavedLineResp.getScreenCapPath() == null
                || findSavedLineResp.getScreenCapPath().isEmpty();
        boolean isSoapOperaScreenCapEmpty = findSavedLineResp.getSoapOperaScnCapPath() == null
                || findSavedLineResp.getSoapOperaScnCapPath().isEmpty();

        if (style.equalsIgnoreCase(Constant.capScreenStyle.get(0)) && isStandardScreenCapEmpty
                || style.equalsIgnoreCase(Constant.capScreenStyle.get(1)) && isSoapOperaScreenCapEmpty
                || isForce) {
            // if(true){
            File videoFile = null;

            try {
                videoFile = new File(videoFilePath,videoFilename + "." + Constant.extension.MKV);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            try (
                    FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
                    Java2DFrameConverter converter = new Java2DFrameConverter();) {
                Frame frame = getFrame(grabber, findSavedLineResp);

                if (frame == null) {
                    log.error("Error occurred when saving the screen cap.");
                    throw new FrameGrabber.Exception("nNo frame grabbed.");
                }

                // use the same font type to Muse Anime HK
                String fontFamilyName = "SourceHanSansHK-Bold.otf";
                if(style.equalsIgnoreCase(Constant.capScreenStyle.get(1))){
                    fontFamilyName = "TW-Kai-98_1.ttf";
                }
                ClassPathResource classPathResource = new ClassPathResource(fontFamilyName);
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, classPathResource.getFile()).deriveFont(60f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);

                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                Graphics2D g2d = bufferedImage.createGraphics();

                g2d.setFont(customFont);

                String[] lines = findSavedLineResp.getLine().split(System.lineSeparator());
                G2DSubtitleConfig config = new G2DSubtitleConfig(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        5, Color.BLACK, Color.WHITE, 1, 1);

                if(style.equalsIgnoreCase(Constant.capScreenStyle.get(1))){
                    config.setInnerColor(Color.YELLOW);
                    config.setYScale(0.9);
                }

                drawSubtitle(g2d, lines, config);

                // Dispose graphics
                g2d.dispose();

                ImageIO.write(bufferedImage, "png", new File(outputFilePath,outputFilename+"."+ Constant.extension.PNG));
                log.info("Frame extracted and saved as " + outputFilename +"."+ Constant.extension.PNG);

                grabber.stop();
                String sssss = "/static/screen_cap/" + outputFilename +"."+ Constant.extension.PNG;
                String screenCapPath = ServletUriComponentsBuilder.fromCurrentContextPath().path(sssss).toUriString();
                form.setPath(screenCapPath);

                SavedLineEntity.Builder test = findSavedLineResp.toBuilder();
                if (style.equalsIgnoreCase(Constant.capScreenStyle.get(0))){
                    test.screenCapPath(sssss);
                }else {
                    test.soapOperaScnCapPath(sssss);
                }
                savedLineRepository.save(test.build());
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }
        } else {
            // get path in DB directly and return
            String path;
            if(style.equalsIgnoreCase(Constant.capScreenStyle.get(0))) {
                path = findSavedLineResp.getScreenCapPath();
            }else{
                path = findSavedLineResp.getSoapOperaScnCapPath();
            }
            if(! new File(path.replace("/static/",Constant.MYGO_REACTION_ASSET)).exists()){
                return this.getScreenCapFromVideo(savedLineId,style, true);
            }else{
                String screenCapPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(path).toUriString();
                form.setPath(screenCapPath);
            }
        }
        form.setCode(0);
        form.setMessage("success");
        return form;
    }

    public static Frame getFrame(FFmpegFrameGrabber grabber, SavedLineEntity findSavedLineResp) throws FFmpegFrameGrabber.Exception {
        grabber.start();
        // get max timestamp of the video
        long timeLength = grabber.getLengthInTime();

        // get initial timestamp
        Frame frame = grabber.grabImage();
        long startTime = frame.timestamp;

        // int second = 60;
        Instant he = Instant.parse("1970-01-01T00:00:00.000+08:00");
        // Instant startTimestamp = Instant.parse("1970-01-01T00:01:00.123+08:00");
        // long second = he.until(startTimestamp, ChronoUnit.SECONDS);
        long second = he.until(findSavedLineResp.getStartTime(), ChronoUnit.SECONDS);
        long timestamp = startTime + second * 1000000L; // 1 minute and 123 milliseconds

        grabber.setTimestamp(timestamp);
        frame = grabber.grabImage();
        return frame;
    }

    /**
     * Draw subtitle on image
     * 
     * @param g2d    image context in Graphic2D
     * @param lines  subtitle line (split with line separator)
     * @param config config object
     */
    private void drawSubtitle(Graphics2D g2d, String[] lines, G2DSybtitleConfig config) {
        FontMetrics fm = g2d.getFontMetrics();
        for (int lineCount = lines.length - 1; lineCount >= 0; lineCount--) {
            int width = fm.stringWidth(lines[lineCount]);
            // make subtitle line center
            int xPos = ((int) (config.getWidth() / config.getXScale()) - width) / 2;
            // set y coordinate to 40px higher than bottom of the image;
            // if there are multiple lines, set the line by one line upper
            int yPos = (int) (config.getHeight() / config.getYScale()) - fm.getHeight() - fm.getHeight() * lineCount - 40
                    + fm.getAscent();

            AffineTransform at = g2d.getTransform();
            at.setTransform(at);
            at.scale(config.getXScale(), config.getYScale());
            g2d.setTransform(at);

            g2d.setColor(config.getBorderColor()); // todo add to G2DSubtitleConfig object
            // Draw the outline
            for (int x = -config.getOffset(); x <= config.getOffset(); x += 1) {
                for (int y = -config.getOffset(); y <= config.getOffset(); y += 1) {
                    g2d.drawString(lines[lineCount], xPos + x, yPos + y);
                }
            }

            g2d.setColor(config.getInnerColor()); // todo add to G2DSubtitleConfig object
            g2d.drawString(lines[lineCount], xPos, yPos);

        }
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
            subtitleExtractService.insertSubtitleIntoDB(subtitleFile, savedSeriesResp.getSeriesId());
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
