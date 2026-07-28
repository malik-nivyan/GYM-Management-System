package com.titanforge.gym.controller;

import com.titanforge.gym.dto.membership.MembershipRequest;
import com.titanforge.gym.dto.membership.MembershipResponse;
import com.titanforge.gym.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping
    public List<MembershipResponse> all() {
        return membershipService.findAll();
    }

    @GetMapping("/{id}")
    public MembershipResponse get(@PathVariable Integer id) {
        return membershipService.findById(id);
    }

    @PostMapping
    public MembershipResponse create(@Valid @RequestBody MembershipRequest request) {
        return membershipService.create(request);
    }

    @PutMapping("/{id}")
    public MembershipResponse update(@PathVariable Integer id, @Valid @RequestBody MembershipRequest request) {
        return membershipService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        membershipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

