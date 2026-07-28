package com.titanforge.gym.service;

import com.titanforge.gym.dto.member.MemberRequest;
import com.titanforge.gym.dto.member.MemberResponse;
import com.titanforge.gym.exception.ResourceNotFoundException;
import com.titanforge.gym.model.Member;
import com.titanforge.gym.model.Membership;
import com.titanforge.gym.model.Trainer;
import com.titanforge.gym.repository.MemberRepository;
import com.titanforge.gym.repository.MembershipRepository;
import com.titanforge.gym.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final MembershipRepository membershipRepository;

    public MemberService(MemberRepository memberRepository,
                         TrainerRepository trainerRepository,
                         MembershipRepository membershipRepository) {
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Integer id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        return toResponse(member);
    }

    public MemberResponse create(MemberRequest request) {
        Member member = new Member();
        apply(member, request);
        return toResponse(memberRepository.save(member));
    }

    public MemberResponse update(Integer id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        apply(member, request);
        return toResponse(memberRepository.save(member));
    }

    public void delete(Integer id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found: " + id);
        }
        memberRepository.deleteById(id);
    }

    private void apply(Member member, MemberRequest request) {
        Trainer trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found: " + request.trainerId()));
        Membership membership = membershipRepository.findById(request.membershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + request.membershipId()));

        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setGender(request.gender());
        member.setAge(request.age());
        member.setPhone(request.phone());
        member.setEmail(request.email());
        member.setJoinDate(request.joinDate());
        member.setTrainer(trainer);
        member.setStatus(request.status());
        member.setMembership(membership);
    }

    private MemberResponse toResponse(Member member) {
        String trainerName = member.getTrainer() == null
                ? null
                : (member.getTrainer().getFirstName() + " " + member.getTrainer().getLastName()).trim();
        String membershipName = member.getMembership() == null ? null : member.getMembership().getName();

        return new MemberResponse(
                member.getMemberId(),
                member.getFirstName(),
                member.getLastName(),
                member.getGender(),
                member.getAge(),
                member.getPhone(),
                member.getEmail(),
                member.getJoinDate(),
                member.getTrainer() == null ? null : member.getTrainer().getTrainerId(),
                trainerName,
                member.getStatus(),
                member.getMembership() == null ? null : member.getMembership().getMembershipId(),
                membershipName
        );
    }
}

