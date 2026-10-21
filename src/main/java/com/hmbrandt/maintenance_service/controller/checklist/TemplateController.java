package com.hmbrandt.maintenance_service.controller.checklist;

import com.hmbrandt.maintenance_service.dto.checklist.TemplateResponseDto;
import com.hmbrandt.maintenance_service.service.checklist.TemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/maintenance/checklist")
@RequiredArgsConstructor
@Tag(name = "Checklist Template", description = "ServiceRepository to control the checklist templates")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/template/{templateId}")
    public ResponseEntity<TemplateResponseDto> getTemplate(@PathVariable Long templateId){
        return  ResponseEntity.ok(templateService.getTemplateById(templateId));
    }
}
