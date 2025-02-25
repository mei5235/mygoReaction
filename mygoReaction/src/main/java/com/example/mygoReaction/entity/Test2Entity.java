package com.example.mygoReaction.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="test2")
public class Test2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer seriesId;

    private java.time.Instant start_Time;

    private java.time.Instant end_Time;

    private String line;

    private String created_by;

    private String screen_cap_path;

    private byte[] screen_cap_thumbnail;

    @CreationTimestamp
    private java.time.Instant created_timestamp;

    private String updated_by;

    @UpdateTimestamp
    private java.time.Instant updated_timestamp;
}