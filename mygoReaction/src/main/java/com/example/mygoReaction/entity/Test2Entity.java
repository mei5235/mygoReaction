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

    private Integer seriesId;

    @Column(name="start_time")
    private java.time.Instant startTime;

    @Column(name="end_time")
    private java.time.Instant endTime;

    private String line;

    private String screen_cap_path;

    private byte[] screen_cap_thumbnail;

    private String created_by;

    @CreationTimestamp
    @Column(name = "created_timestamp")
    private java.time.Instant createdTimestamp;

    private String updated_by;

    @UpdateTimestamp
    private java.time.Instant updated_timestamp;
}