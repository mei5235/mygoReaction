package com.example.mygoReaction.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavedSeriesDto {
    Integer series_id;
    String series_name;
    Integer episode;
    Integer season;
}
