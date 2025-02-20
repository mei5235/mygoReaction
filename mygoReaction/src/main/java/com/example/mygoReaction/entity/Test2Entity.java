package com.example.mygoReaction.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="test2")
public class Test2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String series_name;

    private Integer season;

    private Integer episode;

    private java.time.Instant start_Time;

    private java.time.Instant end_Time;

    private String line;

    private String crated_by;

    //    private java.time.Instant created_timestamp;
}
