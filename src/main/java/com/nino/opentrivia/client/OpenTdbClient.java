package com.nino.opentrivia.client;

import java.util.List;

public interface OpenTdbClient {

    record OpenTdbQuestion(
            String category,
            String type,
            String difficulty,
            String question,
            String correct_answer,
            List<String> incorrect_answers
    ) {}

    List<OpenTdbQuestion> fetchQuestions(int amount, String category, String difficulty);
}