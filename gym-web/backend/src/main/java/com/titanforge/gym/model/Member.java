package com.titanforge.gym.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Member_id")
    private Integer memberId;

    @Column(name = "First_name")
    private String firstName;

    @Column(name = "Last_name")
    private String lastName;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "Age")
    private Integer age;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Join_date")
    private LocalDate joinDate;

    @ManyToOne
    @JoinColumn(name = "Trainer_id")
    private Trainer trainer;

    @Column(name = "Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "Membership_id")
    private Membership membership;
}

