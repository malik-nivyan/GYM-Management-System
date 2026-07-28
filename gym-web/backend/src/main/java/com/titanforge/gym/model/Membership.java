package com.titanforge.gym.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Membership_id")
    private Integer membershipId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Duration_months")
    private Integer durationMonths;

    @Column(name = "Fee")
    private Double fee;
}

