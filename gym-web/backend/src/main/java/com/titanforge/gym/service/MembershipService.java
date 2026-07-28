package com.titanforge.gym.service;

import com.titanforge.gym.dto.membership.MembershipRequest;
import com.titanforge.gym.dto.membership.MembershipResponse;
import com.titanforge.gym.exception.ResourceNotFoundException;
import com.titanforge.gym.model.Membership;
import com.titanforge.gym.repository.MembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> findAll() {
        return membershipRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MembershipResponse findById(Integer id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + id));
        return toResponse(membership);
    }

    public MembershipResponse create(MembershipRequest request) {
        Membership membership = new Membership();
        apply(membership, request);
        return toResponse(membershipRepository.save(membership));
    }

    public MembershipResponse update(Integer id, MembershipRequest request) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + id));
        apply(membership, request);
        return toResponse(membershipRepository.save(membership));
    }

    public void delete(Integer id) {
        if (!membershipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Membership not found: " + id);
        }
        membershipRepository.deleteById(id);
    }

    private void apply(Membership membership, MembershipRequest request) {
        membership.setName(request.name());
        membership.setDurationMonths(request.durationMonths());
        membership.setFee(request.fee());
    }

    private MembershipResponse toResponse(Membership membership) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getName(),
                membership.getDurationMonths(),
                membership.getFee()
        );
    }
}

