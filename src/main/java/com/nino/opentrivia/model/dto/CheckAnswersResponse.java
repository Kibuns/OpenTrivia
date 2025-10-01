package com.nino.opentrivia.model.dto;

import java.util.List;

public record CheckAnswersResponse(
        int total,
        int correct,
        List<ResultDto> details
) {}