package com.titanforge.gym.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payment_id")
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "Member_id")
    private Member member;

    @Column(name = "Amount")
    private Double amount;

    @Column(name = "Payment_date")
    private LocalDate paymentDate;

    @Column(name = "Payment_method")
    private String paymentMethod;

    @Column(name = "Status")
    private String status;
}

