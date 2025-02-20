package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.Test2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Test2Repository extends JpaRepository<Test2Entity,String> {
    List<Test2Entity> findAll();

    List<Test2Entity> findBySeason(Integer season);

    //List<Test2Entity> findLessThan(LocalTime start_date, LocalTime end_date);
}
