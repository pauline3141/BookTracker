import { useLocation, useNavigate, Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { getShelves, createBook, addEntry } from '../api/bookTrackerApi'
import type { Shelf, Book } from '../types'

export default function BookDetail() {
    const { state } = useLocation()
    const navigate = useNavigate()
    const book = state?.book as Book | undefined
    const [shelves, setShelves] = useState<Shelf[]>([])
    const [selectedShelf, setSelectedShelf] = useState<number | ''>('')
    const [added, setAdded] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        let ignore = false
        getShelves()
            .then(data => {
                if (!ignore) {
                    setShelves(data)
                    if (data.length > 0) setSelectedShelf(data[0].id)
                }
            })
            .catch(err => { if (!ignore) setError(err instanceof Error ? err.message : 'Fehler') })
            .finally(() => { if (!ignore) setLoading(false) })
        return () => { ignore = true }
    }, [])

    if (!book) {
        navigate('/search')
        return null
    }

    if (loading) return <p>Lade Regale ...</p>
    if (error) return <p className="error">Fehler: {error}</p>

    const hasShelves = shelves.length > 0

    const addToShelf = () => {
        if (!hasShelves || !selectedShelf) {
            setError('Bitte zuerst ein Regal anlegen, bevor du Bücher hinzufügst.')
            return
        }
        createBook({
            title: book.title,
            author: book.author,
            isbn: book.isbn,
            coverUrl: book.coverUrl,
            publishYear: book.publishYear ?? 0,
            totalPages: book.totalPages ?? 0
        })
            .then(saved => addEntry(selectedShelf as number, saved.id as number, saved.totalPages))
            .then(() => setAdded(true))
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
    }

    return (
        <div>
            <Navbar />
            <div className="container">
                <button className="secondary" onClick={() => navigate(-1)} style={{ marginBottom: '1.5rem' }}>
                    ← Zurück
                </button>

                <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'flex-start' }}>
                    {book.coverUrl ? (
                        <img
                            src={book.coverUrl}
                            alt={book.title}
                            style={{ width: '160px', height: '240px', objectFit: 'cover', borderRadius: '6px', flexShrink: 0 }}
                        />
                    ) : (
                        <div
                            style={{
                                width: '160px',
                                height: '240px',
                                background: '#e5e7eb',
                                borderRadius: '6px',
                                flexShrink: 0,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                color: '#9ca3af',
                                fontSize: '0.85rem',
                                textAlign: 'center',
                                padding: '1rem'
                            }}
                        >
                            Kein Cover verfügbar
                        </div>
                    )}

                    <div>
                        <h1>{book.title}</h1>
                        <p style={{ color: '#666' }}>{book.author}</p>
                        {book.publishYear && book.publishYear > 0 && (
                            <p style={{ color: '#999', fontSize: '0.9rem' }}>Erschienen: {book.publishYear}</p>
                        )}
                        {book.isbn && (
                            <p style={{ color: '#999', fontSize: '0.9rem' }}>ISBN: {book.isbn}</p>
                        )}
                    </div>
                </div>

                {added ? (
                    <p style={{ color: 'green', marginTop: '1.5rem' }}>
                        Buch wurde erfolgreich ins Regal gelegt.
                    </p>
                ) : !hasShelves ? (
                    <div className="error" style={{ marginTop: '1.5rem' }}>
                        <p>Du hast noch kein Regal angelegt.</p>
                        <p>
                            <Link to="/shelves/new">Jetzt ein Regal erstellen</Link>, um Bücher hinzuzufügen.
                        </p>
                    </div>
                ) : (
                    <div style={{ marginTop: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.75rem', maxWidth: '300px' }}>
                        <select
                            value={selectedShelf}
                            onChange={e => setSelectedShelf(Number(e.target.value))}
                        >
                            {shelves.map(shelf => (
                                <option key={shelf.id} value={shelf.id}>
                                    {shelf.name}
                                </option>
                            ))}
                        </select>
                        <button onClick={addToShelf}>
                            Ins Regal legen
                        </button>
                        {error && <p className="error">{error}</p>}
                    </div>
                )}
            </div>
        </div>
    )
}