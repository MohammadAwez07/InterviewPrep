'use client'

import { useState, useRef, useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import AppShell from '@/components/layout/AppShell'
import { RadarChart, PolarGrid, PolarAngleAxis, Radar, ResponsiveContainer } from 'recharts'
import { format } from 'date-fns'
import {
  CheckCircle,
  AlertCircle,
  Clock,
  FileText,
  ChevronLeft,
  ChevronRight,
  Printer,
  Copy,
  ArrowRight,
  BarChart3,
  FileEdit,
  Sparkles,
  Loader2,
  Calendar,
  Zap,
} from 'lucide-react'

// Types
interface SkillReadiness {
  skill: string
  category: string
  required: boolean
  userScore: number
  status: 'STRONG' | 'GAP' | 'MISSING'
}

interface ResumeSection {
  type: 'SUMMARY' | 'EXPERIENCE' | 'SKILLS' | 'EDUCATION' | 'CERTIFICATIONS' | 'PROJECTS'
  title: string
  content: any
}

interface GapAnalysis {
  id: string
  readinessScore: number
  skillBreakdown: SkillReadiness[]
  strongAreas: string[]
  gapAreas: string[]
  recommendations: string[]
  suggestedTopics: string[]
  tailoredResumeSections: ResumeSection[]
  resumeChanges: string[]
  cached: boolean
  jobTitle: string
  company: string
}

interface AnalysisHistoryItem {
  id: string
  jobTitle: string
  company: string
  readinessScore: number
  hasResumeTailoring: boolean
  createdAt: string
}

const RADAR_CATEGORIES = ['Java', 'Spring', 'AWS', 'DSA', 'System Design', 'Databases']

function CircularProgress({ score }: { score: number }) {
  const radius = 60
  const stroke = 8
  const normalizedRadius = radius - stroke * 2
  const circumference = normalizedRadius * 2 * Math.PI
  const strokeDashoffset = circumference - (score / 100) * circumference

  return (
    <div className="flex flex-col items-center">
      <svg height={radius * 2} width={radius * 2} className="rotate-[-90deg]">
        <circle
          stroke="rgba(99,102,241,0.15)"
          strokeWidth={stroke}
          fill="transparent"
          r={normalizedRadius}
          cx={radius}
          cy={radius}
        />
        <circle
          stroke={score >= 70 ? '#10b981' : score >= 50 ? '#f59e0b' : '#ef4444'}
          strokeWidth={stroke}
          strokeDasharray={circumference + ' ' + circumference}
          style={{ strokeDashoffset, transition: 'stroke-dashoffset 0.5s ease-in-out' }}
          strokeLinecap="round"
          fill="transparent"
          r={normalizedRadius}
          cx={radius}
          cy={radius}
        />
      </svg>
      <div className="text-4xl font-bold text-white mt-[-80px]">{score}</div>
      <div className="text-xs text-gray-600 mt-1">/ 100</div>
      <div className="text-[10px] uppercase tracking-widest text-gray-600 mt-3">Readiness Score</div>
    </div>
  )
}

function ResumeSectionView({ section }: { section: ResumeSection }) {
  switch (section.type) {
    case 'SUMMARY':
      return (
        <div className="mb-5">
          <p className="text-sm text-gray-700 leading-relaxed italic">{section.content?.text}</p>
        </div>
      )
    case 'EXPERIENCE':
      return (
        <div className="mb-5">
          <div className="flex items-baseline justify-between mb-2">
            <h4 className="font-semibold text-gray-900">{section.content?.role}</h4>
            <span className="text-xs text-gray-600">{section.content?.dates}</span>
          </div>
          <p className="text-sm text-gray-700 mb-2">{section.content?.company}</p>
          <ul className="space-y-1">
            {section.content?.bullets?.map((bullet: string, i: number) => (
              <li key={i} className="text-sm text-gray-700 flex gap-2">
                <span className="text-gray-500">•</span>
                {bullet}
              </li>
            ))}
          </ul>
        </div>
      )
    case 'SKILLS':
      return (
        <div className="mb-5">
          <div className="space-y-2">
            {section.content?.categories?.map((cat: any, i: number) => (
              <div key={i}>
                <span className="text-xs text-gray-600 font-medium uppercase tracking-wider">{cat.name}: </span>
                <span className="text-sm text-gray-800">{cat.items?.join(', ')}</span>
              </div>
            ))}
          </div>
        </div>
      )
    case 'EDUCATION':
      return (
        <div className="mb-5">
          <div className="flex items-baseline justify-between mb-1">
            <h4 className="font-semibold text-gray-900">{section.content?.institution}</h4>
            <span className="text-xs text-gray-600">{section.content?.dates}</span>
          </div>
          <p className="text-sm text-gray-700">{section.content?.degree}</p>
          {section.content?.details && <p className="text-sm text-gray-600 mt-1">{section.content?.details}</p>}
        </div>
      )
    case 'CERTIFICATIONS':
      return (
        <div className="mb-5">
          <ul className="space-y-1">
            {section.content?.certifications?.map((cert: any, i: number) => (
              <li key={i} className="text-sm text-gray-700">
                <span className="font-medium">{cert.name}</span>
                <span className="text-gray-600"> — {cert.issuer}</span>
                {cert.date && <span className="text-gray-500 text-xs"> ({cert.date})</span>}
              </li>
            ))}
          </ul>
        </div>
      )
    case 'PROJECTS':
      return (
        <div className="mb-5">
          {section.content?.projects?.map((proj: any, i: number) => (
            <div key={i} className="mb-3">
              <h4 className="font-semibold text-gray-900">{proj.name}</h4>
              <p className="text-sm text-gray-700">{proj.description}</p>
              {proj.technologies?.length > 0 && (
                <p className="text-xs text-gray-600 mt-1">Tech: {proj.technologies.join(', ')}</p>
              )}
            </div>
          ))}
        </div>
      )
    default:
      return null
  }
}

export default function AnalysisPage() {
  const qc = useQueryClient()
  const [activeTab, setActiveTab] = useState<'gap' | 'resume'>('gap')
  const [showResumeInput, setShowResumeInput] = useState(false)
  const [jobTitle, setJobTitle] = useState('')
  const [company, setCompany] = useState('')
  const [jdText, setJdText] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [result, setResult] = useState<GapAnalysis | null>(null)
  const [targetDate, setTargetDate] = useState('')
  const [showDateInput, setShowDateInput] = useState(false)
  const [resumeWarning, setResumeWarning] = useState(false)
  const printRef = useRef<HTMLDivElement>(null)

  const today = format(new Date(), 'yyyy-MM-dd')

  const { data: history } = useQuery<AnalysisHistoryItem[]>({
    queryKey: ['analysis-history'],
    queryFn: () => api.get('/analysis/history').then((r) => r.data.data),
  })

  const analyseMutation = useMutation({
    mutationFn: () =>
      api.post('/analysis/analyse', {
        jobTitle: jobTitle || 'Untitled',
        company: company || 'Unknown',
        jdText,
        resumeText: resumeText || undefined,
      }),
    onSuccess: (res) => {
      setResult(res.data.data)
      qc.invalidateQueries({ queryKey: ['analysis-history'] })
    },
  })

  const generatePlanMutation = useMutation({
    mutationFn: () =>
      api.post('/planner/generate', {
        targetDate,
        weakTopics: result?.suggestedTopics || result?.gapAreas || [],
      }),
    onSuccess: () => {
      window.location.href = '/planner'
    },
  })

  useEffect(() => {
    if (resumeText && resumeText.length < 100 && resumeText.length > 0) {
      setResumeWarning(true)
    } else {
      setResumeWarning(false)
    }
  }, [resumeText])

  const handleAnalyse = () => {
    if (!jdText.trim()) return
    analyseMutation.mutate()
  }

  const copyToClipboard = () => {
    if (!result?.tailoredResumeSections) return
    const text = result.tailoredResumeSections
      .map((s) => {
        let content = ''
        if (s.type === 'SUMMARY') content = s.content?.text || ''
        if (s.type === 'EXPERIENCE') {
          content = `${s.content?.role} at ${s.content?.company}\n${s.content?.bullets?.join('\n') || ''}`
        }
        if (s.type === 'SKILLS') {
          content = s.content?.categories?.map((c: any) => `${c.name}: ${c.items?.join(', ')}`).join('\n') || ''
        }
        return `${s.title}\n${content}`
      })
      .join('\n\n')
    navigator.clipboard.writeText(text)
  }

  const handlePrint = () => {
    window.print()
  }

  // Build radar data
  const radarData = RADAR_CATEGORIES.map((cat) => {
    const skills = result?.skillBreakdown?.filter((s) => s.category === cat) || []
    const avgScore = skills.length > 0
      ? skills.reduce((sum, s) => sum + (s.userScore || 0), 0) / skills.length
      : 0
    return { category: cat, score: Math.round(avgScore) }
  })

  return (
    <AppShell>
      <div className="flex" style={{ height: 'calc(100vh)', overflow: 'hidden' }}>
        {/* Main content */}
        <div className="flex-1 overflow-y-auto px-8 py-7">
          <div className="max-w-3xl mx-auto">
            {/* Header */}
            <div className="mb-8">
              <h1 className="text-2xl font-bold text-white">Interview Readiness Analyser</h1>
              <p className="text-sm text-gray-500 mt-1">
                Paste a job description and your resume to get a tailored gap analysis and resume rewrite
              </p>
            </div>

            {/* Input Form */}
            {!result && (
              <div className="card p-6 mb-6">
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-400 mb-1.5">Job Title</label>
                    <input
                      type="text"
                      value={jobTitle}
                      onChange={(e) => setJobTitle(e.target.value)}
                      className="input-base"
                      placeholder="Senior Backend Engineer"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-400 mb-1.5">Company</label>
                    <input
                      type="text"
                      value={company}
                      onChange={(e) => setCompany(e.target.value)}
                      className="input-base"
                      placeholder="Acme Inc"
                    />
                  </div>
                </div>

                <div className="mb-4">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Job Description</label>
                  <textarea
                    value={jdText}
                    onChange={(e) => setJdText(e.target.value)}
                    className="input-base min-h-[200px] resize-none"
                    placeholder="Paste the full job description here..."
                  />
                </div>

                {!showResumeInput ? (
                  <button
                    onClick={() => setShowResumeInput(true)}
                    className="text-xs text-indigo-400 hover:text-indigo-300 transition mb-4"
                  >
                    Add your resume for tailoring
                  </button>
                ) : (
                  <div className="mb-4">
                    <div className="flex items-center justify-between mb-1.5">
                      <label className="block text-xs font-medium text-gray-400">Your Resume</label>
                      <button
                        onClick={() => setShowResumeInput(false)}
                        className="text-[10px] text-gray-600 hover:text-gray-400 transition"
                      >
                        Hide
                      </button>
                    </div>
                    <textarea
                      value={resumeText}
                      onChange={(e) => setResumeText(e.target.value)}
                      className="input-base min-h-[150px] resize-none"
                      placeholder="Paste your current resume text here..."
                    />
                    {resumeWarning && (
                      <p className="text-xs text-amber-500 mt-2 flex items-center gap-1">
                        <AlertCircle size={12} />
                        Add more detail for better results (experience, skills, technologies used)
                      </p>
                    )}
                  </div>
                )}

                <button
                  onClick={handleAnalyse}
                  disabled={!jdText.trim() || analyseMutation.isPending}
                  className="btn-primary flex items-center gap-2 disabled:opacity-40"
                >
                  {analyseMutation.isPending ? (
                    <>
                      <Loader2 size={16} className="animate-spin" />
                      Analysing with GPT-4o...
                    </>
                  ) : (
                    <>
                      <BarChart3 size={16} />
                      Analyse
                    </>
                  )}
                </button>
              </div>
            )}

            {/* Results */}
            {result && (
              <div className="space-y-6">
                {/* Tabs */}
                <div className="flex gap-2">
                  <button
                    onClick={() => setActiveTab('gap')}
                    className={`px-4 py-2 text-sm font-medium rounded-lg transition ${
                      activeTab === 'gap'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white/5 text-gray-400 hover:text-white'
                    }`}
                  >
                    Gap Analysis
                  </button>
                  {result.tailoredResumeSections?.length > 0 && (
                    <button
                      onClick={() => setActiveTab('resume')}
                      className={`px-4 py-2 text-sm font-medium rounded-lg transition ${
                        activeTab === 'resume'
                          ? 'bg-indigo-600 text-white'
                          : 'bg-white/5 text-gray-400 hover:text-white'
                      }`}
                    >
                      Tailored Resume
                    </button>
                  )}
                </div>

                {/* Gap Analysis Tab */}
                {activeTab === 'gap' && (
                  <div className="space-y-6">
                    {/* Score and Radar */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="card p-6 flex items-center justify-center">
                        <CircularProgress score={result.readinessScore} />
                      </div>
                      <div className="card p-5">
                        <h3 className="text-sm font-semibold text-white mb-4">Skill Coverage</h3>
                        <ResponsiveContainer width="100%" height={180}>
                          <RadarChart data={radarData}>
                            <PolarGrid stroke="rgba(255,255,255,0.06)" />
                            <PolarAngleAxis
                              dataKey="category"
                              tick={{ fill: '#64748b', fontSize: 9, fontWeight: 500 }}
                            />
                            <Radar
                              dataKey="score"
                              stroke="#6366f1"
                              fill="#6366f1"
                              fillOpacity={0.15}
                              strokeWidth={1.5}
                            />
                          </RadarChart>
                        </ResponsiveContainer>
                      </div>
                    </div>

                    {/* Strong and Gap Areas */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="card p-5">
                        <h3 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
                          <CheckCircle size={14} className="text-emerald-400" />
                          Strong Areas
                        </h3>
                        {result.strongAreas?.length > 0 ? (
                          <ul className="space-y-1">
                            {result.strongAreas.map((area, i) => (
                              <li key={i} className="text-sm text-emerald-400 flex items-center gap-2">
                                <ArrowRight size={12} />
                                {area}
                              </li>
                            ))}
                          </ul>
                        ) : (
                          <p className="text-sm text-gray-600">No strong areas identified yet. Keep studying!</p>
                        )}
                      </div>
                      <div className="card p-5">
                        <h3 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
                          <AlertCircle size={14} className="text-red-400" />
                          Gap Areas
                        </h3>
                        {result.gapAreas?.length > 0 ? (
                          <ul className="space-y-1">
                            {result.gapAreas.map((area, i) => (
                              <li key={i} className="text-sm text-red-400 flex items-center gap-2">
                                <ArrowRight size={12} />
                                {area}
                              </li>
                            ))}
                          </ul>
                        ) : (
                          <p className="text-sm text-gray-600">No gaps identified. You are well prepared!</p>
                        )}
                      </div>
                    </div>

                    {/* Recommendations */}
                    <div className="card p-5">
                      <h3 className="text-sm font-semibold text-white mb-3">Recommendations</h3>
                      <ol className="space-y-2">
                        {result.recommendations?.map((rec, i) => (
                          <li key={i} className="text-sm text-gray-400 flex gap-2">
                            <span className="text-gray-600 shrink-0">{i + 1}.</span>
                            {rec}
                          </li>
                        ))}
                      </ol>
                    </div>

                    {/* Generate Smart Plan */}
                    {result.gapAreas?.length > 0 && (
                      <div className="card p-5">
                        <h3 className="text-sm font-semibold text-white mb-3">Create Study Plan</h3>
                        <p className="text-xs text-gray-500 mb-4">
                          Generate a personalized study plan targeting your gap areas
                        </p>
                        {!showDateInput ? (
                          <button
                            onClick={() => setShowDateInput(true)}
                            className="btn-secondary flex items-center gap-2"
                          >
                            <Zap size={14} />
                            Generate Smart Plan
                          </button>
                        ) : (
                          <div className="flex items-center gap-3">
                            <input
                              type="date"
                              min={today}
                              value={targetDate}
                              onChange={(e) => setTargetDate(e.target.value)}
                              className="input-base w-auto"
                            />
                            <button
                              onClick={() => generatePlanMutation.mutate()}
                              disabled={!targetDate || generatePlanMutation.isPending}
                              className="btn-primary disabled:opacity-40"
                            >
                              {generatePlanMutation.isPending ? 'Creating...' : 'Create Plan'}
                            </button>
                            <button
                              onClick={() => setShowDateInput(false)}
                              className="btn-ghost"
                            >
                              Cancel
                            </button>
                          </div>
                        )}
                      </div>
                    )}

                    {/* Start New Analysis */}
                    <button
                      onClick={() => {
                        setResult(null)
                        setActiveTab('gap')
                      }}
                      className="btn-secondary w-full flex items-center justify-center gap-2"
                    >
                      <ChevronLeft size={14} />
                      Start New Analysis
                    </button>
                  </div>
                )}

                {/* Tailored Resume Tab */}
                {activeTab === 'resume' && result.tailoredResumeSections?.length > 0 && (
                  <div className="space-y-6">
                    {/* Toolbar */}
                    <div className="flex items-center gap-2">
                      <button
                        onClick={handlePrint}
                        className="btn-secondary flex items-center gap-2 text-xs"
                      >
                        <Printer size={14} />
                        Download PDF
                      </button>
                      <button
                        onClick={copyToClipboard}
                        className="btn-secondary flex items-center gap-2 text-xs"
                      >
                        <Copy size={14} />
                        Copy text
                      </button>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                      {/* Resume Preview */}
                      <div className="lg:col-span-2">
                        <div
                          id="resume-print-area"
                          ref={printRef}
                          className="bg-white rounded-xl p-8 text-gray-900"
                        >
                          <h1 className="text-2xl font-bold text-gray-900 mb-1">
                            {result.jobTitle || 'Tailored Resume'}
                          </h1>
                          {result.company && (
                            <p className="text-gray-600 text-sm mb-6">Optimized for {result.company}</p>
                          )}

                          {result.tailoredResumeSections.map((section, i) => (
                            <div key={i}>
                              <h2 className="text-sm uppercase tracking-widest text-gray-500 font-semibold mt-6 mb-3 border-b border-gray-200 pb-1">
                                {section.title}
                              </h2>
                              <ResumeSectionView section={section} />
                            </div>
                          ))}
                        </div>
                        <p className="text-xs text-gray-600 mt-2 flex items-center gap-1">
                          <AlertCircle size={12} />
                          Review AI edits before submitting.
                        </p>
                      </div>

                      {/* Changes Panel */}
                      <div className="card p-5">
                        <h3 className="text-sm font-semibold text-white mb-3">Changes Made</h3>
                        {result.resumeChanges?.length > 0 ? (
                          <ol className="space-y-2">
                            {result.resumeChanges.map((change, i) => (
                              <li key={i} className="text-xs text-gray-400 flex gap-2">
                                <span className="text-gray-600 shrink-0">{i + 1}.</span>
                                {change}
                              </li>
                            ))}
                          </ol>
                        ) : (
                          <p className="text-xs text-gray-600">No specific changes recorded.</p>
                        )}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* History sidebar */}
        <div
          className="w-72 shrink-0 border-l overflow-y-auto hidden lg:block"
          style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
        >
          <div className="px-4 py-4 border-b" style={{ borderColor: 'var(--border)' }}>
            <p className="text-[10px] font-semibold text-gray-600 uppercase tracking-widest">History</p>
          </div>
          <div className="p-3 space-y-2">
            {history?.length === 0 && (
              <div className="text-center py-8">
                <p className="text-gray-600 text-xs mb-4">No analyses yet.</p>
                <p className="text-gray-600 text-xs">Paste a job description to see how you match up.</p>
                {!result && (
                  <button
                    onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
                    className="text-indigo-400 hover:text-indigo-300 text-xs mt-3 inline-block transition"
                  >
                    Start First Analysis
                  </button>
                )}
              </div>
            )}
            {history?.map((item) => (
              <button
                key={item.id}
                onClick={() => {
                  api.get(`/analysis/${item.id}`).then((r) => {
                    setResult(r.data.data)
                    setActiveTab('gap')
                  })
                }}
                className="w-full text-left card p-3 hover:bg-white/5 transition text-left"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-white truncate">{item.jobTitle}</p>
                    {item.company && (
                      <p className="text-xs text-gray-600 truncate">{item.company}</p>
                    )}
                    <p className="text-[10px] text-gray-700 mt-1">
                      {format(new Date(item.createdAt), 'MMM d, yyyy')}
                    </p>
                  </div>
                  <div className="text-right">
                    <span
                      className={`text-xs font-bold ${
                        item.readinessScore >= 70
                          ? 'text-emerald-400'
                          : item.readinessScore >= 50
                          ? 'text-amber-400'
                          : 'text-red-400'
                      }`}
                    >
                      {item.readinessScore}
                    </span>
                    {item.hasResumeTailoring && (
                      <span className="block text-[9px] text-gray-600 mt-0.5">with resume</span>
                    )}
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      </div>
    </AppShell>
  )
}
