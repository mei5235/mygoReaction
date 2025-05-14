package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.constant.Constant;
import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.model.dto.Test2Dto;
import com.example.mygoReaction.model.resp.GenericResp;
import com.example.mygoReaction.repository.SavedLineRepository;
import com.example.mygoReaction.service.SubtitleExtractService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

import static utils.CvUtils.*;

/**
 * Class for extracting line in the .srt subtitle file into DB records
 */
@Slf4j
@Service
@Qualifier("SrtSubtitleExtractServiceImpl")
public class SrtSubtitleExtractServiceImpl implements SubtitleExtractService {
    private final SavedLineRepository savedLineRepository;
    public SrtSubtitleExtractServiceImpl(
            SavedLineRepository savedLineRepository
    ) {
        this.savedLineRepository = savedLineRepository;
    }



    @Override
    public boolean insertSubtitleIntoDB(File subtitleFile, SavedSeriesEntity series) throws IOException {
//            Line Sample

//            344
//            00:20:56,129 --> 00:20:58,340
//            為什麼要演奏春日影!
        try (BufferedReader bfr = new BufferedReader(new FileReader(subtitleFile))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            SavedLineEntity.Builder builder = null;

            String videoFilename = series.getSeriesName() + "-S"
                    + String.format("%02d", series.getSeason()) + "-E"
                    + String.format("%02d", series.getEpisode());
            String videoFilePath = Constant.MYGO_REACTION_ASSET + Constant.VIDEO_FOLDERNAME
                    + series.getSeriesName();
            File videoFile;
            try {
                videoFile = new File(videoFilePath, videoFilename + "." + Constant.extension.MKV);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
                ) {
                Test2Dto dto;
                while ((line = bfr.readLine()) != null) {
                    line = line.replace("\uFEFF", "");
                    dto = new Test2Dto();
                    if (isLineNumber(line)) {
                        builder = initializeBuilder(series.getSeriesId());
                        contentBuilder.setLength(0);
                    } else if (isTimestamp(line)) {
                        processTimestamp(builder, line);
                    } else if (!line.trim().isEmpty()) {
                        appendContent(contentBuilder, line);
                    }else{
                        dto.setStartTime(builder.build().getStartTime());
                        dto.setEndTime(builder.build().getEndTime());
                        dto.setLine(builder.build().getLine());
                        builder.thumbnail(getFrameHehe(grabber, dto));
                        saveCurrentSubtitle(builder, contentBuilder);
                    }
                }

                // Save the last subtitle
//                saveCurrentSubtitle(builder, contentBuilder);

                log.info("Successfully imported subtitles");
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to import subtitles: {}", e.getMessage(), e);
            return false;
        }
    }

    private byte[] getFrameHehe(FFmpegFrameGrabber grabber, Test2Dto dto) throws IOException {
        try (Java2DFrameConverter converter = new Java2DFrameConverter();
             ByteArrayOutputStream bStream = new ByteArrayOutputStream()
        ) {
            Frame frame = getFrame(grabber, dto);
            BufferedImage image = converter.getBufferedImage(frame);

            Thumbnails.Builder<BufferedImage> resized = Thumbnails.of(image);
            resized.height(360);
            ImageIO.write(resized.asBufferedImage(), "png", bStream);
            grabber.stop();
            return bStream.toByteArray();
        } catch (FFmpegFrameGrabber.Exception e) {
            log.error("Error processing video: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isLineNumber(String line) {
        return line.matches("^\\uFEFF?\\d+$");
    }

    private boolean isTimestamp(String line) {
        return line.matches("\\d{2}:\\d{2}:\\d{2},\\d{3}\\s-->\\s\\d{2}:\\d{2}:\\d{2},\\d{3}");
    }

    private SavedLineEntity.Builder initializeBuilder(Integer seriesId) {
        return SavedLineEntity.builder()
                .seriesId(seriesId)
                .created_by(Constant.CREATEDBY)
                .updated_by(Constant.CREATEDBY);
    }

    private void processTimestamp(SavedLineEntity.Builder builder, String timestamp) {
        if (builder == null) return;

        String[] timeArr = timestamp.split("-->", 2);
        String[] processedTimes = Arrays.stream(timeArr)
                .map(String::trim)
                .map(time -> time.replace(',', '.'))
                .toArray(String[]::new);

        builder.startTime(parseTime(processedTimes[0]))
                .endTime(parseTime(processedTimes[1]));
    }

    private Instant parseTime(String time) {
        return Instant.parse("1970-01-01T" + time + "+08:00");
    }

    private void appendContent(StringBuilder builder, String line) {
        if (builder.length() > 0) {
            builder.append(System.lineSeparator());
        }
        builder.append(line);
    }

    private void saveCurrentSubtitle(SavedLineEntity.Builder builder, StringBuilder content) {
        if (builder != null && content.length() > 0) {
            builder.line(content.toString());
            savedLineRepository.save(builder.build());
        }
    }
}
