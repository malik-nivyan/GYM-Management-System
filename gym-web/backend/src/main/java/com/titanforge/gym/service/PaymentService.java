package com.titanforge.gym.service;

import com.titanforge.gym.dto.payment.PaymentRequest;
import com.titanforge.gym.dto.payment.PaymentResponse;
import com.titanforge.gym.exception.ResourceNotFoundException;
import com.titanforge.gym.model.Member;
import com.titanforge.gym.model.Payment;
import com.titanforge.gym.repository.MemberRepository;
import com.titanforge.gym.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public PaymentService(PaymentRepository paymentRepository, MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        return toResponse(payment);
    }

    public PaymentResponse create(PaymentRequest request) {
        Payment payment = new Payment();
        apply(payment, request);
        return toResponse(paymentRepository.save(payment));
    }

    public PaymentResponse update(Integer id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        apply(payment, request);
        return toResponse(paymentRepository.save(payment));
    }

    public void delete(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found: " + id);
        }
        paymentRepository.deleteById(id);
    }

    private void apply(Payment payment, PaymentRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));
        payment.setMember(member);
        payment.setAmount(request.amount());
        payment.setPaymentDate(request.paymentDate());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(request.status());
    }

    private PaymentResponse toResponse(Payment payment) {
        String memberName = payment.getMember() == null
                ? null
                : (payment.getMember().getFirstName() + " " + payment.getMember().getLastName()).trim();

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getMember() == null ? null : payment.getMember().getMemberId(),
                memberName,
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getStatus()
        );
    }
}

