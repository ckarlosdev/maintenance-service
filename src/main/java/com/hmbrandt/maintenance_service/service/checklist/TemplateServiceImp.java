package com.hmbrandt.maintenance_service.service.checklist;

import com.hmbrandt.maintenance_service.dto.checklist.ItemResponseDto;
import com.hmbrandt.maintenance_service.dto.checklist.OptionResponseDto;
import com.hmbrandt.maintenance_service.dto.checklist.TemplateResponseDto;
import com.hmbrandt.maintenance_service.entity.checklist.ChecklistItem;
import com.hmbrandt.maintenance_service.entity.checklist.ChecklistTemplate;
import com.hmbrandt.maintenance_service.entity.checklist.ResponseOption;
import com.hmbrandt.maintenance_service.repository.checklist.ChecklistTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImp implements TemplateService{

    private final ChecklistTemplateRepository templateRepository;

    @Override
    public TemplateResponseDto getTemplateById(Long templateId){

        return templateRepository.findById(templateId)
                .map(this::mapTemplateDto)
                .orElseThrow(() -> new EntityNotFoundException("Template not found, id: " + templateId));

    }

    private TemplateResponseDto mapTemplateDto(ChecklistTemplate entity){
        return new TemplateResponseDto(
                entity.getId(),
                entity.getTitle(),
                entity.getTemplateDescription(),
                entity.getItems()
                        .stream()
                        .map(this::mapItemsDto)
                        .collect(Collectors.toList())
        );
    }

    private ItemResponseDto mapItemsDto(ChecklistItem entity){
        return new ItemResponseDto(
                entity.getId(),
                entity.getItemText(),
                entity.getSortOrder(),
                entity.getIsRequired(),
                entity.getResponseOptions()
                        .stream()
                        .map(this::mapOptionsDto)
                        .collect(Collectors.toList())
        );
    }

    private OptionResponseDto mapOptionsDto(ResponseOption entity){
        return new OptionResponseDto(
                entity.getOptionLabel(),
                entity.getSortOrder()
        );
    }
}
