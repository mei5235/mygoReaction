package com.example.mygoReaction.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SearchLineForm {
//    Integer seriesId;
    String seriesName;
    Integer season;
    Integer episode;
    Instant startTime;
    String line;
}
