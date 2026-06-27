import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import ConfirmDialog from '../components/ConfirmDialog'
import { getShelves, createShelf, deleteShelf, getEntries } from '../api/bookTrackerApi'
import type { Shelf } from '../types'

export default function ShelfList() {
    const [shelves, setShelves] = useState<Shelf[]>([])
    const [covers, setCovers] = useState<Record<number, string[]>>({})
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [showForm, setShowForm] = useState(false)
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [shelfToDelete, setShelfToDelete] = useState<Shelf | null>(null)
    const navigate = useNavigate()

    useEffect(() => {
        let ignore = false
        getShelves()
            .then(data => {
                if (!ignore) {
                    setShelves(data)
                    setError(null)
                    data.forEach(shelf => {
                        getEntries(shelf.id)
                            .then(entries => {
                                if (!ignore) {
                                    const shelfCovers = entries
                                        .map(e => e.book.coverUrl)
                                        .filter((url): url is string => !!url)
                                        .slice(0, 4)
                                    setCovers(prev => ({ ...prev, [shelf.id]: shelfCovers }))
                                }
                            })
                            .catch(() => { /* Cover-Vorschau ist optional, Fehler hier ignorieren */ })
                    })
                }
            })
            .catch(err => { if (!ignore) setError(err instanceof Error ? err.message : 'Fehler') })
            .finally(() => { if (!ignore) setLoading(false) })
        return () => { ignore = true }
    }, [])

    const handleCreate = () => {
        if (!name.trim()) return
        createShelf({ name, description })
            .then(shelf => {
                setShelves(prev => [...prev, shelf])
                setName('')
                setDescription('')
                setShowForm(false)
            })
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
    }

    const confirmDelete = () => {
        if (!shelfToDelete) return
        deleteShelf(shelfToDelete.id)
            .then(() => setShelves(prev => prev.filter(s => s.id !== shelfToDelete.id)))
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler beim Löschen'))
            .finally(() => setShelfToDelete(null))
    }

    if (loading) return <p>Lade Regale ...</p>
    if (error) return <p className="error">Fehler: {error}</p>

    return (
        <div>
            <Navbar />
            <div className="container">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                    <h1 style={{ margin: 0 }}>Meine Regale</h1>
                    <button onClick={() => setShowForm(prev => !prev)}>
                        {showForm ? 'Abbrechen' : '+ Neues Regal'}
                    </button>
                </div>
                {showForm && (
                    <div className="card" style={{ marginBottom: '1.5rem' }}>
                        <h2>Neues Regal anlegen</h2>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '0.75rem' }}>
                            <input value={name} onChange={e => setName(e.target.value)} placeholder="Name des Regals" />
                            <input value={description} onChange={e => setDescription(e.target.value)} placeholder="Beschreibung (optional)" />
                            <button onClick={handleCreate} style={{ alignSelf: 'flex-start' }}>Anlegen</button>
                        </div>
                    </div>
                )}
                {shelves.map(shelf => {
                    const shelfCovers = covers[shelf.id] || []
                    return (
                        <div
                            key={shelf.id}
                            className="card card-clickable"
                            onClick={() => navigate(`/shelves/${shelf.id}`)}
                            style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1.25rem' }}
                        >
                            <div
                                style={{
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(2, 40px)',
                                    gridTemplateRows: 'repeat(2, 40px)',
                                    gap: '2px',
                                    flexShrink: 0,
                                    borderRadius: '4px',
                                    overflow: 'hidden',
                                    background: '#e5e7eb'
                                }}
                            >
                                {Array.from({ length: 4 }).map((_, i) => (
                                    shelfCovers[i] ? (
                                        <img
                                            key={i}
                                            src={shelfCovers[i]}
                                            alt=""
                                            style={{ width: '40px', height: '40px', objectFit: 'cover', display: 'block' }}
                                        />
                                    ) : (
                                        <div key={i} style={{ width: '40px', height: '40px', background: '#e5e7eb' }} />
                                    )
                                ))}
                            </div>

                            <div style={{ flex: 1 }}>
                                <h2>{shelf.name}</h2>
                                {shelf.description && (
                                    <p style={{ color: '#666', fontSize: '0.9rem' }}>{shelf.description}</p>
                                )}
                            </div>

                            <button
                                className="secondary"
                                onClick={e => {
                                    e.stopPropagation()
                                    setShelfToDelete(shelf)
                                }}
                                style={{ fontSize: '0.8rem', flexShrink: 0 }}
                            >
                                Löschen
                            </button>
                        </div>
                    )
                })}
            </div>

            {shelfToDelete && (
                <ConfirmDialog
                    title="Regal löschen"
                    message={`Möchtest du "${shelfToDelete.name}" wirklich löschen? Alle enthaltenen Bücher-Einträge werden ebenfalls entfernt.`}
                    onConfirm={confirmDelete}
                    onCancel={() => setShelfToDelete(null)}
                />
            )}
        </div>
    )
}