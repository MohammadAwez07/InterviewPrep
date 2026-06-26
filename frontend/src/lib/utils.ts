import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function difficultyColor(d: string) {
  switch (d?.toUpperCase()) {
    case 'EASY':   return 'tag-easy'
    case 'MEDIUM': return 'tag-medium'
    case 'HARD':   return 'tag-hard'
    default: return 'bg-white/5 text-gray-400'
  }
}

export function formatDuration(seconds: number) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}m ${s}s`
}
