package com.example.mygoReaction.repository;

import com.example.mygoReaction.entity.Time1Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Time1Repository extends JpaRepository<Time1Entity, Integer> {

    Optional<Time1Entity> findById(Integer id);

    List<Time1Entity> findAll();
}
