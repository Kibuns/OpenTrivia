export interface Question {
  id: number;
  prompt: string;
  choices: string[];
}

export interface QuizResponse {
  quizId: string;
  questions: Question[];
}

export interface AnswerDto {
  questionId: number;
  choice: string;
}

export interface CheckAnswersRequest {
  quizId: string;
  answers: AnswerDto[];
}

export interface CheckAnswersResponse {
  total: number;
  correct: number;
  details: { questionId: number; correct: boolean }[];
}