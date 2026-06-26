import Link from 'next/link'
import { Zap, Brain, Bot, Calendar, Library, BarChart2 } from 'lucide-react'

const features = [
  {
    icon: Zap,
    title: 'DSA Problem Bank',
    desc: '50+ curated problems with Monaco editor, hints, and full solutions',
    gradient: 'from-blue-500/20 to-indigo-500/20',
    border: 'border-blue-500/20',
  },
  {
    icon: Brain,
    title: 'SM-2 Flashcards',
    desc: '200+ cards with spaced repetition — Java, Spring Boot, AWS, System Design',
    gradient: 'from-purple-500/20 to-pink-500/20',
    border: 'border-purple-500/20',
  },
  {
    icon: Bot,
    title: 'AI Mock Interviews',
    desc: 'Timed sessions with GPT-4o feedback on correctness, complexity & quality',
    gradient: 'from-emerald-500/20 to-teal-500/20',
    border: 'border-emerald-500/20',
  },
  {
    icon: Calendar,
    title: 'Study Planner',
    desc: 'Target-date schedule with weak-topic prioritisation and daily goals',
    gradient: 'from-amber-500/20 to-orange-500/20',
    border: 'border-amber-500/20',
  },
  {
    icon: Library,
    title: 'Learning Resources',
    desc: '120+ curated resources: Udemy (Prosus), NeetCode, YouTube, LeetCode',
    gradient: 'from-rose-500/20 to-red-500/20',
    border: 'border-rose-500/20',
  },
  {
    icon: BarChart2,
    title: 'Progress Dashboard',
    desc: 'Streak tracker, activity heatmap, topic breakdown — all in one view',
    gradient: 'from-cyan-500/20 to-blue-500/20',
    border: 'border-cyan-500/20',
  },
]

const stack = ['Java 21', 'Spring Boot 3', 'PostgreSQL', 'Redis', 'Next.js 14', 'GPT-4o']

export default function HomePage() {
  return (
    <main className="min-h-screen" style={{ background: 'var(--bg-primary)' }}>
      {/* Nav */}
      <nav className="flex items-center justify-between px-8 py-5 border-b border-white/5">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-lg bg-indigo-500 flex items-center justify-center text-sm font-bold">
            IP
          </div>
          <span className="font-semibold text-white tracking-tight">InterviewPrep</span>
        </div>
        <div className="flex items-center gap-2">
          <Link href="/auth/login" className="btn-ghost text-sm">Sign In</Link>
          <Link
            href="/auth/register"
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-lg transition"
          >
            Get Started
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative px-8 pt-24 pb-20 text-center overflow-hidden">
        {/* Background glow */}
        <div className="absolute inset-0 pointer-events-none">
          <div className="absolute top-20 left-1/2 -translate-x-1/2 w-[600px] h-[400px] bg-indigo-600/10 rounded-full blur-[120px]" />
          <div className="absolute top-40 left-1/4 w-[300px] h-[300px] bg-blue-600/8 rounded-full blur-[100px]" />
          <div className="absolute top-40 right-1/4 w-[300px] h-[300px] bg-purple-600/8 rounded-full blur-[100px]" />
        </div>

        <div className="relative max-w-4xl mx-auto">
          <div className="inline-flex items-center gap-2 bg-indigo-500/10 border border-indigo-500/20 rounded-full px-4 py-1.5 text-sm text-indigo-300 mb-8">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
            Built for Finance Backend Engineers
          </div>

          <h1 className="text-6xl font-bold text-white mb-6 leading-[1.08] tracking-tight">
            Land your next{' '}
            <span className="gradient-text">backend role</span>
          </h1>

          <p className="text-xl text-gray-400 max-w-2xl mx-auto mb-10 leading-relaxed">
            A complete interview preparation platform — DSA practice, spaced repetition flashcards,
            AI-powered mock interviews, system design whiteboard, and a personalised study planner.
          </p>

          <div className="flex items-center justify-center gap-3 mb-16">
            <Link
              href="/auth/register"
              className="px-7 py-3.5 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-xl transition text-sm shadow-lg shadow-indigo-500/20"
            >
              Start for free
            </Link>
            <Link
              href="/auth/login"
              className="px-7 py-3.5 bg-white/5 hover:bg-white/10 border border-white/10 hover:border-white/20 text-gray-300 font-semibold rounded-xl transition text-sm"
            >
              Sign in
            </Link>
          </div>

          {/* Tech stack pills */}
          <div className="flex items-center justify-center gap-2 flex-wrap">
            <span className="text-xs text-gray-600 mr-1">Built with</span>
            {stack.map((s) => (
              <span key={s} className="px-2.5 py-1 bg-white/5 border border-white/8 rounded-lg text-xs text-gray-400">
                {s}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="px-8 pb-24 max-w-5xl mx-auto">
        <div className="text-center mb-12">
          <h2 className="text-2xl font-bold text-white mb-3">Everything you need to prepare</h2>
          <p className="text-gray-500 text-sm">Six integrated modules, one focused platform</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {features.map((f) => {
            const Icon = f.icon
            return (
              <div
                key={f.title}
                className={`relative rounded-2xl p-6 border ${f.border} bg-gradient-to-br ${f.gradient} backdrop-blur`}
              >
                <div className="mb-4 text-indigo-400">
                  <Icon size={28} strokeWidth={1.5} />
                </div>
                <h3 className="font-semibold text-white mb-2 text-sm">{f.title}</h3>
                <p className="text-gray-400 text-xs leading-relaxed">{f.desc}</p>
              </div>
            )
          })}
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-white/5 px-8 py-6 text-center text-xs text-gray-600">
        InterviewPrep · Finance Backend Engineer Edition · {new Date().getFullYear()}
      </footer>
    </main>
  )
}
