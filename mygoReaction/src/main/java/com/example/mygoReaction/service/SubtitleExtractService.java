package com.example.mygoReaction.service;


import com.example.mygoReaction.entity.SavedSeriesEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

public interface SubtitleExtractService {
    public Boolean insertSubtitleIntoDB(File subtitleFile, Integer seriesId) throws IOException;
}
