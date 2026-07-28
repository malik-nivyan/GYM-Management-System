package com.titanforge.gym.controller;

import com.titanforge.gym.dto.trainer.TrainerRequest;
import com.titanforge.gym.dto.trainer.TrainerResponse;
import com.titanforge.gym.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public List<TrainerResponse> all() {
        return trainerService.findAll();
    }

    @GetMapping("/{id}")
    public TrainerResponse get(@PathVariable Integer id) {
        return trainerService.findById(id);
    }

    @PostMapping
    public TrainerResponse create(@Valid @RequestBody TrainerRequest request) {
        return trainerService.create(request);
    }

    @PutMapping("/{id}")
    public TrainerResponse update(@PathVariable Integer id, @Valid @RequestBody TrainerRequest request) {
        return trainerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

