'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState, useEffect } from 'react'
import dynamic from 'next/dynamic'
import { formatDuration } from '@/lib/utils'
import { Play, Clock, ChevronLeft, Bot, Sparkles, Check, X, ArrowRight } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

const MonacoEditor = dynamic(() => import('@monaco-editor/react'), { ssr: false })

interface MockSession {
  id: string
  status: 'IN_PROGRESS' | 'SUBMITTED'
  startedAt: string
  endedAt?: string
  durationSec?: number
  score?: number
  problem?: { title: string; description: string; difficulty: string }
  submittedCode?: string
  aiFeedback?: {
    score: number
    correctness: string
    timeComplexity: string
    spaceComplexity: string
    codeQuality: string
    edgeCasesHandled: boolean
    feedback: string
    suggestions: string[]
  }
}

function ScorePill({ score }: { score: number }) {
  const cls =
    score >= 80
      ? 'bg-emerald-500/15 text-emerald-400 ring-1 ring-emerald-500/25'
      : score >= 50
      ? 'bg-amber-500/15 text-amber-400 ring-1 ring-amber-500/25'
      : 'bg-red-500/15 text-red-400 ring-1 ring-red-500/25'
  return (
    <span className={`text-sm font-bold px-3 py-1 rounded-full ${cls}`}>{score}/100</span>
  )
}

