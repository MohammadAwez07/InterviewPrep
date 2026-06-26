'use client'

import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import Link from 'next/link'
import api from '@/lib/api'
import AppShell from '@/components/layout/AppShell'
import { Search, CheckCircle2, Circle, ChevronRight, Hash, Layers, Target, BookOpen } from 'lucide-react'

interface SolutionApproach {
  id: string
  approachType: 'BRUTE_FORCE' | 'BETTER' | 'OPTIMAL'
  approachName: string
  timeComplexity: string
  spaceComplexity: string
  isOptimal: boolean
}

interface Problem {
  id: string
  slug: string
  title: string
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  topic: string
  solved: boolean
  stepNumber: number
  sectionName: string
  subTopic: string
  patternTags: string[]
  stepOrder: number
  solutionCount: number
}

interface A2ZStep {
  stepNumber: number
  sectionName: string
  problemCount: number
  solvedCount: number
}

const DIFFICULTIES = ['All', 'EASY', 'MEDIUM', 'HARD'] as const

const PATTERNS = [
  'All', 'Array', 'HashMap', 'Two Pointers', 'Sliding Window', 
  'Binary Search', 'Linked List', 'Recursion', 'Stack', 'Queue',
  'Heap', 'Greedy', 'Tree', 'Graph', 'DP', 'Backtracking'
]

function formatDifficulty(d: string): string {
  if (d === 'All') return 'All'
  return d.charAt(0) + d.slice(1).toLowerCase()
}

function getDifficultyColor(diff: string): string {
  switch (diff) {
    case 'EASY': return 'text-emerald-400'
    case 'MEDIUM': return 'text-amber-400'
    case 'HARD': return 'text-red-400'
    default: return 'text-gray-400'
  }
}

function getDifficultyBg(diff: string): string {
  switch (diff) {
    case 'EASY': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    case 'MEDIUM': return 'bg-amber-500/10 text-amber-400 border-amber-500/20'
    case 'HARD': return 'bg-red-500/10 text-red-400 border-red-500/20'
    default: return 'bg-white/5 text-gray-400 border-white/10'
  }
}

