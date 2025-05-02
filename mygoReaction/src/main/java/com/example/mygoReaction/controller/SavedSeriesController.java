package com.example.mygoReaction.controller;

import com.example.mygoReaction.model.resp.GenericResp;
import com.example.mygoReaction.service.Impl.SavedSeriesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("series")
public class SavedSeriesController {

    private static final Logger log = LoggerFactory.getLogger(SavedLineController.class);

    private final SavedSeriesServiceImpl savedSeriesService;

    public SavedSeriesController (SavedSeriesServiceImpl savedSeriesService) {
        this.savedSeriesService = savedSeriesService;
    }

    @RequestMapping(method = RequestMethod.GET, value="/getSavedSeries")
    public ResponseEntity<GenericResp> getAllSavedSeries() {
        GenericResp form = null;

        form = savedSeriesService.getAllSavedSeries();

        return ResponseEntity.ok().body(form);
    }
}
