package com.example.mygoReaction.service.Impl;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.model.resp.GenericResp;
import com.example.mygoReaction.model.resp.GetAllSavedSeriesResp;
import com.example.mygoReaction.repository.SavedSeriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SavedSeriesServiceImpl {
    private final SavedSeriesRepository savedSeriesRepository;

    public SavedSeriesServiceImpl(SavedSeriesRepository savedSeriesRepository) {
        this.savedSeriesRepository = savedSeriesRepository;
    }

    public GenericResp getAllSavedSeries() {
        List<SavedSeriesEntity> savedSeries = null;
        GetAllSavedSeriesResp form = new GetAllSavedSeriesResp();
        try{
            savedSeries = savedSeriesRepository.findAll();
            form.setCode(0);
            form.setMessage("success");
            form.setSavedSeries(savedSeries);
            return form;
        } catch (Exception e) {
            form.setCode(1);
            form.setMessage("Fail");
            log.error(Arrays.toString(e.getStackTrace()));
            return form;
        }
    }
}
