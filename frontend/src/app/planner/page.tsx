'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState } from 'react'
import { format } from 'date-fns'
import { CheckCircle2, Calendar, ExternalLink, X, BookOpen, Video, Code2, Layers, PlayCircle, Clock, ArrowRight } from 'lucide-react'

const TOPICS = [
  'Arrays', 'Strings', 'HashMaps & Sets', 'Two Pointers', 'Sliding Window',
  'Linked Lists', 'Stacks & Queues', 'Binary Search', 'Trees - BFS', 'Trees - DFS',
  'Graphs', 'Heaps', 'Dynamic Programming', 'Backtracking', 'Greedy',
]

interface Resource {
  id: string
  title: string
  provider: string
  type: 'VIDEO' | 'COURSE' | 'ARTICLE' | 'PRACTICE' | 'PLAYLIST'
  url: string
  description: string
  duration: string
  isFree: boolean
}

interface PlanDay {
  id: string
  dayNumber: number
  scheduledDate: string
  topic: string
  subtopic?: string
  problemsCount: number
  flashcardsCount: number
  notes?: string
  isCompleted: boolean
}

const TYPE_ICONS: Record<string, React.ComponentType<{ size: number; className?: string }>> = {
  VIDEO: Video, COURSE: Layers, ARTICLE: BookOpen, PRACTICE: Code2, PLAYLIST: PlayCircle,
}

function mapToResourceTopic(plannerTopic: string): string {
  const mapping: Record<string, string> = {
    'Trees - BFS': 'Trees - BFS/DFS',
    'Trees - DFS': 'Trees - BFS/DFS',
    'Graphs - BFS/DFS': 'Graphs - BFS/DFS',
    'Graphs - Advanced': 'Graphs - BFS/DFS',
    'Dynamic Programming - 1D': 'Dynamic Programming - 1D',
    'Dynamic Programming - 2D': 'Dynamic Programming - 2D',
    'Dynamic Programming - Advanced': 'Dynamic Programming - Advanced',
    'Mock Interview Week': 'Mock Interview Week',
  }
  return mapping[plannerTopic] ?? plannerTopic
}

