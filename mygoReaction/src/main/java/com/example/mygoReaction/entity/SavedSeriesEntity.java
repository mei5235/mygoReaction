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
@Table(name="saved_series")
public class SavedSeriesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer seriesId;

    private String series_name;

    private Integer season;

    private Integer episode;

    private String created_by;

    @CreationTimestamp
    private java.time.Instant created_timestamp;
}
