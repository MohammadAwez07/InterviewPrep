'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation } from '@tanstack/react-query'
import api from '@/lib/api'
import { useParams } from 'next/navigation'
import dynamic from 'next/dynamic'
import { useState, useEffect } from 'react'
import {
  ChevronLeft, Lightbulb, CheckCircle, AlertCircle,
  XCircle, ArrowRight, BookOpen, ExternalLink, Clock, Database,
  Zap, Copy, Check, Play, Code2
} from 'lucide-react'
import Link from 'next/link'

const MonacoEditor = dynamic(() => import('@monaco-editor/react'), { ssr: false })

interface SolutionApproach {
  id: string
  approachType: 'BRUTE_FORCE' | 'BETTER' | 'OPTIMAL'
  approachName: string
  timeComplexity: string
  spaceComplexity: string
  code: string
  explanation: string
  intuition: string
  optimal: boolean
  orderIndex: number
}

interface Example {
  input: string
  output: string
  explanation?: string
}

interface ProblemDetail {
  id: string
  title: string
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  topic: string
  stepNumber?: number
  sectionName?: string
  patternTags?: string[]
  videoSolutionUrl?: string
  articleSolutionUrl?: string
  description: string
  constraintsText: string
  examples: Example[]
  hints: string[]
  solutionApproaches: SolutionApproach[]
  solutionCode?: string
  solved: boolean
  lastSubmittedCode?: string
}

const diffStyles = {
  EASY: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  MEDIUM: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
  HARD: 'bg-red-500/10 text-red-400 border-red-500/20',
}

const approachMeta = {
  BRUTE_FORCE: { borderColor: 'border-l-red-500',    badge: 'bg-red-500/10 text-red-400',     label: 'Brute Force' },
  BETTER:      { borderColor: 'border-l-amber-500',   badge: 'bg-amber-500/10 text-amber-400',  label: 'Better'      },
  OPTIMAL:     { borderColor: 'border-l-emerald-500', badge: 'bg-emerald-500/10 text-emerald-400', label: 'Optimal'  },
}

function normaliseCode(code: string): string {
  if (!code) return ''
  return code.replace(/\\n/g, '\n').replace(/\\t/g, '    ')
}

function CodeBlock({ code, language = 'java' }: { code: string; language?: string }) {
  const [copied, setCopied] = useState(false)
  const src = normaliseCode(code)
  const lineCount = src.split('\n').length
  const height = Math.min(Math.max(lineCount * 21 + 32, 100), 560)

  return (
    <div className="rounded-xl overflow-hidden border border-white/8" style={{ background: '#0d1117' }}>
      <div className="flex items-center justify-between px-4 py-2 border-b border-white/5">
        <span className="text-[10px] text-gray-600 font-medium uppercase tracking-wider">{language}</span>
        <button
          onClick={() => { navigator.clipboard.writeText(src); setCopied(true); setTimeout(() => setCopied(false), 2000) }}
          className="flex items-center gap-1 text-[10px] text-gray-600 hover:text-gray-300 transition"
        >
          {copied ? <Check size={11} className="text-emerald-400" /> : <Copy size={11} />}
          {copied ? 'Copied' : 'Copy'}
        </button>
      </div>
      <MonacoEditor
        height={`${height}px`}
        language={language}
        theme="vs-dark"
        value={src}
        options={{
          readOnly: true,
          fontSize: 12,
          fontFamily: '"JetBrains Mono", "Fira Code", Menlo, monospace',
          minimap: { enabled: false },
          scrollBeyondLastLine: false,
          lineNumbers: 'on',
          padding: { top: 10, bottom: 10 },
          renderLineHighlight: 'none',
          scrollbar: { vertical: 'auto', horizontal: 'auto', verticalScrollbarSize: 4 },
          overviewRulerLanes: 0,
          folding: true,
          contextmenu: false,
          wordWrap: 'off',
          glyphMargin: false,
          lineDecorationsWidth: 0,
        }}
      />
    </div>
  )
}

