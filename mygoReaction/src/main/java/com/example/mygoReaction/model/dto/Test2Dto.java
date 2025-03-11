package com.example.mygoReaction.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Test2Dto {
    Integer id;
    Integer series_id;
    Instant start_time;
    Instant end_time;
    String line;
    String screen_cap_path;

}
