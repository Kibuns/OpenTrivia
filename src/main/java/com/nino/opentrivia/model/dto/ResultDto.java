package com.nino.opentrivia.model.dto;

public record ResultDto(
        int questionId,
        boolean correct
) {}