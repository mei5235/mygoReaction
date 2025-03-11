package com.example.mygoReaction.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="test2")
@SequenceGenerator(name="test2_seq", allocationSize=1)

public class Test2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "test2_seq")
    private Integer id;

    @Column(nullable = false)
    private Integer seriesId;

    @Column(name="start_time", nullable = false)
    private java.time.Instant startTime;

    @Column(name="end_time",nullable = false)
    private java.time.Instant endTime;

    @Column(nullable = false)
    private String line;

    @Column(name = "screen_cap_path")
    private String screenCapPath;

    private String created_by;

    @CreationTimestamp
    @Column(name = "created_timestamp")
    private java.time.Instant createdTimestamp;

    private String updated_by;

    @UpdateTimestamp
    private java.time.Instant updated_timestamp;
}