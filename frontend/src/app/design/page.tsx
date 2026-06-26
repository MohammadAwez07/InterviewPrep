'use client'

import AppShell from '@/components/layout/AppShell'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import { useState, useCallback, useRef } from 'react'
import dynamic from 'next/dynamic'
import type { Node, Edge } from 'reactflow'
import { Plus, Save, Trash2, Pen } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

const ReactFlow = dynamic(
  () => import('reactflow').then((mod) => mod.default),
  { ssr: false, loading: () => <div className="flex-1 animate-pulse" style={{ background: 'var(--bg-card)' }} /> }
)

// Lazily import controls & background for ReactFlow
const Controls = dynamic(() => import('reactflow').then((m) => m.Controls), { ssr: false })
const Background = dynamic(() => import('reactflow').then((m) => m.Background), { ssr: false })
const MiniMap = dynamic(() => import('reactflow').then((m) => m.MiniMap), { ssr: false })
import 'reactflow/dist/style.css'

interface DesignSession {
  id: string
  title: string
  updatedAt: string
  canvasData: { nodes: Node[]; edges: Edge[] }
}

const PREBUILT_NODES: Array<{ label: string; type: string }> = [
  { label: 'Client', type: 'client' },
  { label: 'API Gateway', type: 'gateway' },
  { label: 'Load Balancer', type: 'lb' },
  { label: 'Service', type: 'service' },
  { label: 'Database', type: 'db' },
  { label: 'Cache (Redis)', type: 'cache' },
  { label: 'Message Queue', type: 'queue' },
  { label: 'CDN', type: 'cdn' },
]

