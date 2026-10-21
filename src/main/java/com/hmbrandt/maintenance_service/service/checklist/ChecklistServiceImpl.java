package com.hmbrandt.maintenance_service.service.checklist;

import com.hmbrandt.maintenance_service.dto.checklist.ChecklistAnswerResponseDto;
import com.hmbrandt.maintenance_service.dto.checklist.ChecklistRequestDto;
import com.hmbrandt.maintenance_service.dto.checklist.ChecklistResponseDto;
import com.hmbrandt.maintenance_service.entity.checklist.Checklist;
import com.hmbrandt.maintenance_service.entity.checklist.ChecklistAnswer;
import com.hmbrandt.maintenance_service.entity.checklist.ChecklistItem;
import com.hmbrandt.maintenance_service.entity.checklist.ChecklistTemplate;
import com.hmbrandt.maintenance_service.repository.checklist.ChecklistItemRepository;
import com.hmbrandt.maintenance_service.repository.checklist.ChecklistRepository;
import com.hmbrandt.maintenance_service.repository.checklist.ChecklistTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistItemRepository itemRepository;

    @Override
    public ChecklistResponseDto saveChecklist(ChecklistRequestDto checklistDto){

        LocalDateTime now = LocalDateTime.now();

        ChecklistTemplate template = templateRepository.findById(checklistDto.templateId())
                .orElseThrow(() -> new EntityNotFoundException("No template found."));

        Checklist newChecklist = new Checklist();
        newChecklist.setChecklistTemplate(template);
        newChecklist.setVehicleId(checklistDto.vehicleId());
        newChecklist.setChecklistStatus(checklistDto.checklistStatus());
        newChecklist.setGeneralComment(checklistDto.generalComment());
        newChecklist.setCompletedAt(now);
        newChecklist.setCreatedDate(now);
        newChecklist.setUpdatedDate(now);

        if (checklistDto.answers() != null && !checklistDto.answers().isEmpty()) {
            List<ChecklistAnswer> answers = checklistDto.answers().stream().map(answerDto -> {

                // Buscar el ítem al que pertenece la respuesta
                ChecklistItem item = itemRepository.findById(answerDto.itemId())
                        .orElseThrow(() -> new EntityNotFoundException("Checklist item not found, id: " + answerDto.itemId()));

                return ChecklistAnswer.builder()
                        .checklist(newChecklist) // Importante: vincular con el checklist padre
                        .checklistItem(item)
                        .answer(answerDto.answer())
                        .answerComment(answerDto.comment())
                        .build();
            }).collect(Collectors.toList());

            newChecklist.setAnswers(answers);
        }

        Checklist savedChecklist = checklistRepository.save(newChecklist);
        return mapToChecklistResponseDto(savedChecklist);
    }

    @Override
    public ChecklistResponseDto findById(Long id){
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Checklist not found, id: "+ id));

        return mapToChecklistResponseDto(checklist);
    }

    @Override
    public List<ChecklistResponseDto> getByVehicleId(Long vehicleId){
        List<Checklist> checklists = checklistRepository.findByVehicleIdOrderByCreatedDateDesc(vehicleId);

        return checklists.stream()
                .map(this::mapToChecklistResponseDto)
                .toList();
    }

    private ChecklistResponseDto mapToChecklistResponseDto(Checklist entity){
        return new ChecklistResponseDto(
                entity.getId(),
                entity.getChecklistTemplate().getId(),
                entity.getVehicleId(),
                entity.getChecklistStatus(),
                entity.getGeneralComment(),
                entity.getAnswers()
                        .stream()
                        .map(this::mapAnswerDto)
                        .toList()
        );
    }

    private ChecklistAnswerResponseDto mapAnswerDto(ChecklistAnswer entity){
        return new ChecklistAnswerResponseDto(
                entity.getId(),
                entity.getChecklist().getId(),
                entity.getChecklistItem().getId(),
                entity.getAnswer(),
                entity.getAnswerComment()
        );
    }
}
