package com.nino.opentrivia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nino.opentrivia.model.dto.*;
import com.nino.opentrivia.service.QuizService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getQuestions_shouldReturnQuestionsJson() throws Exception {
        UUID quizId = UUID.randomUUID();
        List<QuestionDto> questions = List.of(
                new QuestionDto(1, "What is 2 + 2?", List.of("4", "3", "5", "6"))
        );
        GetQuestionsResponse response = new GetQuestionsResponse(quizId, questions);

        Mockito.when(quizService.startQuiz(5, null, null)).thenReturn(
                new com.nino.opentrivia.model.domain.Quiz(quizId, null, List.of())
        );

        mockMvc.perform(get("/api/questions")
                        .param("amount", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(quizId.toString()));
    }

    @Test
    void checkAnswers_shouldReturnScoreResponse() throws Exception {
        UUID quizId = UUID.randomUUID();

        CheckAnswersRequest request = new CheckAnswersRequest(
                quizId,
                List.of(new AnswerDto(1, "4"))
        );

        CheckAnswersResponse mockResponse = new CheckAnswersResponse(
                1,
                1,
                List.of(new ResultDto(1, true, "4"))
        );

        Mockito.when(quizService.checkAnswers(
                Mockito.eq(quizId),
                Mockito.anyMap()
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/api/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(1))
                .andExpect(jsonPath("$.details[0].questionId").value(1))
                .andExpect(jsonPath("$.details[0].correct").value(true))
                .andExpect(jsonPath("$.details[0].correctAnswer").value("4"));
    }

    @Test
    void getQuestions_shouldReturnBadRequestIfAmountTooHigh() throws Exception {
        mockMvc.perform(get("/api/questions").param("amount", "1000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkAnswers_shouldReturn404IfQuizNotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();
        CheckAnswersRequest request = new CheckAnswersRequest(
                fakeId,
                List.of(new AnswerDto(1, "test"))
        );

        Mockito.when(quizService.checkAnswers(Mockito.eq(fakeId), Mockito.anyMap()))
                .thenThrow(new NoSuchElementException("Quiz not found or expired"));

        mockMvc.perform(post("/api/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Quiz not found"));
    }

    @Test
    void checkAnswers_shouldReturn400IfNoAnswersProvided() throws Exception {
        CheckAnswersRequest request = new CheckAnswersRequest(
                UUID.randomUUID(),
                List.of()
        );

        mockMvc.perform(post("/api/checkanswers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}