type LeftTab = 'problem' | 'solutions'

export default function ProblemDetailPage() {
  const { slug } = useParams<{ slug: string }>()
  const [code, setCode] = useState('')
  const [leftTab, setLeftTab] = useState<LeftTab>('problem')
  const [showHints, setShowHints] = useState(false)
  const [submitStatus, setSubmitStatus] = useState<'ACCEPTED' | 'PARTIAL' | 'FAILED' | null>(null)
  const [activeApproachId, setActiveApproachId] = useState<string | null>(null)
  const [showTestPanel, setShowTestPanel] = useState(false)
  const [userOutput, setUserOutput] = useState('')

  const { data: problem, isLoading } = useQuery<ProblemDetail>({
    queryKey: ['problem', slug],
    queryFn: () => api.get(`/problems/${slug}`).then((r) => r.data.data),
  })

  // Auto-select optimal (or first) approach when problem loads
  useEffect(() => {
    const approaches = problem?.solutionApproaches
    if (approaches && approaches.length > 0) {
      const best = approaches.find((s) => s.optimal) ?? approaches[0]
      setActiveApproachId(best.id)
    }
  }, [problem?.id])

  useEffect(() => {
    if (problem?.lastSubmittedCode) setCode(problem.lastSubmittedCode)
  }, [problem?.lastSubmittedCode])

  const submitMutation = useMutation({
    mutationFn: (status: 'ACCEPTED' | 'PARTIAL' | 'FAILED') =>
      api.post(`/problems/${slug}/submit`, { submittedCode: code, status, language: 'JAVA' }),
    onSuccess: (_, status) => setSubmitStatus(status),
  })

  const approaches = problem?.solutionApproaches ?? []
  const activeApproach = approaches.find((s) => s.id === activeApproachId) ?? approaches[0] ?? null

  return (
    <AppShell>
      <div className="flex" style={{ height: 'calc(100vh - 64px)', overflow: 'hidden' }}>

        {/* ── Left panel ── */}
        <div className="w-[46%] flex flex-col border-r" style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}>

          {/* Problem header — always visible */}
          <div className="px-6 pt-4 pb-0 border-b shrink-0" style={{ borderColor: 'var(--border)' }}>
            <Link href="/problems"
              className="inline-flex items-center gap-1.5 text-xs text-gray-600 hover:text-gray-300 transition mb-3">
              <ChevronLeft size={13} /> Problems
            </Link>

            {isLoading ? (
              <div className="space-y-2 mb-4">
                <div className="h-5 bg-white/5 rounded animate-pulse w-3/4" />
                <div className="h-3 bg-white/5 rounded animate-pulse w-1/3" />
              </div>
            ) : problem ? (
              <div className="mb-0">
                <div className="flex flex-wrap items-center gap-2 mb-2">
                  <span className={`px-2 py-0.5 rounded text-[10px] font-semibold border ${diffStyles[problem.difficulty]}`}>
                    {problem.difficulty}
                  </span>
                  {problem.stepNumber && (
                    <span className="text-[10px] text-indigo-400 bg-indigo-500/10 px-2 py-0.5 rounded">
                      Step {problem.stepNumber}
                    </span>
                  )}
                  {problem.patternTags?.map(tag => (
                    <span key={tag} className="px-2 py-0.5 rounded text-[10px] bg-white/5 text-gray-500">
                      {tag}
                    </span>
                  ))}
                </div>
                <h1 className="text-lg font-bold text-white leading-tight mb-3">{problem.title}</h1>
              </div>
            ) : null}

            {/* Tab bar */}
            <div className="flex gap-0 -mb-px">
              {([
                { id: 'problem',   label: 'Problem',   icon: BookOpen },
                { id: 'solutions', label: `Solutions${approaches.length > 0 ? ` (${approaches.length})` : ''}`, icon: Code2 },
              ] as { id: LeftTab; label: string; icon: React.ElementType }[]).map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  onClick={() => setLeftTab(id)}
                  className={`flex items-center gap-1.5 px-4 py-2.5 text-xs font-medium border-b-2 transition ${
                    leftTab === id
                      ? 'border-indigo-500 text-indigo-400'
                      : 'border-transparent text-gray-600 hover:text-gray-300'
                  }`}
                >
                  <Icon size={12} />
                  {label}
                </button>
              ))}
            </div>
          </div>

          {/* Scrollable tab body */}
          <div className="flex-1 overflow-y-auto">

            {/* ── Problem Tab ── */}
            {leftTab === 'problem' && problem && (
              <div className="px-6 py-5 space-y-5">

                {(problem.videoSolutionUrl || problem.articleSolutionUrl) && (
                  <div className="flex gap-2">
                    {problem.videoSolutionUrl && (
                      <a href={problem.videoSolutionUrl} target="_blank" rel="noopener noreferrer"
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-red-500/10 text-red-400 text-xs hover:bg-red-500/15 transition">
                        <ExternalLink size={11} /> Video
                      </a>
                    )}
                    {problem.articleSolutionUrl && (
                      <a href={problem.articleSolutionUrl} target="_blank" rel="noopener noreferrer"
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-indigo-500/10 text-indigo-400 text-xs hover:bg-indigo-500/15 transition">
                        <BookOpen size={11} /> Article
                      </a>
                    )}
                  </div>
                )}

                <p className="text-sm text-gray-300 leading-relaxed whitespace-pre-wrap">
                  {problem.description}
                </p>

                {problem.examples?.map((ex, i) => (
                  <div key={i} className="bg-white/[0.02] rounded-xl p-4 border border-white/5">
                    <p className="text-[10px] uppercase tracking-wider font-semibold text-gray-600 mb-3">
                      Example {i + 1}
                    </p>
                    <div className="font-mono text-xs space-y-1.5">
                      <p><span className="text-gray-600">Input: </span><span className="text-gray-300">{ex.input}</span></p>
                      <p><span className="text-gray-600">Output: </span><span className="text-gray-300">{ex.output}</span></p>
                    </div>
                    {ex.explanation && (
                      <p className="text-gray-500 mt-3 text-xs leading-relaxed border-t border-white/5 pt-3">
                        {ex.explanation}
                      </p>
                    )}
                  </div>
                ))}

                {problem.constraintsText && (
                  <div className="bg-white/[0.02] rounded-xl p-4 border border-white/5">
                    <p className="text-[10px] uppercase tracking-wider text-gray-600 mb-2 font-semibold">Constraints</p>
                    <p className="text-xs text-gray-400 font-mono leading-relaxed">{problem.constraintsText}</p>
                  </div>
                )}

                {problem.hints?.length > 0 && (
                  <div>
                    <button onClick={() => setShowHints(!showHints)}
                      className="flex items-center gap-2 text-amber-400 hover:text-amber-300 text-xs transition font-medium">
                      <Lightbulb size={12} />
                      {showHints ? 'Hide hints' : `Show hints (${problem.hints.length})`}
                    </button>
                    {showHints && (
                      <ul className="mt-3 space-y-2 pl-1">
                        {problem.hints.map((h, i) => (
                          <li key={i} className="flex gap-2 text-xs text-gray-400">
                            <ArrowRight size={11} className="text-amber-500/60 shrink-0 mt-0.5" />
                            {h}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}
              </div>
            )}

            {/* ── Solutions Tab ── */}
            {leftTab === 'solutions' && (
              <div className="px-6 py-5">
                {approaches.length === 0 ? (
                  <div className="py-10 text-center">
                    <BookOpen size={24} className="text-gray-700 mx-auto mb-3" />
                    <p className="text-sm text-gray-500">No solution approaches added yet.</p>
                    {problem?.articleSolutionUrl && (
                      <a href={problem.articleSolutionUrl} target="_blank" rel="noopener noreferrer"
                        className="inline-flex items-center gap-1.5 mt-3 text-xs text-indigo-400 hover:underline">
                        <ExternalLink size={11} /> Read on TakeUForward
                      </a>
                    )}
                  </div>
                ) : (
                  <div className="space-y-5">

                    {/* Approach selector pills */}
                    <div className="flex gap-2 flex-wrap">
                      {approaches.map((s) => {
                        const meta = approachMeta[s.approachType]
                        const isActive = s.id === activeApproachId
                        return (
                          <button
                            key={s.id}
                            onClick={() => setActiveApproachId(s.id)}
                            className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-xs font-semibold border-l-4 transition ${meta.borderColor} ${
                              isActive
                                ? 'bg-white text-gray-900'
                                : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white'
                            }`}
                          >
                            {s.optimal && <Zap size={10} className={isActive ? 'text-emerald-600' : 'text-emerald-500'} />}
                            {meta.label}
                          </button>
                        )
                      })}
                    </div>

                    {/* Active approach detail */}
                    {activeApproach && (
                      <div className="space-y-4">

                        {/* Header with complexity */}
                        <div className="bg-white/[0.03] rounded-xl p-4 border border-white/5">
                          <div className="flex items-start justify-between mb-3">
                            <h3 className="text-sm font-semibold text-white">{activeApproach.approachName}</h3>
                            {activeApproach.optimal && (
                              <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-500/15 text-emerald-400 border border-emerald-500/20">
                                Optimal
                              </span>
                            )}
                          </div>
                          <div className="flex gap-5">
                            <span className="flex items-center gap-1.5 text-xs">
                              <Clock size={11} className="text-gray-600" />
                              <span className="text-gray-600">Time:</span>
                              <code className="text-gray-300 font-mono">{activeApproach.timeComplexity}</code>
                            </span>
                            <span className="flex items-center gap-1.5 text-xs">
                              <Database size={11} className="text-gray-600" />
                              <span className="text-gray-600">Space:</span>
                              <code className="text-gray-300 font-mono">{activeApproach.spaceComplexity}</code>
                            </span>
                          </div>
                        </div>

                        {/* Intuition */}
                        {activeApproach.intuition && (
                          <div className="bg-indigo-500/5 rounded-xl p-4 border border-indigo-500/10">
                            <p className="text-[10px] uppercase tracking-wider text-indigo-400 mb-2 font-semibold">Intuition</p>
                            <p className="text-sm text-gray-300 leading-relaxed">{activeApproach.intuition}</p>
                          </div>
                        )}

                        {/* Algorithm steps */}
                        {activeApproach.explanation && (
                          <div className="bg-white/[0.02] rounded-xl p-4 border border-white/5">
                            <p className="text-[10px] uppercase tracking-wider text-gray-600 mb-2 font-semibold">Algorithm</p>
                            <p className="text-sm text-gray-400 leading-relaxed whitespace-pre-wrap">{activeApproach.explanation}</p>
                          </div>
                        )}

                        {/* Code block */}
                        {activeApproach.code && <CodeBlock code={activeApproach.code} />}
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* ── Right panel: Editor ── */}
        <div className="flex-1 flex flex-col" style={{ background: 'var(--bg-primary)' }}>

          {/* Toolbar */}
          <div className="flex items-center gap-2 px-4 py-2.5 border-b shrink-0"
            style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}>
            <span className="text-xs text-gray-600 font-mono">Java</span>

            <button
              onClick={() => setShowTestPanel(!showTestPanel)}
              className="ml-auto flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-white/5 text-gray-400 hover:text-white hover:bg-white/10 border border-white/8 transition">
              <Play size={11} />
              Test
            </button>

            <div className="flex items-center gap-1.5">
              {(['ACCEPTED', 'PARTIAL', 'FAILED'] as const).map((s) => {
                const icons = { ACCEPTED: CheckCircle, PARTIAL: AlertCircle, FAILED: XCircle }
                const Icon = icons[s]
                const cls = {
                  ACCEPTED: 'bg-emerald-500/15 text-emerald-400 hover:bg-emerald-500/25 ring-1 ring-emerald-500/30',
                  PARTIAL:  'bg-amber-500/15  text-amber-400  hover:bg-amber-500/25  ring-1 ring-amber-500/30',
                  FAILED:   'bg-red-500/15    text-red-400    hover:bg-red-500/25    ring-1 ring-red-500/30',
                }[s]
                const labels = { ACCEPTED: 'Accept', PARTIAL: 'Partial', FAILED: 'Failed' }
                return (
                  <button key={s} onClick={() => submitMutation.mutate(s)}
                    disabled={submitMutation.isPending || !code.trim()}
                    className={`flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-xs font-semibold transition disabled:opacity-40 ${cls}`}>
                    <Icon size={11} />
                    {labels[s]}
                  </button>
                )
              })}
            </div>
          </div>

          {/* Test panel */}
          {showTestPanel && problem && (
            <div className="border-b shrink-0" style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}>
              <div className="px-4 py-3">
                <p className="text-[10px] uppercase tracking-widest text-gray-600 font-semibold mb-3">Test Cases</p>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <p className="text-[10px] text-gray-600 mb-1.5 font-medium">Expected</p>
                    {problem.examples?.slice(0, 2).map((ex, i) => (
                      <div key={i} className="bg-white/[0.03] border border-white/5 rounded-lg p-2.5 mb-2">
                        <p className="text-[10px] font-mono text-gray-500">In: <span className="text-gray-300">{ex.input}</span></p>
                        <p className="text-[10px] font-mono text-gray-500 mt-1">Out: <span className="text-emerald-400 font-semibold">{ex.output}</span></p>
                      </div>
                    ))}
                  </div>
                  <div>
                    <p className="text-[10px] text-gray-600 mb-1.5 font-medium">Your Output</p>
                    <textarea
                      value={userOutput}
                      onChange={(e) => setUserOutput(e.target.value)}
                      placeholder="Paste your output here..."
                      className="w-full h-[88px] bg-white/[0.03] border border-white/8 rounded-lg p-2.5 text-[11px] font-mono text-gray-300 placeholder-gray-700 resize-none focus:outline-none focus:border-indigo-500/40"
                    />
                  </div>
                </div>
                <p className="text-[10px] text-gray-700 mt-2">Run locally → paste output → mark as Accept / Partial / Failed</p>
              </div>
            </div>
          )}

          {/* Editor */}
          <div className="flex-1 min-h-0">
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
                scrollBeyondLastLine: false,
                lineNumbers: 'on',
                padding: { top: 16, bottom: 16 },
                renderLineHighlight: 'all',
                smoothScrolling: true,
                wordWrap: 'off',
              }}
            />
          </div>

          {/* Status bar */}
          {submitStatus && (
            <div className={`px-5 py-2.5 border-t shrink-0 flex items-center gap-2 text-xs ${
              submitStatus === 'ACCEPTED'
                ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400'
                : submitStatus === 'PARTIAL'
                ? 'bg-amber-500/10 border-amber-500/20 text-amber-400'
                : 'bg-red-500/10 border-red-500/20 text-red-400'
            }`}>
              {submitStatus === 'ACCEPTED' && <><CheckCircle size={13} /> Solution accepted — great work!</>}
              {submitStatus === 'PARTIAL'  && <><AlertCircle  size={13} /> Partial solution saved.</>}
              {submitStatus === 'FAILED'   && <><XCircle      size={13} /> Failed attempt recorded. Keep going!</>}
            </div>
          )}
        </div>
      </div>
    </AppShell>
  )
}
