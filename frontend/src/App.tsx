import { useState } from 'react';
import { startQuiz, checkAnswers } from './api';
import type { QuizResponse, AnswerDto, CheckAnswersResponse } from './types';

type Phase = 'idle' | 'loading' | 'playing' | 'result' | 'error';

export default function App() {
  const [phase, setPhase] = useState<Phase>('idle');
  const [quiz, setQuiz] = useState<QuizResponse | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [result, setResult] = useState<CheckAnswersResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleStart() {
    try {
      setPhase('loading');
      const q = await startQuiz(3);
      setQuiz(q);
      setAnswers({});
      setResult(null);
      setPhase('playing');
    } catch (e: any) {
      setError(e.message ?? "Couldn't start quiz");
      setPhase('error');
    }
  }

  async function handleSubmit() {
    if (!quiz) return;
    try {
      setPhase('loading');
      const payload = {
        quizId: quiz.quizId,
        answers: Object.entries(answers).map(([id, choice]) => ({
          questionId: Number(id),
          choice,
        })) as AnswerDto[],
      };
      const r = await checkAnswers(payload);
      setResult(r);
      setPhase('result');
    } catch (e: any) {
      setError(e.message ?? 'Error checking answers');
      setPhase('error');
    }
  }

  function handleAnswer(questionId: number, choice: string) {
    setAnswers((prev) => ({ ...prev, [questionId]: choice }));
  }

  return (
    <div style={{ maxWidth: 600, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Trivia Quiz</h1>

      {phase === 'idle' && <button onClick={handleStart}>Start quiz</button>}

      {phase === 'loading' && <p>Loading...</p>}

      {phase === 'error' && (
        <div>
          <p style={{ color: 'crimson' }}>{error}</p>
          <button onClick={() => setPhase('idle')}>Try again</button>
        </div>
      )}

      {phase === 'playing' && quiz && !result && (
        <div>
          <ol>
            {quiz.questions.map((q) => (
              <li key={q.id} style={{ marginBottom: '1rem' }}>
                <p>{q.prompt}</p>
                {q.choices.map((c) => (
                  <label key={c} style={{ display: 'block' }}>
                    <input
                      type="radio"
                      name={`q-${q.id}`}
                      checked={answers[q.id] === c}
                      onChange={() => handleAnswer(q.id, c)}
                    />
                    {c}
                  </label>
                ))}
              </li>
            ))}
          </ol>
          <button onClick={handleSubmit}>Check answers</button>
        </div>
      )}

      {phase === 'result' && result && quiz && (
        <div>
          <h2>Result: {result.correct} / {result.total} correct</h2>
          <ul>
            {result.details.map((d) => {
              const q = quiz.questions.find(q => q.id === d.questionId);
              const userAnswer = answers[d.questionId];
              return (
                <li key={d.questionId} style={{ marginBottom: '1rem' }}>
                  <p><strong>{q?.prompt}</strong></p>
                  <p>Your answer: {userAnswer}</p>
                  {d.correct ? (
                    <p style={{ color: 'green' }}>✅ Correct!</p>
                  ) : (
                    <p style={{ color: 'crimson' }}>
                      ❌ Wrong. Correct answer: {d.correctAnswer}
                    </p>
                  )}
                </li>
              );
            })}
    </ul>
    <button onClick={() => setPhase('idle')}>New quiz</button>
  </div>
)}
    </div>
  );
}
