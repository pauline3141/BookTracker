import { useEffect, useState, useCallback } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from '../components/Navbar'
import ReadingProgress from '../components/ReadingProgress'
import NoteForm from '../components/NoteForm'
import { getShelf, getEntries, getNotes } from '../api/bookTrackerApi'
import type { Shelf, ShelfEntry, BookNote } from '../types'

export default function ShelfDetail() {
    const { id } = useParams<{ id: string }>()
    const [shelf, setShelf] = useState<Shelf | null>(null)
    const [entries, setEntries] = useState<ShelfEntry[]>([])
    const [notes, setNotes] = useState<Record<number, BookNote[]>>({})
    const [expandedEntry, setExpandedEntry] = useState<number | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const shelfId = Number(id)

    const load = useCallback(() => {
        let ignore = false
        Promise.all([getShelf(shelfId), getEntries(shelfId)])
            .then(([shelfData, entriesData]) => {
                if (!ignore) {
                    setShelf(shelfData)
                    setEntries(entriesData)
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

    if (loading) return <p>Lade Regal ...</p>
    if (error) return <p className="error">Fehler: {error}</p>

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1>{shelf?.name}</h1>
                {shelf?.description && (
                    <p style={{ color: '#666', marginBottom: '1.5rem' }}>{shelf.description}</p>
                )}
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
                                <button
                                    className="secondary"
                                    style={{ marginTop: '0.75rem', fontSize: '0.8rem' }}
                                    onClick={() => loadNotes(entry.id)}
                                >
                                    {expandedEntry === entry.id ? 'Notizen ausblenden' : 'Notizen anzeigen'}
                                </button>
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