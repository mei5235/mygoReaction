package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.Test2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Test2Repository extends JpaRepository<Test2Entity,String> {
    List<Test2Entity> findAll();

    Optional<Test2Entity> findById(Integer id);
}
