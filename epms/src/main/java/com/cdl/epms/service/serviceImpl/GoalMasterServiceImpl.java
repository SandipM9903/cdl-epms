package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.dto.goalmaster.GoalMasterCategoryGroupDto;
import com.cdl.epms.dto.goalmaster.GoalMasterRequestDto;
import com.cdl.epms.dto.goalmaster.GoalMasterResponseDto;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.GoalMaster;
import com.cdl.epms.repository.GoalMasterRepository;
import com.cdl.epms.service.services.GoalMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalMasterServiceImpl implements GoalMasterService {

    private final GoalMasterRepository goalMasterRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public GoalMasterResponseDto createGoalMaster(GoalMasterRequestDto requestDto, String createdBy) {

        validateRequest(requestDto);

        // Check for duplicate differentiator name
        if (goalMasterRepository.existsByDifferentiatorNameIgnoreCase(requestDto.getDifferentiatorName())) {
            throw new ConflictException("Differentiator with name '" + requestDto.getDifferentiatorName() + "' already exists");
        }

        GoalMaster goalMaster = modelMapper.map(requestDto, GoalMaster.class);
        goalMaster.setCategory(requestDto.getCategory().toUpperCase().trim());
        goalMaster.setCreatedBy(createdBy);
        goalMaster.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true);

        GoalMaster savedGoalMaster = goalMasterRepository.save(goalMaster);
        return mapToDto(savedGoalMaster);
    }

    @Override
    @Transactional
    public GoalMasterResponseDto updateGoalMaster(Long id, GoalMasterRequestDto requestDto, String updatedBy) {

        validateRequest(requestDto);

        GoalMaster goalMaster = goalMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal Master not found with id: " + id));

        // Check for duplicate differentiator name (excluding current)
        if (!goalMaster.getDifferentiatorName().equalsIgnoreCase(requestDto.getDifferentiatorName())) {
            if (goalMasterRepository.existsByDifferentiatorNameIgnoreCase(requestDto.getDifferentiatorName())) {
                throw new ConflictException("Differentiator with name '" + requestDto.getDifferentiatorName() + "' already exists");
            }
        }

        goalMaster.setCategory(requestDto.getCategory().toUpperCase().trim());
        goalMaster.setDifferentiatorName(requestDto.getDifferentiatorName());
        goalMaster.setDefinition(requestDto.getDefinition());
        goalMaster.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : goalMaster.getIsActive());
        goalMaster.setDisplayOrder(requestDto.getDisplayOrder());
        goalMaster.setUpdatedBy(updatedBy);

        GoalMaster updatedGoalMaster = goalMasterRepository.save(goalMaster);
        return mapToDto(updatedGoalMaster);
    }

    @Override
    public GoalMasterResponseDto getGoalMasterById(Long id) {
        GoalMaster goalMaster = goalMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal Master not found with id: " + id));
        return mapToDto(goalMaster);
    }

    @Override
    public List<GoalMasterResponseDto> getAllGoalMasters() {
        return goalMasterRepository.findAllGroupedByCategory().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GoalMasterCategoryGroupDto> getActiveGoalMastersGroupedByCategory() {
        List<GoalMaster> activeGoalMasters = goalMasterRepository.findActiveGroupedByCategory();

        // Group by category
        Map<String, List<GoalMaster>> groupedByCategory = activeGoalMasters.stream()
                .collect(Collectors.groupingBy(GoalMaster::getCategory));

        List<GoalMasterCategoryGroupDto> result = new ArrayList<>();

        for (Map.Entry<String, List<GoalMaster>> entry : groupedByCategory.entrySet()) {
            GoalMasterCategoryGroupDto groupDto = new GoalMasterCategoryGroupDto();
            groupDto.setCategory(entry.getKey());
            groupDto.setItems(entry.getValue().stream().map(this::mapToDto).collect(Collectors.toList()));
            result.add(groupDto);
        }

        return result;
    }

    @Override
    public List<GoalMasterResponseDto> getGoalMastersByCategory(String category) {
        return goalMasterRepository.findByCategoryOrderByDisplayOrderAsc(category).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return goalMasterRepository.findDistinctActiveCategories();
    }

    @Override
    public Map<String, Long> getCategoryCounts() {
        List<Object[]> counts = goalMasterRepository.getCategoryCounts();
        Map<String, Long> categoryCounts = new HashMap<>();
        for (Object[] count : counts) {
            categoryCounts.put((String) count[0], (Long) count[1]);
        }
        return categoryCounts;
    }

    @Override
    @Transactional
    public void deleteGoalMaster(Long id) {
        GoalMaster goalMaster = goalMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal Master not found with id: " + id));
        goalMasterRepository.delete(goalMaster);
    }

    @Override
    @Transactional
    public void toggleActiveStatus(Long id, Boolean isActive) {
        GoalMaster goalMaster = goalMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal Master not found with id: " + id));
        goalMaster.setIsActive(isActive);
        goalMasterRepository.save(goalMaster);
    }

    private void validateRequest(GoalMasterRequestDto requestDto) {
        if (requestDto == null) {
            throw new ValidationException("Request body is required");
        }
        if (requestDto.getCategory() == null || requestDto.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category is required");
        }
        if (requestDto.getDifferentiatorName() == null || requestDto.getDifferentiatorName().trim().isEmpty()) {
            throw new ValidationException("Differentiator name is required");
        }
        if (requestDto.getDefinition() == null || requestDto.getDefinition().trim().isEmpty()) {
            throw new ValidationException("Definition is required");
        }
    }

    private GoalMasterResponseDto mapToDto(GoalMaster goalMaster) {
        return modelMapper.map(goalMaster, GoalMasterResponseDto.class);
    }
}