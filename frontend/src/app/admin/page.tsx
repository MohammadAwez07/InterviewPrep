'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState, useEffect } from 'react'
import { Zap, RefreshCw, CheckCircle, AlertCircle, Clock, Code2, BookOpen, BarChart3 } from 'lucide-react'

interface Stats {
  totalProblems: number
  withApproaches: number
  withoutApproaches: number
  generatorRunning: boolean
  generatorProgress: number
  generatorTotal: number
  lastError: string
}

export default function AdminPage() {
  const [pollInterval, setPollInterval] = useState(0)

  const { data: stats, refetch } = useQuery<Stats>({
    queryKey: ['admin-stats'],
    queryFn: () => api.get('/admin/solution-stats').then(r => r.data.data),
    refetchInterval: pollInterval,
  })

  // Auto-poll while generator is running
  useEffect(() => {
    setPollInterval(stats?.generatorRunning ? 2000 : 0)
  }, [stats?.generatorRunning])

  const generateMutation = useMutation({
    mutationFn: () => api.post('/admin/generate-solutions'),
    onSuccess: () => {
      setPollInterval(2000)
      refetch()
    },
  })

  const covered = stats?.withApproaches ?? 0
  const total = stats?.totalProblems ?? 0
  const pct = total > 0 ? Math.round((covered / total) * 100) : 0

  const genProgress = stats?.generatorTotal
    ? Math.round((stats.generatorProgress / stats.generatorTotal) * 100)
    : 0

  return (
    <AppShell>
      <div className="max-w-2xl mx-auto px-6 py-10">

        <div className="mb-8">
          <h1 className="text-2xl font-bold text-white mb-1">Solution Generator</h1>
          <p className="text-sm text-gray-500">
            Uses GPT-4o to automatically generate Brute Force, Better, and Optimal solutions for every problem that doesn't have one yet.
          </p>
        </div>

        {/* Coverage card */}
        <div className="bg-white/[0.03] rounded-2xl border border-white/8 p-6 mb-5">
          <p className="text-[10px] uppercase tracking-widest text-gray-600 font-semibold mb-4">Coverage</p>
          <div className="flex items-end gap-4 mb-4">
            <div>
              <p className="text-4xl font-bold text-white">{pct}%</p>
              <p className="text-xs text-gray-500 mt-1">{covered} / {total} problems have solutions</p>
            </div>
            <div className="flex-1 grid grid-cols-2 gap-3">
              <div className="bg-emerald-500/10 rounded-xl p-3 border border-emerald-500/15">
                <p className="text-xl font-bold text-emerald-400">{covered}</p>
                <p className="text-[10px] text-emerald-600 flex items-center gap-1 mt-0.5">
                  <CheckCircle size={10} /> With solutions
                </p>
              </div>
              <div className="bg-amber-500/10 rounded-xl p-3 border border-amber-500/15">
                <p className="text-xl font-bold text-amber-400">{stats?.withoutApproaches ?? 0}</p>
                <p className="text-[10px] text-amber-600 flex items-center gap-1 mt-0.5">
                  <AlertCircle size={10} /> Missing
                </p>
              </div>
            </div>
          </div>

          {/* Coverage progress bar */}
          <div className="h-2 bg-white/5 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-indigo-500 to-emerald-500 rounded-full transition-all duration-700"
              style={{ width: `${pct}%` }}
            />
          </div>
        </div>

        {/* Generator status */}
        {stats?.generatorRunning && (
          <div className="bg-indigo-500/10 rounded-2xl border border-indigo-500/20 p-5 mb-5">
            <div className="flex items-center gap-2 mb-3">
              <RefreshCw size={14} className="text-indigo-400 animate-spin" />
              <p className="text-sm font-semibold text-indigo-300">Generating solutions…</p>
              <span className="ml-auto text-xs text-indigo-400 font-mono">
                {stats.generatorProgress} / {stats.generatorTotal}
              </span>
            </div>
            <div className="h-1.5 bg-indigo-900/40 rounded-full overflow-hidden">
              <div
                className="h-full bg-indigo-400 rounded-full transition-all duration-500"
                style={{ width: `${genProgress}%` }}
              />
            </div>
            <p className="text-[10px] text-indigo-600 mt-2">
              ~1 request/second to respect OpenAI rate limits. This will take ~{Math.ceil((stats.generatorTotal - stats.generatorProgress) / 60)} minute(s).
            </p>
          </div>
        )}

        {stats?.lastError && (
          <div className="bg-red-500/10 border border-red-500/20 rounded-xl p-4 mb-5 text-xs text-red-400">
            <span className="font-semibold">Last error: </span>{stats.lastError}
          </div>
        )}

        {/* Generate button */}
        <button
          onClick={() => generateMutation.mutate()}
          disabled={stats?.generatorRunning || generateMutation.isPending}
          className="w-full flex items-center justify-center gap-2.5 px-6 py-4 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm transition disabled:opacity-50 disabled:cursor-not-allowed mb-5"
        >
          {stats?.generatorRunning ? (
            <><RefreshCw size={16} className="animate-spin" /> Generating…</>
          ) : (
            <><Zap size={16} /> Generate Solutions for All {stats?.withoutApproaches ?? '…'} Missing Problems</>
          )}
        </button>

        {/* How it works */}
        <div className="bg-white/[0.02] rounded-2xl border border-white/5 p-5">
          <p className="text-[10px] uppercase tracking-widest text-gray-600 font-semibold mb-4">How it works</p>
          <ol className="space-y-3">
            {[
              { icon: BarChart3, text: 'Finds every active problem that has no solution approaches in the database.' },
              { icon: Code2,    text: 'For each problem, sends the title, description, topic, and pattern tags to GPT-4o.' },
              { icon: BookOpen, text: 'GPT-4o returns a structured JSON array with Brute Force, Better (when distinct), and Optimal approaches — each with Java code, time/space complexity, intuition, and a step-by-step explanation.' },
              { icon: CheckCircle, text: 'Solutions are saved to the database and immediately available in the problem detail page.' },
              { icon: Clock,    text: 'Rate-limited to 1 request/second to stay within OpenAI API limits.' },
            ].map(({ icon: Icon, text }, i) => (
              <li key={i} className="flex gap-3">
                <span className="flex items-center justify-center w-5 h-5 rounded-full bg-indigo-500/15 text-indigo-400 text-[10px] font-bold shrink-0 mt-0.5">
                  {i + 1}
                </span>
                <p className="text-xs text-gray-400 leading-relaxed">{text}</p>
              </li>
            ))}
          </ol>
        </div>

      </div>
    </AppShell>
  )
}
