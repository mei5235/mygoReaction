package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.SavedLineEntity;
import com.example.mygoReaction.model.dto.SavedLineDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SavedLineRepository extends JpaRepository<SavedLineEntity,String> {
    List<SavedLineEntity> findAll();

    Optional<SavedLineEntity> findBySavedLineId(Integer id);

    @Query("Select sle.savedLineId as savedLineId, sle.seriesId as seriesId, sle.startTime as startTime, sle.endTime as endTime, sle.line as line from SavedLineEntity sle where startTime <= :selectedTime and endTime >= :selectedTime and seriesId = :seriesId ORDER BY createdTimestamp")
    List<SavedLineDto> findBySeriesIdAndTimestamp(Integer seriesId, Instant selectedTime);

    @Query("Select sle.savedLineId as savedLineId, sle.seriesId as seriesId, sle.startTime as startTime, sle.endTime as endTime, sle.line as line, sle.thumbnail as thumbnail from SavedLineEntity sle where (:seriesId is null or :seriesId = seriesId) and line like %:keyword%")
    List<SavedLineDto> findByLineContaining(Integer seriesId, String keyword);


}

