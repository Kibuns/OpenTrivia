import { useState } from 'react';
import { startQuiz, checkAnswers } from './api';
import type { QuizResponse, CheckAnswersResponse, AnswerDto } from './types';

export default function App() {
  const [quiz, setQuiz] = useState<QuizResponse | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [result, setResult] = useState<CheckAnswersResponse | null>(null);

  async function handleStart() {
    try {
      const q = await startQuiz(3); // haal 3 vragen op
      setQuiz(q);
      setAnswers({});
      setResult(null);
    } catch (e) {
      console.error(e);
    }
  }

  function handleAnswer(questionId: number, choice: string) {
    setAnswers(prev => ({ ...prev, [questionId]: choice }));
  }

  async function handleSubmit() {
    if (!quiz) return;
    const payload = {
      quizId: quiz.quizId,
      answers: Object.entries(answers).map(([id, choice]) => ({
        questionId: Number(id),
        choice
      })) as AnswerDto[]
    };
    const r = await checkAnswers(payload);
    setResult(r);
  }

  return (
    <div style={{ maxWidth: 600, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Trivia Quiz</h1>

      {!quiz && <button onClick={handleStart}>Start quiz</button>}

      {quiz && !result && (
        <div>
          <ol>
            {quiz.questions.map(q => (
              <li key={q.id} style={{ marginBottom: '1rem' }}>
                <p>{q.prompt}</p>
                {q.choices.map(c => (
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

      {result && (
        <div>
          <h2>Result: {result.correct} / {result.total} correct</h2>
          <ul>
            {result.details.map(d => (
              <li key={d.questionId}>
                Question {d.questionId}: {d.correct ? '✅' : '❌'}
              </li>
            ))}
          </ul>
          <button onClick={() => setQuiz(null)}>Start new quiz</button>
        </div>
      )}
    </div>
  );
}
