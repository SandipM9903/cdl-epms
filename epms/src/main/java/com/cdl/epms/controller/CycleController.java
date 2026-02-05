package com.cdl.epms.controller;

import com.cdl.epms.dto.cycle.CreateCycleRequestDto;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.service.services.CycleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cycles")
@CrossOrigin(origins = "*")
public class CycleController {

    private final CycleService cycleService;

    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @PostMapping
    public ResponseEntity<PerformanceCycle> save(
            @Valid @RequestBody CreateCycleRequestDto requestDto
    ) {
        PerformanceCycle saved = cycleService.createCycle(
                requestDto.getCycleType(),
                requestDto.getYear(),
                requestDto.getQuarter(),
                requestDto.getStartDate(),
                requestDto.getEndDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<String> publish(@PathVariable Long id) {
        String message = cycleService.publishCycle(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/active")
    public ResponseEntity<PerformanceCycle> findActive() {
        return ResponseEntity.ok(cycleService.getActiveCycle());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<String> close(@PathVariable Long id) {
        cycleService.closeCycle(id);
        return ResponseEntity.ok("Performance cycle closed successfully.");
    }
}

