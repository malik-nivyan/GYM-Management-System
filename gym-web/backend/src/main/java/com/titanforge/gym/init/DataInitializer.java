package com.titanforge.gym.init;

import com.titanforge.gym.model.*;
import com.titanforge.gym.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer {
    private final MembershipRepository membershipRepository;
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final LoginUserRepository loginUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(MembershipRepository membershipRepository, TrainerRepository trainerRepository,
                           MemberRepository memberRepository, PaymentRepository paymentRepository,
                           LoginUserRepository loginUserRepository, PasswordEncoder passwordEncoder) {
        this.membershipRepository = membershipRepository;
        this.trainerRepository = trainerRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.loginUserRepository = loginUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        if (membershipRepository.count() == 0) {
            seedDomainData();
        }
        upsertUser("admin", "nivyan", "ADMIN");
        upsertUser("staff", "staffpass", "STAFF");
    }

    private void seedDomainData() {

        Membership m1 = Membership.builder().name("Basic").durationMonths(1).fee(1500.0).build();
        Membership m2 = Membership.builder().name("Standard").durationMonths(3).fee(4000.0).build();
        Membership m3 = Membership.builder().name("Premium").durationMonths(12).fee(12000.0).build();
        membershipRepository.save(m1); membershipRepository.save(m2); membershipRepository.save(m3);

        Trainer t1 = Trainer.builder().firstName("John").lastName("Doe").gender("Male").age(30)
                .phone("0300-1111111").email("john.doe@example.com").specialization("Strength").hireDate(LocalDate.of(2022,1,15)).build();
        Trainer t2 = Trainer.builder().firstName("Jane").lastName("Smith").gender("Female").age(28)
                .phone("0300-2222222").email("jane.smith@example.com").specialization("Yoga").hireDate(LocalDate.of(2023,6,1)).build();
        trainerRepository.save(t1); trainerRepository.save(t2);

        Member a = Member.builder().firstName("Alice").lastName("Brown").gender("Female").age(26)
                .phone("0311-1111111").email("alice.brown@example.com").joinDate(LocalDate.of(2023,1,1))
                .trainer(t1).status("Active").membership(m2).build();
        Member b = Member.builder().firstName("Bob").lastName("White").gender("Male").age(34)
                .phone("0311-2222222").email("bob.white@example.com").joinDate(LocalDate.of(2022,1,1))
                .trainer(t2).status("Inactive").membership(m1).build();
        Member c = Member.builder().firstName("Carl").lastName("Black").gender("Male").age(40)
                .phone("0311-3333333").email("carl.black@example.com").joinDate(LocalDate.of(2021,1,15))
                .trainer(t1).status("Active").membership(m3).build();
        memberRepository.save(a); memberRepository.save(b); memberRepository.save(c);

        Payment p1 = Payment.builder().member(a).amount(4000.0).paymentDate(LocalDate.of(2023,1,1)).paymentMethod("Card").status("Paid").build();
        Payment p2 = Payment.builder().member(c).amount(12000.0).paymentDate(LocalDate.of(2021,1,15)).paymentMethod("Bank").status("Paid").build();
        Payment p3 = Payment.builder().member(a).amount(1500.0).paymentDate(LocalDate.of(2024,6,1)).paymentMethod("Cash").status("Pending").build();
        paymentRepository.save(p1); paymentRepository.save(p2); paymentRepository.save(p3);
    }

    private void upsertUser(String username, String rawPassword, String role) {
        LoginUser user = loginUserRepository.findById(username)
                .orElseGet(() -> LoginUser.builder().username(username).build());

        if (user.getPassword() == null || !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setRole(role);
        loginUserRepository.save(user);
    }
}

