package utils;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.G2DSubtitleConfig;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CvUtils {
    private static final Map<String, Font> fontCache = new ConcurrentHashMap<>();

    public static Frame getFrame(FFmpegFrameGrabber grabber, SavedLineEntity findSavedLineResp) throws FFmpegFrameGrabber.Exception {
        if (findSavedLineResp == null || findSavedLineResp.getStartTime() == null) {
            throw new IllegalArgumentException("SavedLineEntity or StartTime cannot be null");
        }

        try {
            grabber.start();

            // Get video metadata
            double frameRate = grabber.getVideoFrameRate();
            long durationInMicros = grabber.getLengthInTime();

            if (frameRate <= 0 || durationInMicros <= 0) {
                throw new FFmpegFrameGrabber.Exception("Invalid video metadata: frameRate=" + frameRate + ", duration=" + durationInMicros);
            }

            // Calculate target timestamp in microseconds
            Instant referenceTime = Instant.parse("1970-01-01T00:00:00.000+08:00");
            long targetMicros = referenceTime.until(findSavedLineResp.getStartTime(), ChronoUnit.MICROS);
//        long timestamp = startTime + second * 1000000L; // 1 minute and 123 milliseconds

            // Validate target time is within video duration
            if (targetMicros > durationInMicros) {
                throw new FFmpegFrameGrabber.Exception(
                        String.format("Target time %d microseconds exceeds video duration %d microseconds",
                                targetMicros, durationInMicros));
            }

            // Seek to approximate position
            grabber.setTimestamp(targetMicros);
            // Grab the frame
            Frame frame = grabber.grabImage();

            // Optional: Fine-tune frame selection if needed
            long actualTimestamp = frame.timestamp;
            long tolerance = 500_000L; // 500ms tolerance

            // If we're too far from target time, try to get closer
            if (Math.abs(actualTimestamp - targetMicros) > tolerance) {
                log.warn("Frame timestamp deviation: {}ms", (actualTimestamp - targetMicros) / 1000);

                // Try to get closer to target time
                for (int attempts = 0; attempts < 5; attempts++) {
                    Frame nextFrame = grabber.grabImage();
                    if (nextFrame == null) break;

                    if (Math.abs(nextFrame.timestamp - targetMicros) < Math.abs(actualTimestamp - targetMicros)) {
                        frame = nextFrame;
                        actualTimestamp = frame.timestamp;
                    } else {
                        break; // We're getting further away, stop here
                    }
                }
            }

            log.info("Frame grabbed at timestamp: {}ms (target: {}ms)",
                    actualTimestamp / 1000, targetMicros / 1000);

            return frame;
        } catch (FFmpegFrameGrabber.Exception e) {
            log.error("Error grabbing frame: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while grabbing frame: {}", e.getMessage());
            throw new FFmpegFrameGrabber.Exception("Failed to grab frame: " + e.getMessage());
        }
    }

    public static void drawSubtitle(Graphics2D g2d, String[] lines, G2DSubtitleConfig config, String fontFamilyName) {
        try {
            // Calculate appropriate font size based on image dimensions
            float fontSize = Math.min(config.getHeight(), config.getWidth()) * 0.055f;
            Font font = getFont(fontFamilyName, fontSize);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();

            // Save original transform
            AffineTransform originalTransform = g2d.getTransform();

            try {
                for (int lineCount = lines.length - 1; lineCount >= 0; lineCount--) {
                    // Reset transform for each line
                    g2d.setTransform(originalTransform);

                    String line = lines[lineCount];
                    int width = fm.stringWidth(line);

                    // Calculate positions in original coordinate space
                    int xPos = (config.getWidth() - width) / 2;
                    int yPos = (int) (config.getHeight() / config.getYScale() - (1 + lineCount) * fm.getHeight()
                            - config.getHeight() * 0.037 + fm.getAscent());

                    // Apply scaling
                    g2d.scale(config.getXScale(), config.getYScale());

                    // Draw outline
                    g2d.setColor(config.getBorderColor());
                    for (int x = -config.getOffset(); x <= config.getOffset(); x++) {
                        for (int y = -config.getOffset(); y <= config.getOffset(); y++) {
                            g2d.drawString(line, xPos + x, yPos + y);
                        }
                    }

                    // Draw text
                    g2d.setColor(config.getInnerColor());
                    g2d.drawString(line, xPos, yPos);
                }

            } finally {
                // Restore original transform
                g2d.setTransform(originalTransform);
            }
        } catch (Exception e) {
            log.error("Error drawing subtitle", e);
            throw new RuntimeException("Failed to draw subtitle", e);
        }
    }

    public static Font getFont(String fontFamilyName, float size) throws FontFormatException, IOException {
        return fontCache.computeIfAbsent(fontFamilyName, k -> {
            try {
                ClassPathResource classPathResource = new ClassPathResource(k);
                return Font.createFont(Font.TRUETYPE_FONT, classPathResource.getFile()).deriveFont(size);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load font: " + k, e);
            }
        });
    }
}
