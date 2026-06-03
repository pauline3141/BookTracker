import { useEffect, useState, useCallback } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from '../components/Navbar'
import ReadingProgress from '../components/ReadingProgress'
import NoteForm from '../components/NoteForm'
import { getShelf, getEntries, getNotes, removeEntry, moveEntry, getShelves } from '../api/bookTrackerApi'
import type { Shelf, ShelfEntry, BookNote } from '../types'

export default function ShelfDetail() {
    const { id } = useParams<{ id: string }>()
    const [shelf, setShelf] = useState<Shelf | null>(null)
    const [entries, setEntries] = useState<ShelfEntry[]>([])
    const [allShelves, setAllShelves] = useState<Shelf[]>([])
    const [notes, setNotes] = useState<Record<number, BookNote[]>>({})
    const [expandedEntry, setExpandedEntry] = useState<number | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const shelfId = Number(id)

    const load = useCallback(() => {
        let ignore = false
        Promise.all([getShelf(shelfId), getEntries(shelfId), getShelves()])
            .then(([shelfData, entriesData, shelvesData]) => {
                if (!ignore) {
                    setShelf(shelfData)
                    setEntries(entriesData)
                    setAllShelves(shelvesData.filter(s => s.id !== shelfId))
                    setError(null)
                }
            })
            .catch(err => { if (!ignore) setError(err instanceof Error ? err.message : 'Fehler') })
            .finally(() => { if (!ignore) setLoading(false) })
        return () => { ignore = true }
    }, [shelfId])

    useEffect(() => {
        load()
    }, [load])

    const loadNotes = (entryId: number) => {
        if (expandedEntry === entryId) {
            setExpandedEntry(null)
            return
        }
        setExpandedEntry(entryId)
        if (!notes[entryId]) {
            getNotes(entryId)
                .then(data => setNotes(prev => ({ ...prev, [entryId]: data })))
                .catch(err => console.error(err))
        }
    }

    const handleNoteAdded = (entryId: number, note: BookNote) => {
        setNotes(prev => ({ ...prev, [entryId]: [...(prev[entryId] || []), note] }))
    }

    const handleRemove = (entryId: number) => {
        removeEntry(shelfId, entryId)
            .then(() => setEntries(prev => prev.filter(e => e.id !== entryId)))
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
    }

    const handleMove = (entryId: number, targetShelfId: number) => {
        moveEntry(shelfId, entryId, targetShelfId)
            .then(() => setEntries(prev => prev.filter(e => e.id !== entryId)))
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
    }

    // Abgeleitete Werte
    const totalBooks = entries.length
    const booksWithProgress = entries.filter(e => e.currentPage > 0).length
    const booksFinished = entries.filter(e => e.totalPages > 0 && e.currentPage >= e.totalPages).length

    if (loading) return <p>Lade Regal ...</p>
    if (error) return <p className="error">Fehler: {error}</p>

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1>{shelf?.name}</h1>
                {shelf?.description && (
                    <p style={{ color: '#666', marginBottom: '1rem' }}>{shelf.description}</p>
                )}

                <div style={{ display: 'flex', gap: '2rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
                    <div>
                        <span style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#3d2b1f' }}>{totalBooks}</span>
                        <span style={{ fontSize: '0.8rem', color: '#666', display: 'block' }}>Bücher gesamt</span>
                    </div>
                    <div>
                        <span style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#3d2b1f' }}>{booksWithProgress}</span>
                        <span style={{ fontSize: '0.8rem', color: '#666', display: 'block' }}>Angefangen</span>
                    </div>
                    <div>
                        <span style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#3d2b1f' }}>{booksFinished}</span>
                        <span style={{ fontSize: '0.8rem', color: '#666', display: 'block' }}>Fertig gelesen</span>
                    </div>
                </div>

                {entries.length === 0 && (
                    <p style={{ color: '#999' }}>Noch keine Bücher in diesem Regal.</p>
                )}
                {entries.map(entry => (
                    <div key={entry.id} className="card">
                        <div className="book-card">
                            {entry.book.coverUrl ? (
                                <img src={entry.book.coverUrl} alt={entry.book.title} className="book-cover" />
                            ) : (
                                <div className="book-cover-placeholder">Kein Cover</div>
                            )}
                            <div style={{ flex: 1 }}>
                                <h2>{entry.book.title}</h2>
                                <p style={{ color: '#666', fontSize: '0.9rem' }}>{entry.book.author}</p>
                                <ReadingProgress
                                    entry={entry}
                                    onUpdate={updated => setEntries(entries.map(e => e.id === updated.id ? updated : e))}
                                />
                                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.75rem', flexWrap: 'wrap', alignItems: 'center' }}>
                                    <button
                                        className="secondary"
                                        style={{ fontSize: '0.8rem' }}
                                        onClick={() => loadNotes(entry.id)}
                                    >
                                        {expandedEntry === entry.id ? 'Notizen ausblenden' : 'Notizen anzeigen'}
                                    </button>
                                    {allShelves.length > 0 && (
                                        <select
                                            style={{ fontSize: '0.8rem', padding: '0.3rem 0.5rem' }}
                                            defaultValue=""
                                            onChange={e => {
                                                if (e.target.value) handleMove(entry.id, Number(e.target.value))
                                            }}
                                        >
                                            <option value="" disabled>Verschieben nach...</option>
                                            {allShelves.map(s => (
                                                <option key={s.id} value={s.id}>{s.name}</option>
                                            ))}
                                        </select>
                                    )}
                                    <button
                                        className="danger"
                                        style={{ fontSize: '0.8rem' }}
                                        onClick={() => handleRemove(entry.id)}
                                    >
                                        Entfernen
                                    </button>
                                </div>
                            </div>
                        </div>
                        {expandedEntry === entry.id && (
                            <div style={{ marginTop: '1rem', borderTop: '1px solid #eee', paddingTop: '1rem' }}>
                                {(notes[entry.id] || []).length === 0 && (
                                    <p style={{ color: '#999', fontSize: '0.85rem' }}>Noch keine Notizen.</p>
                                )}
                                {(notes[entry.id] || []).map(note => (
                                    <div key={note.id} style={{ marginBottom: '0.75rem', padding: '0.5rem', background: '#f9f5f0', borderRadius: '4px' }}>
                                        {note.pageReference && (
                                            <p style={{ fontSize: '0.75rem', color: '#999' }}>Seite {note.pageReference}</p>
                                        )}
                                        <p style={{ fontSize: '0.9rem' }}>{note.content}</p>
                                    </div>
                                ))}
                                <NoteForm entryId={entry.id} onAdd={note => handleNoteAdded(entry.id, note)} />
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    )
}