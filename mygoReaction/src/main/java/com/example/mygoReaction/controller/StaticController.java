package com.example.mygoReaction.controller;

import com.example.mygoReaction.constant.Constant;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "/static")
public class StaticController {
    private static final Logger log = LoggerFactory.getLogger(StaticController.class);

    @RequestMapping(value = "/**")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request,String width, String height){
        String uri = request.getRequestURI().replace("/static","");
        String ext = uri.substring(uri.indexOf(".")+1);
        try {
            File f = new File(Constant.MYGO_REACTION_ASSET+ URLDecoder.decode(uri, StandardCharsets.UTF_8));
            BufferedImage p = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(width == null & height == null){
                ImageIO.write(p, ext, baos);

            }else{
                Thumbnails.Builder<BufferedImage> resized = Thumbnails.of(p);
                if (width==null) {
                    log.info("resized.width");
                    resized.height(Integer.parseInt(height));
                }
                else if(height==null) {
                    log.info("resized.height");
                    resized.width(Integer.parseInt(width));
                }
                else {
                    log.info("resized.size");
                    resized.size(Integer.parseInt(width), Integer.parseInt(height)).keepAspectRatio(false);
                }
                ImageIO.write(resized.asBufferedImage(), ext, baos);
            }

            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/"+ext); // Change format as needed
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
