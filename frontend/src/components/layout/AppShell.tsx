'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { isAuthenticated } from '@/lib/auth'
import Sidebar from './Sidebar'

export default function AppShell({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const [mounted, setMounted] = useState(false)
  const [authenticated, setAuthenticated] = useState(false)

  useEffect(() => {
    setMounted(true)
    const auth = isAuthenticated()
    setAuthenticated(auth)
    if (!auth) {
      router.replace('/auth/login')
    }
  }, [router])

  // Prevent hydration mismatch by not rendering until mounted
  if (!mounted) {
    return (
      <div className="flex min-h-screen" style={{ background: 'var(--bg-primary)' }}>
        <div className="w-60 shrink-0 border-r" style={{ background: 'var(--bg-card)', borderColor: 'var(--border)' }} />
        <main className="flex-1 overflow-auto" />
      </div>
    )
  }

  if (!authenticated) return null

  return (
    <div className="flex min-h-screen" style={{ background: 'var(--bg-primary)' }}>
      <Sidebar />
      <main className="flex-1 overflow-auto">
        {children}
      </main>
    </div>
  )
}
