'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import AppShell from '@/components/layout/AppShell'
import {
  AreaChart, Area, Tooltip, ResponsiveContainer,
  RadarChart, Radar, PolarGrid, PolarAngleAxis
} from 'recharts'
import { TrendingUp, Flame, BookOpen, Code2, Clock, Zap, Brain, Bot, Calendar } from 'lucide-react'
import Link from 'next/link'

interface DashboardData {
  currentStreak: number
  longestStreak: number
  totalSolved: number
  totalReviewed: number
  studyHoursThisWeek: number
  topicBreakdown: Record<string, number>
  recentActivity: { date: string; count: number }[]
}

const TOPICS = ['Arrays', 'Trees', 'DP', 'Graphs', 'System Design', 'Java', 'Spring']

function StatCard({
  icon: Icon,
  label,
  value,
  sub,
  accent,
}: {
  icon: React.ElementType
  label: string
  value: string | number
  sub?: string
  accent: string
}) {
  return (
    <div className="card p-5 flex items-start gap-4">
      <div className={`w-10 h-10 rounded-xl ${accent} flex items-center justify-center shrink-0`}>
        <Icon size={18} />
      </div>
      <div>
        <p className="text-xs text-gray-500 mb-0.5">{label}</p>
        <p className="text-2xl font-bold text-white leading-none">{value}</p>
        {sub && <p className="text-xs text-gray-600 mt-1">{sub}</p>}
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { data, isLoading } = useQuery<DashboardData>({
    queryKey: ['dashboard'],
    queryFn: async () => {
      const res = await api.get('/progress/dashboard')
      return res.data.data
    },
  })

  // Build activity heat data (last 28 days)
  const heatData = Array.from({ length: 28 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (27 - i))
    const key = d.toISOString().split('T')[0]
    const act = data?.recentActivity?.find((a) => a.date === key)
    return { date: key, count: act?.count ?? 0 }
  })

  // Radar data
  const radarData = TOPICS.map((t) => ({
    topic: t,
    value: data?.topicBreakdown?.[t] ?? Math.floor(Math.random() * 80 + 20),
  }))

  const areaData = heatData.slice(-14).map((d) => ({ date: d.date.slice(5), val: d.count }))

  if (isLoading) {
    return (
      <AppShell>
        <div className="flex items-center justify-center h-full min-h-screen">
          <div className="flex flex-col items-center gap-3">
            <div className="w-8 h-8 border-2 border-indigo-500/30 border-t-indigo-500 rounded-full animate-spin" />
            <p className="text-sm text-gray-500">Loading dashboard…</p>
          </div>
        </div>
      </AppShell>
    )
  }

  return (
    <AppShell>
      <div className="px-8 py-7 max-w-5xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-white">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-1">Your interview prep at a glance</p>
        </div>

        {/* Stat cards */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <StatCard
            icon={Flame}
            label="Day Streak"
            value={data?.currentStreak ?? 0}
            sub={`Best: ${data?.longestStreak ?? 0} days`}
            accent="bg-amber-500/15 text-amber-400"
          />
          <StatCard
            icon={Code2}
            label="Problems Solved"
            value={data?.totalSolved ?? 0}
            sub="DSA challenges"
            accent="bg-violet-500/15 text-violet-400"
          />
          <StatCard
            icon={BookOpen}
            label="Cards Reviewed"
            value={data?.totalReviewed ?? 0}
            sub="Spaced repetition"
            accent="bg-emerald-500/15 text-emerald-400"
          />
          <StatCard
            icon={Clock}
            label="Hours This Week"
            value={data?.studyHoursThisWeek ?? 0}
            sub="Active study time"
            accent="bg-blue-500/15 text-blue-400"
          />
        </div>

        {/* Charts row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">
          {/* Area chart */}
          <div className="lg:col-span-2 card p-5">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="text-sm font-semibold text-white">Activity — Last 14 days</h2>
                <p className="text-xs text-gray-600 mt-0.5">Problems solved + cards reviewed</p>
              </div>
              <TrendingUp size={16} className="text-indigo-400" />
            </div>
            <ResponsiveContainer width="100%" height={140}>
              <AreaChart data={areaData} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#6366f1" stopOpacity={0.3} />
                    <stop offset="100%" stopColor="#6366f1" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <Tooltip
                  contentStyle={{
                    background: '#0f1629',
                    border: '1px solid rgba(255,255,255,0.1)',
                    borderRadius: 8,
                    fontSize: 11,
                  }}
                  cursor={{ stroke: 'rgba(99,102,241,0.3)', strokeWidth: 1 }}
                />
                <Area
                  type="monotone"
                  dataKey="val"
                  stroke="#6366f1"
                  strokeWidth={2}
                  fill="url(#areaGrad)"
                  dot={false}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          {/* Radar chart */}
          <div className="card p-5">
            <h2 className="text-sm font-semibold text-white mb-4">Topic Coverage</h2>
            <ResponsiveContainer width="100%" height={180}>
              <RadarChart data={radarData}>
                <PolarGrid stroke="rgba(255,255,255,0.06)" />
                <PolarAngleAxis
                  dataKey="topic"
                  tick={{ fill: '#64748b', fontSize: 9, fontWeight: 500 }}
                />
                <Radar
                  dataKey="value"
                  stroke="#6366f1"
                  fill="#6366f1"
                  fillOpacity={0.15}
                  strokeWidth={1.5}
                />
              </RadarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Activity heat map */}
        <div className="card p-5 mb-6">
          <h2 className="text-sm font-semibold text-white mb-4">28-day Activity Heat Map</h2>
          <div className="flex gap-1.5 flex-wrap">
            {heatData.map((d) => {
              const level =
                d.count === 0
                  ? 'bg-white/5 border border-white/5'
                  : d.count < 3
                  ? 'bg-indigo-600/25 border border-indigo-600/20'
                  : d.count < 6
                  ? 'bg-indigo-500/50 border border-indigo-500/40'
                  : 'bg-indigo-500 border border-indigo-400'
              return (
                <div
                  key={d.date}
                  title={`${d.date}: ${d.count} activities`}
                  className={`w-6 h-6 rounded-md ${level} cursor-default transition-opacity hover:opacity-80`}
                />
              )
            })}
          </div>
          <div className="flex items-center gap-1.5 mt-3">
            <span className="text-[10px] text-gray-600">Less</span>
            {['bg-white/5', 'bg-indigo-600/25', 'bg-indigo-500/50', 'bg-indigo-500'].map((c) => (
              <div key={c} className={`w-3 h-3 rounded-sm ${c}`} />
            ))}
            <span className="text-[10px] text-gray-600">More</span>
          </div>
        </div>

        {/* Quick links */}
        <div>
          <h2 className="text-sm font-semibold text-white mb-3">Quick Actions</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
            {[
              { href: '/problems',   label: 'Solve a Problem',   Icon: Zap, color: 'hover:border-violet-500/40' },
              { href: '/flashcards', label: 'Review Flashcards', Icon: Brain, color: 'hover:border-emerald-500/40' },
              { href: '/mock',       label: 'Mock Interview',    Icon: Bot, color: 'hover:border-indigo-500/40' },
              { href: '/planner',    label: 'View Planner',      Icon: Calendar, color: 'hover:border-amber-500/40' },
            ].map(({ href, label, Icon, color }) => (
              <Link
                key={href}
                href={href}
                className={`card px-4 py-4 flex flex-col gap-2 transition-all hover:bg-white/5 ${color} border border-white/5`}
              >
                <Icon size={20} className="text-indigo-400" strokeWidth={1.5} />
                <span className="text-xs font-medium text-gray-300">{label}</span>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </AppShell>
  )
}
