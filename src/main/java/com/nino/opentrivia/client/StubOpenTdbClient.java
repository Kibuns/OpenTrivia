package com.nino.opentrivia.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("stub")
public class StubOpenTdbClient implements OpenTdbClient {

    @Override
    public List<OpenTdbQuestion> fetchQuestions(int amount, String category, String difficulty) {
        var q1 = new OpenTdbQuestion(
                "Maths",
                "multiple",
                "easy",
                "What is 2 + 2?",
                "4",
                List.of("3", "5", "6")
        );

        var q2 = new OpenTdbQuestion(
                "Science",
                "boolean",
                "medium",
                "Is water wet?",
                "True",
                List.of("False")
        );

        return List.of(q1, q2);
    }
}