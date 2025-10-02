import type { QuizResponse, CheckAnswersResponse, CheckAnswersRequest } from './types';

export async function startQuiz(amount = 5): Promise<QuizResponse> {
  const res = await fetch(`/api/questions?amount=${amount}`);
  if (!res.ok) {
    const problem = await res.json().catch(() => null);
    const message = problem?.detail || `Failed to get questions: ${res.status}`;
    throw new Error(message);
  }
  return res.json();
}

export async function checkAnswers(payload: CheckAnswersRequest): Promise<CheckAnswersResponse> {
  const res = await fetch('/api/checkanswers', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!res.ok) throw new Error(`Failed to check answers: ${res.status}`);
  return res.json();
}
