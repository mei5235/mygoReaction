package com.example.mygoReaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="saved_series",uniqueConstraints = { @UniqueConstraint(columnNames = { "series_name", "season" ,"episode"}) })
@SequenceGenerator(name="saved_series_seq", allocationSize=1)

public class SavedSeriesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_series_seq")
    private Integer seriesId;

    @Column(name = "series_name", nullable = false)
    private String seriesName;

    @Column(nullable = false)
    private Integer season;

    @Column(nullable = false)
    private Integer episode;

    private String created_by;

    @CreationTimestamp
    @Column(nullable = false)
    private java.time.Instant created_timestamp;
}