export default function ProblemsPage() {
  const [search, setSearch] = useState('')
  const [difficulty, setDifficulty] = useState('All')
  const [selectedPattern, setSelectedPattern] = useState('All')
  const [selectedStep, setSelectedStep] = useState<number | null>(null)
  const [viewMode, setViewMode] = useState<'a2z' | 'patterns'>('a2z')

  const { data: problems = [], isLoading } = useQuery<Problem[]>({
    queryKey: ['problems', 'a2z', selectedStep, difficulty, selectedPattern],
    queryFn: async () => {
      const params = new URLSearchParams()
      if (selectedStep) params.append('step', selectedStep.toString())
      if (difficulty !== 'All') params.append('difficulty', difficulty)
      if (selectedPattern !== 'All') params.append('pattern', selectedPattern)
      
      const res = await api.get(`/problems/a2z?${params}`)
      const d = res.data.data
      return Array.isArray(d) ? d : []
    },
  })

  const { data: steps = [] } = useQuery<A2ZStep[]>({
    queryKey: ['a2z-steps'],
    queryFn: async () => {
      const res = await api.get('/problems/a2z/steps')
      return res.data.data || []
    },
  })

  const { data: stats } = useQuery({
    queryKey: ['a2z-stats'],
    queryFn: async () => {
      const res = await api.get('/problems/a2z/stats')
      return res.data.data
    },
  })

  const filtered = problems.filter((p) => {
    if (search && !p.title.toLowerCase().includes(search.toLowerCase())) return false
    return true
  })

  const solved = problems.filter((p) => p.solved).length
  const totalProblems = problems.length
  const completionPercentage = totalProblems > 0 ? Math.round((solved / totalProblems) * 100) : 0

  // Group problems by step
  const problemsByStep = filtered.reduce((acc, p) => {
    const step = p.stepNumber || 0
    if (!acc[step]) acc[step] = []
    acc[step].push(p)
    return acc
  }, {} as Record<number, Problem[]>)

  return (
    <AppShell>
      <div className="flex h-[calc(100vh-64px)]">
        {/* Left Sidebar - A2Z Steps */}
        <div className="w-72 border-r overflow-y-auto shrink-0" style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}>
          <div className="p-4 border-b" style={{ borderColor: 'var(--border)' }}>
            <div className="flex items-center gap-2 mb-3">
              <BookOpen size={16} className="text-indigo-400" />
              <span className="text-xs font-semibold text-gray-600 uppercase tracking-widest">Striver A2Z</span>
            </div>
            
            {/* Overall Progress */}
            {stats && (
              <div className="space-y-2">
                <div className="flex justify-between text-xs">
                  <span className="text-gray-500">Overall</span>
                  <span className="text-white font-medium">{stats.solvedCount}/{stats.totalProblems}</span>
                </div>
                <div className="h-1.5 bg-white/5 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-gradient-to-r from-indigo-500 to-violet-500 rounded-full transition-all"
                    style={{ width: `${stats.completionPercentage}%` }}
                  />
                </div>
                <div className="flex gap-2 text-[10px] text-gray-600">
                  <span className="text-emerald-400">{stats.easyCount} Easy</span>
                  <span className="text-amber-400">{stats.mediumCount} Medium</span>
                  <span className="text-red-400">{stats.hardCount} Hard</span>
                </div>
              </div>
            )}
          </div>

          {/* Step List */}
          <div className="p-2 space-y-0.5">
            <button
              onClick={() => setSelectedStep(null)}
              className={`w-full text-left px-3 py-2 rounded-lg text-xs transition ${
                selectedStep === null
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-400 hover:text-white hover:bg-white/5'
              }`}
            >
              <div className="flex items-center justify-between">
                <span className="font-medium">All Problems</span>
                <span className="text-[10px] opacity-70">{stats?.totalProblems || 0}</span>
              </div>
            </button>

            {steps.map((step) => (
              <button
                key={step.stepNumber}
                onClick={() => setSelectedStep(step.stepNumber)}
                className={`w-full text-left px-3 py-2 rounded-lg text-xs transition ${
                  selectedStep === step.stepNumber
                    ? 'bg-indigo-600 text-white'
                    : 'text-gray-400 hover:text-white hover:bg-white/5'
                }`}
              >
                <div className="flex items-center justify-between mb-0.5">
                  <span className="font-medium">Step {step.stepNumber}</span>
                  <span className="text-[10px] opacity-70">{step.problemCount || 0}</span>
                </div>
                <div className="text-[10px] opacity-60 truncate">
                  {step.sectionName}
                </div>
                {step.solvedCount > 0 && (
                  <div className="mt-1 h-0.5 bg-white/10 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-emerald-400 rounded-full"
                      style={{ width: `${(step.solvedCount / (step.problemCount || 1)) * 100}%` }}
                    />
                  </div>
                )}
              </button>
            ))}
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1 overflow-y-auto">
          <div className="px-8 py-7 max-w-5xl mx-auto">
            {/* Header */}
            <div className="flex items-start justify-between mb-8">
              <div>
                <h1 className="text-2xl font-bold text-white">
                  {selectedStep ? `Step ${selectedStep}` : 'All Problems'}
                </h1>
                <p className="text-sm text-gray-500 mt-1">
                  {solved} / {totalProblems} solved
                  {selectedStep && steps.find(s => s.stepNumber === selectedStep)?.sectionName && (
                    <span className="text-gray-600 ml-2">
                      {steps.find(s => s.stepNumber === selectedStep)?.sectionName}
                    </span>
                  )}
                </p>
              </div>
              <div className="text-right">
                <div className="text-3xl font-bold text-white">{completionPercentage}%</div>
                <div className="text-xs text-gray-600">completion</div>
              </div>
            </div>

            {/* Progress bar */}
            <div className="h-2 bg-white/5 rounded-full mb-6 overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-indigo-500 to-violet-500 rounded-full transition-all"
                style={{ width: `${completionPercentage}%` }}
              />
            </div>

            {/* Filters */}
            <div className="flex flex-wrap gap-3 mb-6">
              {/* Search */}
              <div className="relative flex-1 min-w-[240px]">
                <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-600" />
                <input
                  type="text"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  placeholder="Search problems..."
                  className="input-base pl-9 w-full"
                />
              </div>

              {/* Difficulty */}
              <div className="flex gap-1">
                {DIFFICULTIES.map((d) => (
                  <button
                    key={d}
                    onClick={() => setDifficulty(d)}
                    className={`px-3 py-2 text-xs font-semibold rounded-lg transition ${
                      difficulty === d
                        ? 'bg-white text-gray-900'
                        : 'bg-white/5 border border-white/10 text-gray-400 hover:text-white hover:bg-white/8'
                    }`}
                  >
                    {formatDifficulty(d)}
                  </button>
                ))}
              </div>
            </div>

            {/* Pattern Filter */}
            <div className="flex flex-wrap gap-1.5 mb-8">
              <Layers size={12} className="text-gray-600 mr-1 self-center" />
              {PATTERNS.slice(0, 8).map((p) => (
                <button
                  key={p}
                  onClick={() => setSelectedPattern(p)}
                  className={`px-2.5 py-1 text-[10px] rounded-full transition ${
                    selectedPattern === p
                      ? 'bg-indigo-500/30 text-indigo-300 ring-1 ring-indigo-500/50'
                      : 'text-gray-600 hover:text-gray-400 hover:bg-white/5'
                  }`}
                >
                  {p}
                </button>
              ))}
            </div>

            {/* Problem List */}
            {isLoading ? (
              <div className="flex items-center justify-center h-48">
                <div className="w-6 h-6 border-2 border-indigo-500/30 border-t-indigo-500 rounded-full animate-spin" />
              </div>
            ) : (
              <div className="space-y-6">
                {filtered.length === 0 && (
                  <div className="text-center py-16">
                    <Target size={32} className="text-gray-700 mx-auto mb-3" />
                    <p className="text-gray-500 text-sm">No problems match your filters</p>
                  </div>
                )}

                {/* Group by step if showing all */}
                {(selectedStep === null ? Object.entries(problemsByStep) : [[selectedStep, filtered]])
                  .sort(([a], [b]) => Number(a) - Number(b))
                  .map(([stepNum, stepProblems]) => (
                    <div key={stepNum} className="space-y-2">
                      {/* Section Header */}
                      {selectedStep === null && stepNum !== '0' && (
                        <div className="flex items-center gap-3 py-3 border-b" style={{ borderColor: 'var(--border)' }}>
                          <span className="text-xs font-bold text-indigo-400">Step {stepNum}</span>
                          <span className="text-xs text-gray-600">
                            {stepProblems[0]?.sectionName}
                          </span>
                          <div className="flex-1" />
                          <span className="text-xs text-gray-600">
                            {stepProblems.filter(p => p.solved).length}/{stepProblems.length}
                          </span>
                        </div>
                      )}

                      {/* Problems in this step */}
                      <div className="space-y-1">
                        {stepProblems.map((p, i) => (
                          <Link
                            key={p.id}
                            href={`/problems/${p.slug}`}
                            className="flex items-center gap-4 px-4 py-3 card hover:bg-white/5 hover:border-white/12 transition-all group"
                          >
                            {/* Number */}
                            <span className="text-xs text-gray-600 w-8 text-right shrink-0 font-mono">
                              {p.stepOrder || i + 1}
                            </span>

                            {/* Solved indicator */}
                            {p.solved ? (
                              <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
                            ) : (
                              <Circle size={16} className="text-gray-700 group-hover:text-gray-500 shrink-0 transition" />
                            )}

                            {/* Title */}
                            <div className="flex-1 min-w-0">
                              <span className="text-sm font-medium text-gray-200 group-hover:text-white transition block truncate">
                                {p.title}
                              </span>
                              {p.patternTags && p.patternTags.length > 0 && (
                                <div className="flex gap-1 mt-1">
                                  {p.patternTags.slice(0, 2).map((tag) => (
                                    <span key={tag} className="text-[10px] text-gray-600 bg-white/5 px-1.5 py-0.5 rounded">
                                      {tag}
                                    </span>
                                  ))}
                                </div>
                              )}
                            </div>

                            {/* Solutions count */}
                            {p.solutionCount > 0 && (
                              <div className="flex items-center gap-1 text-[10px] text-gray-600">
                                <Hash size={10} />
                                {p.solutionCount} solutions
                              </div>
                            )}

                            {/* Difficulty */}
                            <span className={`px-2 py-1 text-[10px] font-medium rounded border ${getDifficultyBg(p.difficulty)}`}>
                              {formatDifficulty(p.difficulty)}
                            </span>

                            {/* Arrow */}
                            <ChevronRight size={14} className="text-gray-700 group-hover:text-gray-500 transition" />
                          </Link>
                        ))}
                      </div>
                    </div>
                  ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </AppShell>
  )
}
