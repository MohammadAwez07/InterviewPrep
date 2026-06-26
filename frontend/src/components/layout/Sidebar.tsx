'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useEffect, useState } from 'react'
import { cn } from '@/lib/utils'
import { logout, getUser } from '@/lib/auth'
import {
  LayoutDashboard, Code2, BookOpen, Calendar, Library,
  Pen, Bot, LogOut, ChevronRight, Zap, Settings2
} from 'lucide-react'

const nav = [
  { href: '/dashboard',  icon: LayoutDashboard, label: 'Dashboard',       color: 'text-blue-400' },
  { href: '/problems',   icon: Code2,            label: 'Problems',        color: 'text-violet-400' },
  { href: '/flashcards', icon: BookOpen,         label: 'Flashcards',      color: 'text-emerald-400' },
  { href: '/planner',    icon: Calendar,         label: 'Study Planner',   color: 'text-amber-400' },
  { href: '/resources',  icon: Library,          label: 'Resources',       color: 'text-rose-400' },
  { href: '/analysis',   icon: Zap,              label: 'Readiness Check', color: 'text-yellow-400' },
  { href: '/design',     icon: Pen,              label: 'System Design',   color: 'text-cyan-400' },
  { href: '/mock',       icon: Bot,              label: 'Mock Interview',  color: 'text-indigo-400' },
  { href: '/admin',      icon: Settings2,        label: 'Admin',           color: 'text-gray-400' },
]

export default function Sidebar() {
  const pathname = usePathname()
  const [user, setUser] = useState<{ fullName?: string; email?: string } | null>(null)
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    setUser(getUser())
  }, [])

  const initials = user?.fullName
    ?.split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2) ?? 'IP'

  return (
    <aside
      className="w-60 shrink-0 flex flex-col border-r"
      style={{ background: 'var(--bg-card)', borderColor: 'var(--border)', minHeight: '100vh' }}
    >
      {/* Logo */}
      <div className="px-5 py-5 border-b flex items-center gap-2.5" style={{ borderColor: 'var(--border)' }}>
        <div className="w-7 h-7 rounded-lg bg-indigo-500 flex items-center justify-center text-xs font-bold text-white shrink-0">
          IP
        </div>
        <span className="font-semibold text-white text-sm tracking-tight">InterviewPrep</span>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-0.5">
        {nav.map(({ href, icon: Icon, label, color }) => {
          const active = pathname.startsWith(href)
          return (
            <Link
              key={href}
              href={href}
              className={cn(
                'group flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
                active
                  ? 'bg-indigo-500/12 text-white ring-glow'
                  : 'text-gray-500 hover:text-gray-200 hover:bg-white/5'
              )}
            >
              <Icon
                size={16}
                className={cn('shrink-0 transition-colors', active ? color : 'text-gray-600 group-hover:text-gray-400')}
              />
              <span className="flex-1">{label}</span>
              {active && <ChevronRight size={13} className="text-gray-600" />}
            </Link>
          )
        })}
      </nav>

      {/* User footer */}
      <div className="px-3 py-4 border-t space-y-1" style={{ borderColor: 'var(--border)' }}>
        <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl">
          <div className="w-7 h-7 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-xs font-bold text-white shrink-0">
            {mounted ? initials : 'IP'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-medium text-white truncate">{mounted ? (user?.fullName ?? 'User') : 'User'}</p>
            <p className="text-[10px] text-gray-600 truncate">{mounted ? user?.email : ''}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="flex items-center gap-3 px-3 py-2 w-full rounded-xl text-xs text-gray-600 hover:text-red-400 hover:bg-red-500/8 transition-all"
        >
          <LogOut size={14} />
          Sign out
        </button>
      </div>
    </aside>
  )
}
