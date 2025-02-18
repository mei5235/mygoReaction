package com.example.mygoReaction.controller;

import com.example.mygoReaction.entity.Time1Entity;
import com.example.mygoReaction.repository.Time1Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@RestController
public class TestController {
    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/")
    public String helloWorld(){
        return "ssss";
    }

    @GetMapping("/test1")
    public String test() throws IOException {
        String pathFromResource = "my_go_subtitle/";
        String filename = "[Nekomoe kissaten] BanG Dream! It’s MyGO!!!!! [03][Web].JPTC.ass";
        File f = resourceLoader.getResource("classpath:"+pathFromResource+filename)
                .getFile();
        StringBuilder out = new StringBuilder();
        try(FileReader fr = new FileReader(f)){
            BufferedReader bfr = new BufferedReader(fr);
            String line = "";
            while(!((line = bfr.readLine()) == null)){
//                System.out.println(line);
                if(line.matches("Dialogue: [0-9:,\\.]+,Dial_CH,.+")) {
                    out.append(line);
                    out.append("\n");
                }
            }
            bfr.close();

        }catch(java.io.IOException e)
        {
            e.printStackTrace();
        }

        return out.toString();
    }
}
