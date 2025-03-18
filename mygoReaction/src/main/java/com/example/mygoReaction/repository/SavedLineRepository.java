package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.SavedLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SavedLineRepository extends JpaRepository<SavedLineEntity,String> {
    List<SavedLineEntity> findAll();

    Optional<SavedLineEntity> findBySavedLineId(Integer id);

    @Query("Select sle from SavedLineEntity sle where startTime <= :selectedTime and endTime >= :selectedTime and seriesId = :seriesId ORDER BY createdTimestamp")
    List<SavedLineEntity> findBySeriesIdAndTimestamp(Integer seriesId, Instant selectedTime);

    List<SavedLineEntity> findByLineContaining(String keyword);


}