export default function DesignPage() {
  const qc = useQueryClient()
  const [selected, setSelected] = useState<DesignSession | null>(null)
  const [nodes, setNodes] = useState<Node[]>([])
  const [edges, setEdges] = useState<Edge[]>([])
  const nodeId = useRef(1)

  const { data: sessions } = useQuery<DesignSession[]>({
    queryKey: ['design-sessions'],
    queryFn: () => api.get('/design/sessions').then((r) => r.data.data),
  })

  const createMutation = useMutation({
    mutationFn: () =>
      api.post('/design/sessions', { title: 'New Design', canvasData: { nodes: [], edges: [] } }),
    onSuccess: (res) => {
      qc.invalidateQueries({ queryKey: ['design-sessions'] })
      const s = res.data.data
      setSelected(s)
      setNodes(s.canvasData.nodes || [])
      setEdges(s.canvasData.edges || [])
    },
  })

  const saveMutation = useMutation({
    mutationFn: () =>
      api.put(`/design/sessions/${selected!.id}`, {
        title: selected!.title,
        canvasData: { nodes, edges },
      }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['design-sessions'] }),
  })

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/design/sessions/${id}`),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['design-sessions'] })
      setSelected(null)
      setNodes([])
      setEdges([])
    },
  })

  const openSession = (s: DesignSession) => {
    setSelected(s)
    setNodes(s.canvasData?.nodes || [])
    setEdges(s.canvasData?.edges || [])
  }

  const addNode = useCallback(
    (label: string) => {
      const id = `node-${nodeId.current++}`
      setNodes((ns) => [
        ...ns,
        {
          id,
          data: { label },
          position: { x: 150 + Math.random() * 300, y: 100 + Math.random() * 200 },
        },
      ])
    },
    []
  )

  return (
    <AppShell>
      <div className="flex" style={{ height: '100vh', overflow: 'hidden' }}>
        {/* Session list sidebar */}
        <div
          className="w-56 shrink-0 border-r flex flex-col"
          style={{ background: 'var(--bg-card)', borderColor: 'var(--border)' }}
        >
          <div className="px-4 py-4 border-b" style={{ borderColor: 'var(--border)' }}>
            <p className="text-[10px] font-semibold text-gray-600 uppercase tracking-widest mb-3">
              Design Sessions
            </p>
            <button
              onClick={() => createMutation.mutate()}
              className="btn-primary w-full flex items-center justify-center gap-1.5 py-2"
            >
              <Plus size={13} /> New Session
            </button>
          </div>
          <div className="flex-1 overflow-y-auto p-2 space-y-0.5">
            {sessions?.map((s) => (
              <button
                key={s.id}
                onClick={() => openSession(s)}
                className={`w-full text-left px-3 py-2.5 rounded-xl text-xs transition ${
                  selected?.id === s.id
                    ? 'bg-indigo-500/12 text-indigo-300 ring-glow'
                    : 'text-gray-500 hover:text-gray-300 hover:bg-white/5'
                }`}
              >
                <div className="font-medium text-white truncate">{s.title}</div>
                <div className="text-gray-600 text-[10px] mt-0.5">
                  {formatDistanceToNow(new Date(s.updatedAt), { addSuffix: true })}
                </div>
              </button>
            ))}
            {!sessions?.length && (
              <p className="text-center text-gray-700 text-xs py-8">No sessions yet</p>
            )}
          </div>
        </div>

        {/* Canvas area */}
        <div className="flex-1 flex flex-col" style={{ background: 'var(--bg-primary)' }}>
          {selected ? (
            <>
              {/* Toolbar */}
              <div
                className="flex items-center gap-1.5 px-4 py-2.5 border-b flex-wrap"
                style={{ borderColor: 'var(--border)', background: 'var(--bg-card)' }}
              >
                <span className="text-sm font-semibold text-white mr-2">{selected.title}</span>
                {PREBUILT_NODES.map((n) => (
                  <button
                    key={n.type}
                    onClick={() => addNode(n.label)}
                    className="px-2.5 py-1 bg-white/5 hover:bg-white/10 border border-white/10 text-gray-300 rounded-lg text-xs transition"
                  >
                    + {n.label}
                  </button>
                ))}
                <div className="ml-auto flex gap-2">
                  <button
                    onClick={() => saveMutation.mutate()}
                    disabled={saveMutation.isPending}
                    className="btn-primary flex items-center gap-1.5 py-1.5 px-4"
                  >
                    <Save size={13} />
                    {saveMutation.isPending ? 'Saving…' : 'Save'}
                  </button>
                  <button
                    onClick={() => deleteMutation.mutate(selected.id)}
                    className="flex items-center gap-1 px-3 py-1.5 bg-red-500/10 hover:bg-red-500/20 text-red-400 rounded-lg text-xs border border-red-500/20 transition"
                  >
                    <Trash2 size={13} />
                  </button>
                </div>
              </div>

              {/* Flow canvas */}
              <div className="flex-1" style={{ background: 'var(--bg-primary)' }}>
                <ReactFlow
                  nodes={nodes}
                  edges={edges}
                  onNodesChange={(changes) => {
                    import('reactflow').then(({ applyNodeChanges }) =>
                      setNodes((ns) => applyNodeChanges(changes, ns))
                    )
                  }}
                  onEdgesChange={(changes) => {
                    import('reactflow').then(({ applyEdgeChanges }) =>
                      setEdges((es) => applyEdgeChanges(changes, es))
                    )
                  }}
                  onConnect={(connection) => {
                    import('reactflow').then(({ addEdge }) =>
                      setEdges((es) => addEdge({ ...connection, id: `e-${Date.now()}` }, es))
                    )
                  }}
                  fitView
                  style={{ background: 'transparent' }}
                >
                  <Controls />
                  <MiniMap style={{ background: '#0a0e1a', borderRadius: 10 }} />
                  <Background color="rgba(99,102,241,0.08)" gap={24} />
                </ReactFlow>
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center flex-col gap-4">
              <div className="w-16 h-16 rounded-2xl bg-indigo-500/10 flex items-center justify-center">
                <Pen size={28} className="text-indigo-400" strokeWidth={1.5} />
              </div>
              <div className="text-center">
                <p className="text-white font-semibold mb-1">No session selected</p>
                <p className="text-gray-500 text-sm mb-5">Create a new session to start designing</p>
                <button
                  onClick={() => createMutation.mutate()}
                  className="btn-primary flex items-center gap-2 mx-auto"
                >
                  <Plus size={15} /> New Session
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </AppShell>
  )
}
