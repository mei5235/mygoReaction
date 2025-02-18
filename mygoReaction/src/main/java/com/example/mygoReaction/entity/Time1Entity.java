package com.example.mygoReaction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test1")
public class Time1Entity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private java.time.LocalTime time1;

    private java.time.Instant timestamp1;

    private String created_by;
}
