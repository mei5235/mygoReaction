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
@Table(name="saved_line")
@SequenceGenerator(name="saved_line_seq", allocationSize=1)

public class SavedLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "saved_line_seq")
    @Column(name = "saved_line_id")
    private Integer savedLineId;

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

    @Column(name = "soap_opera_scn_cap_path")
    private String soapOperaScnCapPath;

    private String created_by;

    @CreationTimestamp
    @Column(name = "created_timestamp")
    private java.time.Instant createdTimestamp;

    private String updated_by;

    @UpdateTimestamp
    private java.time.Instant updated_timestamp;

    private byte[] thumbnail;
}