export default function PlannerPage() {
  const qc = useQueryClient()
  const [targetDate, setTargetDate] = useState('')
  const [weakTopics, setWeakTopics] = useState<string[]>([])
  const [selectedDay, setSelectedDay] = useState<PlanDay | null>(null)

  const { data: plan, isLoading } = useQuery({
    queryKey: ['active-plan'],
    queryFn: () => api.get('/planner/active').then((r) => r.data.data),
    retry: false,
  })

  const { data: dayResources, isLoading: resourcesLoading } = useQuery<Resource[]>({
    queryKey: ['day-resources', selectedDay?.topic],
    queryFn: () =>
      api
        .get(`/resources/topic/${encodeURIComponent(mapToResourceTopic(selectedDay!.topic))}`)
        .then((r) => r.data.data),
    enabled: !!selectedDay,
  })

  const generateMutation = useMutation({
    mutationFn: () => api.post('/planner/generate', { targetDate, weakTopics }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['active-plan'] }),
  })

  const completeMutation = useMutation({
    mutationFn: (dayId: string) => api.post(`/planner/days/${dayId}/complete`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['active-plan'] }),
  })

  const toggleTopic = (t: string) =>
    setWeakTopics((prev) => prev.includes(t) ? prev.filter((x) => x !== t) : [...prev, t])

  const today = format(new Date(), 'yyyy-MM-dd')
  const completedDays = plan?.days?.filter((d: PlanDay) => d.isCompleted).length ?? 0
  const totalDays = plan?.days?.length ?? 0

  return (
    <AppShell>
      <div className="flex" style={{ height: '100vh', overflow: 'hidden' }}>
        {/* Left panel */}
        <div className="flex-1 overflow-y-auto px-8 py-7">
          <div className="max-w-2xl">
            <div className="mb-7">
              <h1 className="text-2xl font-bold text-white">Study Planner</h1>
              <p className="text-sm text-gray-500 mt-1">
                Generate a personalised day-by-day schedule · Click a day to view study resources
              </p>
            </div>

            {/* Generator */}
            <div className="card p-6 mb-7">
              <h2 className="text-sm font-semibold text-white mb-5">
                Generate Plan
              </h2>

              <div className="mb-4">
                <label className="block text-xs font-medium text-gray-400 mb-1.5">
                  Interview Target Date
                </label>
                <input
                  type="date"
                  min={today}
                  value={targetDate}
                  onChange={(e) => setTargetDate(e.target.value)}
                  className="input-base w-auto"
                />
              </div>

              <div className="mb-5">
                <label className="block text-xs font-medium text-gray-400 mb-2">
                  Weak Topics{' '}
                  <span className="text-gray-600 font-normal">(prioritised first in schedule)</span>
                </label>
                <div className="flex flex-wrap gap-1.5">
                  {TOPICS.map((t) => (
                    <button
                      key={t}
                      onClick={() => toggleTopic(t)}
                      className={`px-3 py-1 rounded-full text-xs font-medium transition ${
                        weakTopics.includes(t)
                          ? 'bg-indigo-600 text-white'
                          : 'bg-white/5 border border-white/10 text-gray-400 hover:text-white hover:bg-white/8'
                      }`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>

              <button
                onClick={() => generateMutation.mutate()}
                disabled={!targetDate || generateMutation.isPending}
                className="btn-primary disabled:opacity-40"
              >
                {generateMutation.isPending ? 'Generating…' : 'Generate Plan'}
              </button>
            </div>

            {/* Active plan */}
            {isLoading ? (
              <div className="space-y-2">
                {Array.from({ length: 5 }).map((_, i) => (
                  <div key={i} className="card h-16 animate-pulse" />
                ))}
              </div>
            ) : plan ? (
              <div>
                <div className="flex items-center gap-3 mb-4">
                  <Calendar size={15} className="text-indigo-400" />
                  <span className="text-sm text-gray-400">
                    Target: <strong className="text-white">{plan.targetDate}</strong>
                  </span>
                  <span className="text-xs text-gray-600">·</span>
                  <span className="text-xs text-gray-500">{completedDays}/{totalDays} days done</span>
                  <div className="flex-1 h-1 bg-white/5 rounded-full overflow-hidden ml-2">
                    <div
                      className="h-full bg-indigo-500 rounded-full"
                      style={{ width: totalDays ? `${(completedDays / totalDays) * 100}%` : '0%' }}
                    />
                  </div>
                </div>

                <div className="space-y-1.5">
                  {plan.days?.slice(0, 60).map((day: PlanDay) => (
                    <div
                      key={day.id}
                      onClick={() => setSelectedDay(selectedDay?.id === day.id ? null : day)}
                      className={`flex items-center gap-4 card px-5 py-3.5 cursor-pointer transition-all ${
                        selectedDay?.id === day.id
                          ? 'ring-glow border-indigo-500/30 bg-indigo-500/8'
                          : day.isCompleted
                          ? 'opacity-50'
                          : 'hover:bg-white/5 hover:border-white/12'
                      }`}
                    >
                      <div className="shrink-0 w-14 text-right">
                        <div className="text-xs font-bold text-gray-400">Day {day.dayNumber}</div>
                        <div className="text-[10px] text-gray-600">{day.scheduledDate}</div>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="text-sm font-medium text-white">{day.topic}</div>
                        {day.subtopic && (
                          <div className="text-xs text-gray-500 mt-0.5">{day.subtopic}</div>
                        )}
                        <div className="text-[10px] text-gray-700 mt-0.5">
                          {day.problemsCount} problems · {day.flashcardsCount} cards
                        </div>
                      </div>
                      <button
                        onClick={(e) => {
                          e.stopPropagation()
                          completeMutation.mutate(day.id)
                        }}
                        disabled={day.isCompleted || completeMutation.isPending}
                        className={`shrink-0 p-1.5 rounded-lg transition ${
                          day.isCompleted
                            ? 'text-emerald-500 cursor-default'
                            : 'text-gray-600 hover:text-emerald-400 hover:bg-emerald-500/10'
                        }`}
                      >
                        <CheckCircle2 size={18} />
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="card p-8 text-center">
                <p className="text-gray-500 text-sm">No active plan. Generate one above.</p>
              </div>
            )}
          </div>
        </div>

        {/* Resource panel */}
        {selectedDay && (
          <div
            className="w-[380px] shrink-0 border-l flex flex-col overflow-hidden"
            style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
          >
            <div
              className="sticky top-0 px-5 py-4 border-b flex items-start justify-between gap-2 z-10"
              style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
            >
              <div>
                <h3 className="font-semibold text-white text-sm">{selectedDay.topic}</h3>
                <p className="text-xs text-gray-500 mt-0.5">Resources for Day {selectedDay.dayNumber}</p>
              </div>
              <button
                onClick={() => setSelectedDay(null)}
                className="btn-ghost p-1.5 -mr-1"
              >
                <X size={16} />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-2.5">
              {selectedDay.notes && (
                <div className="bg-indigo-500/10 border border-indigo-500/20 rounded-xl p-3.5 text-xs text-indigo-300 leading-relaxed">
                  {selectedDay.notes}
                </div>
              )}

              {resourcesLoading ? (
                <div className="space-y-2 pt-2">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <div key={i} className="card-elevated h-20 animate-pulse rounded-xl" />
                  ))}
                </div>
              ) : dayResources && dayResources.length > 0 ? (
                dayResources.map((r) => {
                  const Icon = TYPE_ICONS[r.type] ?? BookOpen
                  return (
                    <a
                      key={r.id}
                      href={r.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="group flex items-start gap-3 card-elevated hover:border-white/12 rounded-xl p-3.5 transition-all"
                    >
                      <Icon size={14} className="shrink-0 mt-0.5 text-indigo-400" />
                      <div className="flex-1 min-w-0">
                        <div className="text-xs font-semibold text-gray-200 group-hover:text-white transition leading-tight mb-0.5">
                          {r.title}
                        </div>
                        <div className="text-[10px] text-indigo-400/80 mb-1">{r.provider}</div>
                        <div className="text-[11px] text-gray-500 leading-relaxed line-clamp-2">
                          {r.description}
                        </div>
                        <div className="flex items-center gap-2 mt-1.5">
                          {r.duration && (
                            <span className="text-[10px] text-gray-700 flex items-center gap-1">
                              <Clock size={11} /> {r.duration}
                            </span>
                          )}
                          {!r.isFree && (
                            <span className="text-[9px] px-1.5 py-0.5 bg-amber-500/10 text-amber-400 border border-amber-500/20 rounded font-medium">
                              Prosus
                            </span>
                          )}
                        </div>
                      </div>
                      <ExternalLink size={12} className="text-gray-700 group-hover:text-indigo-400 transition shrink-0 mt-0.5" />
                    </a>
                  )
                })
              ) : (
                <div className="text-center py-10">
                  <p className="text-gray-500 text-xs">No resources for this topic.</p>
                  <a href="/resources" className="text-indigo-400 hover:text-indigo-300 text-xs mt-2 inline-flex items-center gap-1 transition">
                    Browse all resources <ArrowRight size={12} />
                  </a>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </AppShell>
  )
}
