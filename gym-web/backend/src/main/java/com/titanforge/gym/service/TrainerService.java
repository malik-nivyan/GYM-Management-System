package com.titanforge.gym.service;

import com.titanforge.gym.dto.trainer.TrainerRequest;
import com.titanforge.gym.dto.trainer.TrainerResponse;
import com.titanforge.gym.exception.ResourceNotFoundException;
import com.titanforge.gym.model.Trainer;
import com.titanforge.gym.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> findAll() {
        return trainerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TrainerResponse findById(Integer id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found: " + id));
        return toResponse(trainer);
    }

    public TrainerResponse create(TrainerRequest request) {
        Trainer trainer = new Trainer();
        apply(trainer, request);
        return toResponse(trainerRepository.save(trainer));
    }

    public TrainerResponse update(Integer id, TrainerRequest request) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found: " + id));
        apply(trainer, request);
        return toResponse(trainerRepository.save(trainer));
    }

    public void delete(Integer id) {
        if (!trainerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trainer not found: " + id);
        }
        trainerRepository.deleteById(id);
    }

    private void apply(Trainer trainer, TrainerRequest request) {
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setGender(request.gender());
        trainer.setAge(request.age());
        trainer.setPhone(request.phone());
        trainer.setEmail(request.email());
        trainer.setSpecialization(request.specialization());
        trainer.setHireDate(request.hireDate());
    }

    private TrainerResponse toResponse(Trainer trainer) {
        return new TrainerResponse(
                trainer.getTrainerId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getGender(),
                trainer.getAge(),
                trainer.getPhone(),
                trainer.getEmail(),
                trainer.getSpecialization(),
                trainer.getHireDate()
        );
    }
}

