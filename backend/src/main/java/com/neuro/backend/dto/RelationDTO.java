package com.neuro.backend.dto;

import lombok.Data;

@Data
public class RelationDTO {
    private Long noteIdA;
    private Long noteIdB;
    private String relationType; // REFERENCE 或 SIMILAR
}
