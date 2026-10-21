package com.hmbrandt.maintenance_service.dto.checklist;

import java.util.List;

public record ItemResponseDto(
    Long itemId,
    String item,
    Integer order,
    Boolean isRequired,
    List<OptionResponseDto> options
) {}
