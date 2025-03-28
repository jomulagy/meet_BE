package com.example.meet.entity;

import com.example.meet.common.enumulation.PlaceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "place")
@Data
public class Place {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "detail")
    private String detail;

    @Column(name = "x_pos", precision = 20, scale = 14)
    private BigDecimal xPos;

    @Column(name = "y_pos", precision = 20, scale = 14)
    private BigDecimal yPos;

    @Column(name = "type")
    private PlaceType type;

    @Column(name = "url")
    private String url;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;
}
