'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState, useEffect } from 'react'
import { ChevronLeft, ChevronRight, RotateCcw, Eye } from 'lucide-react'

interface Flashcard {
  id: string
  topic: string
  subTopic: string
  question: string
  answer: string
  difficultyHint: string
  repetitions?: number
  intervalDays?: number
  nextReviewAt?: string
}

const RATINGS: Record<number, { label: string; desc: string; bg: string }> = {
  1: { label: '1', desc: 'Blackout',     bg: 'bg-red-500/20 hover:bg-red-500/30 text-red-400 ring-1 ring-red-500/30' },
  2: { label: '2', desc: 'Wrong',        bg: 'bg-orange-500/20 hover:bg-orange-500/30 text-orange-400 ring-1 ring-orange-500/30' },
  3: { label: '3', desc: 'Hard',         bg: 'bg-yellow-500/20 hover:bg-yellow-500/30 text-yellow-400 ring-1 ring-yellow-500/30' },
  4: { label: '4', desc: 'Good',         bg: 'bg-teal-500/20 hover:bg-teal-500/30 text-teal-400 ring-1 ring-teal-500/30' },
  5: { label: '5', desc: 'Perfect',      bg: 'bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-400 ring-1 ring-emerald-500/30' },
}

export default function FlashcardsPage() {
  const qc = useQueryClient()
  const [mode, setMode] = useState<'due' | 'browse'>('due')
  const [topic, setTopic] = useState('')
  const [idx, setIdx] = useState(0)
  const [flipped, setFlipped] = useState(false)

  const { data: topics } = useQuery<string[]>({
    queryKey: ['flashcard-topics'],
    queryFn: () => api.get('/flashcards/topics').then((r) => r.data.data),
  })

  useEffect(() => { setIdx(0); setFlipped(false) }, [mode, topic])

  const { data: cards, isLoading } = useQuery<Flashcard[]>({
    queryKey: ['flashcards', mode, topic],
    queryFn: () =>
      mode === 'due'
        ? api.get('/flashcards/due').then((r) => r.data.data)
        : api.get('/flashcards', { params: { topic: topic || undefined } }).then((r) => r.data.data),
  })

  const reviewMutation = useMutation({
    mutationFn: ({ id, rating }: { id: string; rating: number }) =>
      api.post(`/flashcards/${id}/review`, { rating }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['flashcards'] })
      setFlipped(false)
      setIdx((i) => i + 1)
    },
  })

  const card = cards?.[idx]
  const total = cards?.length ?? 0
  const progress = total > 0 ? (idx / total) * 100 : 0

  return (
    <AppShell>
      <div className="px-8 py-7 max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-7">
          <div>
            <h1 className="text-2xl font-bold text-white">Flashcards</h1>
            <p className="text-sm text-gray-500 mt-0.5">
              {mode === 'due' ? 'Spaced repetition review' : 'Browse all cards'}
            </p>
          </div>
          <div className="flex gap-1.5">
            {(['due', 'browse'] as const).map((m) => (
              <button
                key={m}
                onClick={() => setMode(m)}
                className={`px-4 py-2 text-xs font-semibold rounded-lg transition ${
                  mode === m
                    ? 'bg-indigo-600 text-white'
                    : 'bg-white/5 border border-white/10 text-gray-400 hover:text-white'
                }`}
              >
                {m === 'due' ? 'Due Today' : 'Browse All'}
              </button>
            ))}
          </div>
        </div>

        {/* Topic select (browse mode) */}
        {mode === 'browse' && (
          <div className="mb-5">
            <select
              value={topic}
              onChange={(e) => { setTopic(e.target.value); setIdx(0) }}
              className="input-base w-auto"
            >
              <option value="">All Topics</option>
              {topics?.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
        )}

        {/* States */}
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="w-7 h-7 border-2 border-indigo-500/30 border-t-indigo-500 rounded-full animate-spin" />
          </div>
        ) : total === 0 ? (
          <div className="card flex flex-col items-center justify-center py-20 text-center">
            <p className="text-white font-semibold mb-1">
              {mode === 'due' ? 'All caught up' : 'No cards found'}
            </p>
            <p className="text-sm text-gray-500">
              {mode === 'due' ? 'No cards due today. Come back tomorrow.' : 'Try a different topic.'}
            </p>
          </div>
        ) : idx >= total ? (
          <div className="card flex flex-col items-center justify-center py-20 text-center">
            <p className="text-xl font-bold text-white mb-1">Session complete</p>
            <p className="text-sm text-gray-400 mb-6">Reviewed {total} card{total !== 1 ? 's' : ''}</p>
            <button
              onClick={() => { setIdx(0); setFlipped(false) }}
              className="btn-primary flex items-center gap-2"
            >
              <RotateCcw size={14} /> Restart
            </button>
          </div>
        ) : card ? (
          <div className="space-y-4">
            {/* Progress */}
            <div className="flex items-center gap-3">
              <div className="flex-1 h-1 bg-white/5 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-indigo-500 to-violet-500 rounded-full transition-all duration-500"
                  style={{ width: `${progress}%` }}
                />
              </div>
              <span className="text-xs text-gray-500 tabular-nums">{idx + 1} / {total}</span>
            </div>

            {/* Card */}
            <div
              onClick={() => setFlipped(!flipped)}
              className="card cursor-pointer min-h-[280px] p-7 flex flex-col justify-between transition-all hover:border-indigo-500/25 select-none"
            >
              <div className="flex items-center gap-2 flex-wrap">
                <span className="tag tag-purple">{card.topic}</span>
                {card.subTopic && <span className="text-xs text-gray-600">{card.subTopic}</span>}
                {!flipped && (
                  <span className="ml-auto flex items-center gap-1 text-xs text-gray-600">
                    <Eye size={12} /> click to reveal
                  </span>
                )}
              </div>

              <div className="mt-6">
                <p className="text-[10px] uppercase tracking-widest text-gray-600 mb-3 font-medium">
                  {flipped ? 'Answer' : 'Question'}
                </p>
                <p className="text-white text-base leading-relaxed">
                  {flipped ? card.answer : card.question}
                </p>
              </div>

              {!flipped && (
                <div className="mt-6 pt-4 border-t border-white/5 text-xs text-gray-600">
                  Repetitions: {card.repetitions ?? 0} · Interval: {card.intervalDays ?? 0}d
                </div>
              )}
            </div>

            {/* Actions */}
            {flipped ? (
              <div>
                <p className="text-xs text-center text-gray-600 mb-3">Rate your recall</p>
                <div className="grid grid-cols-5 gap-2">
                  {([1, 2, 3, 4, 5] as const).map((r) => {
                    const { label, desc, bg } = RATINGS[r]
                    return (
                      <button
                        key={r}
                        onClick={() => reviewMutation.mutate({ id: card.id, rating: r })}
                        disabled={reviewMutation.isPending}
                        className={`flex flex-col items-center gap-1.5 py-3 rounded-xl text-sm font-bold transition disabled:opacity-40 ${bg}`}
                        title={desc}
                      >
                        <span className="text-base">{label}</span>
                        <span className="text-[9px] font-normal opacity-70 leading-tight">{desc}</span>
                      </button>
                    )
                  })}
                </div>
              </div>
            ) : (
              <div className="flex justify-between">
                <button
                  onClick={() => { setFlipped(false); setIdx((i) => Math.max(0, i - 1)) }}
                  disabled={idx === 0}
                  className="btn-ghost flex items-center gap-1 disabled:opacity-30"
                >
                  <ChevronLeft size={15} /> Prev
                </button>
                <button
                  onClick={() => { setFlipped(false); setIdx((i) => i + 1) }}
                  className="btn-ghost flex items-center gap-1"
                >
                  Skip <ChevronRight size={15} />
                </button>
              </div>
            )}
          </div>
        ) : null}
      </div>
    </AppShell>
  )
}