export default function MockPage() {
  const qc = useQueryClient()
  const [activeSession, setActiveSession] = useState<MockSession | null>(null)
  const [code, setCode] = useState('')
  const [elapsed, setElapsed] = useState(0)

  const { data: sessions } = useQuery<MockSession[]>({
    queryKey: ['mock-sessions'],
    queryFn: () => api.get('/mock/sessions').then((r) => r.data.data),
  })

  useEffect(() => {
    if (!activeSession || activeSession.status === 'SUBMITTED') return
    const t = setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => clearInterval(t)
  }, [activeSession])

  const startMutation = useMutation({
    mutationFn: () => api.post('/mock/sessions', {}),
    onSuccess: (res) => {
      setActiveSession(res.data.data)
      setCode('')
      setElapsed(0)
      qc.invalidateQueries({ queryKey: ['mock-sessions'] })
    },
  })

  const submitMutation = useMutation({
    mutationFn: () =>
      api.post(`/mock/sessions/${activeSession!.id}/submit`, {
        submittedCode: code,
        language: 'JAVA',
      }),
    onSuccess: (res) => {
      setActiveSession(res.data.data)
      qc.invalidateQueries({ queryKey: ['mock-sessions'] })
    },
  })

  if (activeSession) {
    const submitted = activeSession.status === 'SUBMITTED'
    const fb = activeSession.aiFeedback
    const diffBadge: Record<string, string> = {
      EASY: 'tag-easy', MEDIUM: 'tag-medium', HARD: 'tag-hard',
    }

    return (
      <AppShell>
        <div className="flex" style={{ height: '100vh', overflow: 'hidden' }}>
          {/* Problem + feedback panel */}
          <div
            className="w-[40%] overflow-y-auto border-r flex flex-col"
            style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
          >
            <div className="px-6 pt-5 pb-4 border-b" style={{ borderColor: 'var(--border)' }}>
              <button
                onClick={() => setActiveSession(null)}
                className="inline-flex items-center gap-1.5 text-xs text-gray-500 hover:text-gray-300 transition mb-4"
              >
                <ChevronLeft size={14} /> Session List
              </button>

              {activeSession.problem ? (
                <>
                  <h2 className="text-base font-bold text-white leading-tight mb-2">
                    {activeSession.problem.title}
                  </h2>
                  <span className={`tag ${diffBadge[activeSession.problem.difficulty] ?? 'tag-blue'} text-[10px]`}>
                    {activeSession.problem.difficulty}
                  </span>
                  <p className="mt-4 text-sm text-gray-400 leading-relaxed">
                    {activeSession.problem.description}
                  </p>
                </>
              ) : (
                <p className="text-gray-500 text-sm">Open-ended mock — write any problem solution.</p>
              )}
            </div>

            {submitted && fb && (
              <div className="flex-1 overflow-y-auto px-6 py-5 space-y-4">
                <div className="flex items-center gap-1 mb-2">
                  <Sparkles size={14} className="text-indigo-400" />
                  <h3 className="text-sm font-semibold text-white">AI Feedback</h3>
                </div>

                {/* Score */}
                <div className="card-elevated rounded-xl p-4 flex items-center gap-4">
                  <div>
                    <div className="text-4xl font-bold text-white">{fb.score}</div>
                    <div className="text-xs text-gray-600 mt-0.5">/ 100</div>
                  </div>
                  <div className="flex-1">
                    <div
                      className="h-2 bg-white/5 rounded-full overflow-hidden"
                    >
                      <div
                        className={`h-full rounded-full ${fb.score >= 80 ? 'bg-emerald-500' : fb.score >= 50 ? 'bg-amber-500' : 'bg-red-500'}`}
                        style={{ width: `${fb.score}%` }}
                      />
                    </div>
                    <p className={`text-xs mt-1.5 font-medium ${fb.score >= 80 ? 'text-emerald-400' : fb.score >= 50 ? 'text-amber-400' : 'text-red-400'}`}>
                      {fb.correctness}
                    </p>
                  </div>
                </div>

                {/* Metrics */}
                <div className="grid grid-cols-2 gap-2">
                  {([
                    ['Time', fb.timeComplexity],
                    ['Space', fb.spaceComplexity],
                    ['Code Quality', fb.codeQuality],
                    ['Edge Cases', fb.edgeCasesHandled],
                  ] as [string, string | boolean][]).map(([k, v]) => (
                    <div key={k} className="card-elevated rounded-xl p-3">
                      <div className="text-[10px] text-gray-600 uppercase tracking-wider mb-1">{k}</div>
                      <div className="text-xs text-white font-mono flex items-center gap-1">
                        {k === 'Edge Cases' ? (
                          v ? <><Check size={12} className="text-emerald-400" /> Handled</> : <><X size={12} className="text-red-400" /> Missing</>
                        ) : (
                          v
                        )}
                      </div>
                    </div>
                  ))}
                </div>

                {/* Feedback */}
                <div className="card-elevated rounded-xl p-4">
                  <p className="text-xs text-gray-400 leading-relaxed">{fb.feedback}</p>
                </div>

                {/* Suggestions */}
                {fb.suggestions?.length > 0 && (
                  <div className="card-elevated rounded-xl p-4">
                    <p className="text-[10px] uppercase tracking-wider text-gray-600 mb-3">Suggestions</p>
                    <ul className="space-y-2">
                      {fb.suggestions.map((s, i) => (
                        <li key={i} className="flex gap-2 text-xs text-gray-400">
                          <ArrowRight size={12} className="text-indigo-500/60 shrink-0 mt-0.5" />
                          {s}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Editor panel */}
          <div className="flex-1 flex flex-col" style={{ background: 'var(--bg-primary)' }}>
            <div
              className="flex items-center gap-3 px-4 py-3 border-b shrink-0"
              style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
            >
              <Clock size={14} className="text-gray-500" />
              <span className="text-sm text-gray-300 font-mono tabular-nums">
                {formatDuration(elapsed)}
              </span>
              <div className="ml-auto">
                {!submitted ? (
                  <button
                    onClick={() => submitMutation.mutate()}
                    disabled={submitMutation.isPending || !code.trim()}
                    className="btn-primary flex items-center gap-2 disabled:opacity-40"
                  >
                    <Bot size={14} />
                    {submitMutation.isPending ? 'Evaluating with GPT-4o…' : 'Submit & Evaluate'}
                  </button>
                ) : (
                  <span className="flex items-center gap-2 text-emerald-400 text-xs font-medium">
                    <span className="w-2 h-2 bg-emerald-400 rounded-full" />
                    Submitted · {activeSession.durationSec ? formatDuration(activeSession.durationSec) : ''}
                  </span>
                )}
              </div>
            </div>

            <div className="flex-1">
              <MonacoEditor
                height="100%"
                language="java"
                theme="vs-dark"
                value={code}
                onChange={(v) => setCode(v ?? '')}
                options={{
                  fontSize: 13,
                  fontFamily: '"JetBrains Mono", "Fira Code", Menlo, monospace',
                  minimap: { enabled: false },
                  readOnly: submitted,
                  scrollBeyondLastLine: false,
                  padding: { top: 16 },
                }}
              />
            </div>
          </div>
        </div>
      </AppShell>
    )
  }

  return (
    <AppShell>
      <div className="px-8 py-7 max-w-3xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-white">Mock Interview</h1>
          <p className="text-sm text-gray-500 mt-1">
            Timed coding session with AI-powered evaluation from GPT-4o
          </p>
        </div>

        {/* Start card */}
        <div className="card p-7 mb-8">
          <div className="flex items-start gap-5">
            <div className="w-12 h-12 rounded-2xl bg-indigo-500/15 flex items-center justify-center shrink-0">
              <Bot size={22} className="text-indigo-400" />
            </div>
            <div className="flex-1">
              <h2 className="font-semibold text-white mb-1">Start a new session</h2>
              <p className="text-sm text-gray-500 leading-relaxed mb-5">
                A random problem will be assigned. Write your Java solution, then submit for GPT-4o
                feedback on correctness, time/space complexity, code quality, and edge cases.
              </p>
              <button
                onClick={() => startMutation.mutate()}
                disabled={startMutation.isPending}
                className="btn-primary flex items-center gap-2"
              >
                <Play size={15} />
                {startMutation.isPending ? 'Starting…' : 'Start Mock Interview'}
              </button>
            </div>
          </div>
        </div>

        {/* Past sessions */}
        {sessions && sessions.length > 0 && (
          <div>
            <h2 className="text-sm font-semibold text-gray-400 mb-4">Past Sessions</h2>
            <div className="space-y-2">
              {sessions.map((s) => (
                <button
                  key={s.id}
                  onClick={() => setActiveSession(s)}
                  className="w-full flex items-center gap-4 card px-5 py-4 hover:bg-white/5 hover:border-white/12 transition-all text-left"
                >
                  <div className="flex-1 min-w-0">
                    <div className="text-sm font-medium text-white truncate">
                      {s.problem?.title ?? 'Open Mock'}
                    </div>
                    <div className="text-xs text-gray-600 mt-0.5">
                      {formatDistanceToNow(new Date(s.startedAt), { addSuffix: true })}
                      {s.durationSec ? ` · ${formatDuration(s.durationSec)}` : ''}
                    </div>
                  </div>
                  {s.score != null && <ScorePill score={s.score} />}
                  <span
                    className={`text-[10px] px-2 py-1 rounded-full font-medium ${
                      s.status === 'SUBMITTED'
                        ? 'bg-white/5 text-gray-500'
                        : 'bg-indigo-500/15 text-indigo-400'
                    }`}
                  >
                    {s.status === 'SUBMITTED' ? 'Done' : 'In Progress'}
                  </span>
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
    </AppShell>
  )
}
