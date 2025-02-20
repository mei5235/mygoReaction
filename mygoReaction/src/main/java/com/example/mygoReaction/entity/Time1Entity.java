package com.example.mygoReaction.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test1")
public class Time1Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private java.time.Instant timestamp1;

    private String created_by;

//    private java.time.Instant created_timestamp;
}
