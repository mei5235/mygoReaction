package com.example.mygoReaction.model.dto;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

public interface SavedLineDto {
    Integer getSavedLineId();
    Integer getSeriesId();
    java.time.Instant getStartTime();
    java.time.Instant getEndTime();
    String getLine();
    byte[] getThumbnail();
}
