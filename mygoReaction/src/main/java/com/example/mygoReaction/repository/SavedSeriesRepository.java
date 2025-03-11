package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.SavedSeriesEntity;
import com.example.mygoReaction.model.dto.SavedSeriesDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedSeriesRepository extends JpaRepository<SavedSeriesEntity,String> {
    List<SavedSeriesEntity> findAll();

    Optional<SavedSeriesEntity> findBySeriesId(Integer series_id);

    Optional<SavedSeriesEntity> findBySeriesNameAndSeasonAndEpisode(String seriesName, Integer season, Integer episode);
}
