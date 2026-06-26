'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState } from 'react'
import { ExternalLink, BookOpen, Video, Code2, Layers, PlayCircle, Search, Clock } from 'lucide-react'

interface Resource {
  id: string
  topic: string
  title: string
  provider: string
  type: 'VIDEO' | 'COURSE' | 'ARTICLE' | 'PRACTICE' | 'PLAYLIST'
  url: string
  description: string
  duration: string
  isFree: boolean
}

const TYPE_CONFIG: Record<string, { icon: React.ComponentType<{ size: number; className?: string }>; badge: string; label: string }> = {
  VIDEO:    { icon: Video,      badge: 'tag bg-red-500/15 text-red-400 ring-1 ring-red-500/25',       label: 'Video' },
  COURSE:   { icon: Layers,     badge: 'tag bg-purple-500/15 text-purple-400 ring-1 ring-purple-500/25', label: 'Course' },
  ARTICLE:  { icon: BookOpen,   badge: 'tag bg-blue-500/15 text-blue-400 ring-1 ring-blue-500/25',    label: 'Article' },
  PRACTICE: { icon: Code2,      badge: 'tag bg-emerald-500/15 text-emerald-400 ring-1 ring-emerald-500/25', label: 'Practice' },
  PLAYLIST: { icon: PlayCircle, badge: 'tag bg-amber-500/15 text-amber-400 ring-1 ring-amber-500/25', label: 'Playlist' },
}

const TOPIC_ORDER = [
  'Arrays', 'Strings', 'HashMaps & Sets', 'Two Pointers', 'Sliding Window',
  'Linked Lists', 'Stacks & Queues', 'Binary Search', 'Trees - BFS/DFS',
  'Recursion', 'Backtracking', 'Graphs - BFS/DFS', 'Heaps', 'Greedy',
  'Dynamic Programming - 1D', 'Dynamic Programming - 2D', 'Dynamic Programming - Advanced',
  'Tries', 'Java Core', 'Spring Boot', 'AWS & Cloud', 'System Design', 'Mock Interview Week',
]

export default function ResourcesPage() {
  const [activeTopic, setActiveTopic] = useState<string | null>(null)
  const [typeFilter, setTypeFilter] = useState<string>('ALL')
  const [search, setSearch] = useState('')

  const { data: grouped, isLoading } = useQuery<Record<string, Resource[]>>({
    queryKey: ['resources-all'],
    queryFn: () => api.get('/resources').then((r) => r.data.data),
  })

  const allTopics = grouped ? TOPIC_ORDER.filter((t) => t in grouped) : []
  const displayTopic = activeTopic ?? allTopics[0]
  const rawList = grouped?.[displayTopic] ?? []
  const list = rawList
    .filter((r) => typeFilter === 'ALL' || r.type === typeFilter)
    .filter((r) =>
      !search ||
      r.title.toLowerCase().includes(search.toLowerCase()) ||
      r.provider.toLowerCase().includes(search.toLowerCase())
    )

  return (
    <AppShell>
      <div className="flex" style={{ height: 'calc(100vh)', overflow: 'hidden' }}>
        {/* Topic sidebar */}
        <div
          className="w-52 shrink-0 border-r flex flex-col"
          style={{ background: 'var(--bg-card)', borderColor: 'var(--border)' }}
        >
          <div className="px-4 py-4 border-b" style={{ borderColor: 'var(--border)' }}>
            <p className="text-[10px] font-semibold text-gray-600 uppercase tracking-widest">Topics</p>
          </div>
          <nav className="flex-1 overflow-y-auto p-2 space-y-0.5">
            {isLoading
              ? Array.from({ length: 8 }).map((_, i) => (
                  <div key={i} className="h-7 bg-white/5 rounded-lg animate-pulse mx-1 mb-1" />
                ))
              : allTopics.map((t) => (
                  <button
                    key={t}
                    onClick={() => { setActiveTopic(t); setTypeFilter('ALL'); setSearch('') }}
                    className={`w-full text-left px-3 py-2 rounded-lg text-xs transition-all ${
                      displayTopic === t
                        ? 'bg-indigo-500/12 text-indigo-300 font-medium ring-glow'
                        : 'text-gray-500 hover:text-gray-300 hover:bg-white/5'
                    }`}
                  >
                    {t}
                    <span className="ml-1 text-gray-700 text-[10px]">
                      ({grouped?.[t]?.length ?? 0})
                    </span>
                  </button>
                ))}
          </nav>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto px-8 py-7">
          {/* Header */}
          <div className="flex items-start justify-between mb-5">
            <div>
              <h1 className="text-xl font-bold text-white">{displayTopic}</h1>
              <p className="text-sm text-gray-500 mt-0.5">{list.length} resources</p>
            </div>
          </div>

          {/* Filters */}
          <div className="flex flex-wrap gap-2 mb-6">
            <div className="relative">
              <Search size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-600" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search…"
                className="input-base pl-8 w-44 py-2"
              />
            </div>
            <div className="flex gap-1.5">
              {['ALL', 'VIDEO', 'COURSE', 'ARTICLE', 'PRACTICE', 'PLAYLIST'].map((t) => (
                <button
                  key={t}
                  onClick={() => setTypeFilter(t)}
                  className={`px-3 py-2 text-xs font-medium rounded-lg transition ${
                    typeFilter === t
                      ? 'bg-indigo-600 text-white'
                      : 'bg-white/5 border border-white/10 text-gray-400 hover:text-white'
                  }`}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>

          {/* Cards */}
          {isLoading ? (
            <div className="space-y-3">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="card h-24 animate-pulse" />
              ))}
            </div>
          ) : (
            <div className="space-y-2.5">
              {list.map((r) => <ResourceCard key={r.id} resource={r} />)}
              {list.length === 0 && (
                <div className="text-center py-12 text-gray-500 text-sm">
                  No resources match this filter
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </AppShell>
  )
}

function ResourceCard({ resource: r }: { resource: Resource }) {
  const { icon: Icon, badge, label } = TYPE_CONFIG[r.type] ?? TYPE_CONFIG.ARTICLE

  return (
    <a
      href={r.url}
      target="_blank"
      rel="noopener noreferrer"
      className="group flex items-start gap-4 card px-5 py-4 hover:bg-white/5 hover:border-white/12 transition-all"
    >
      <div className={`shrink-0 flex items-center gap-1.5 px-2 py-1 rounded-lg text-xs font-medium mt-0.5 ${badge}`}>
        <Icon size={11} />
        {label}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-0.5 flex-wrap">
          <h3 className="text-sm font-semibold text-gray-200 group-hover:text-white transition leading-tight">
            {r.title}
          </h3>
          {!r.isFree && (
            <span className="shrink-0 px-1.5 py-0.5 bg-amber-500/10 text-amber-400 text-[9px] rounded border border-amber-500/20 font-medium">
              Prosus / Udemy
            </span>
          )}
        </div>
        <p className="text-xs text-indigo-400/80 mb-1.5">{r.provider}</p>
        <p className="text-xs text-gray-500 leading-relaxed line-clamp-2">{r.description}</p>
        {r.duration && (
          <p className="text-[10px] text-gray-700 mt-1.5 flex items-center gap-1">
            <Clock size={11} /> {r.duration}
          </p>
        )}
      </div>

      <ExternalLink size={14} className="text-gray-700 group-hover:text-indigo-400 transition shrink-0 mt-1" />
    </a>
  )
}
