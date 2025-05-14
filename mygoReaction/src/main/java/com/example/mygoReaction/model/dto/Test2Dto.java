package com.example.mygoReaction.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Test2Dto {
    Integer seriesId;
    Instant startTime;
    Instant endTime;
    String line;
}
