package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.Test2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Test2Repository extends JpaRepository<Test2Entity,String> {
    List<Test2Entity> findAll();

    Optional<Test2Entity> findById(Integer id);

    @Query("Select t2e from Test2Entity t2e where startTime >= :startTime and endTime <= :endTime ORDER BY createdTimestamp")
    List<Test2Entity> findByTimeStamp(Instant startTime, Instant endTime);

    List<Test2Entity> findBySeriesIdAndLineContaining(Integer seriesId, String keyword);
}

