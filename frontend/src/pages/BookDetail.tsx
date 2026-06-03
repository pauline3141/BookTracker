import { useLocation, useNavigate } from 'react-router-dom'
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

    const addToShelf = () => {
        if (!selectedShelf) return
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
                <div className="card book-card">
                    {book.coverUrl ? (
                        <img src={book.coverUrl} alt={book.title}
                             style={{ width: '120px', height: '180px', objectFit: 'cover', borderRadius: '6px' }} />
                    ) : (
                        <div className="book-cover-placeholder" style={{ width: '120px', height: '180px' }}>Kein Cover</div>
                    )}
                    <div style={{ flex: 1 }}>
                        <h1 style={{ fontSize: '1.5rem' }}>{book.title}</h1>
                        <p style={{ color: '#666', marginTop: '0.3rem' }}>{book.author}</p>
                        {book.isbn && <p className="book-meta">ISBN: {book.isbn}</p>}
                        {book.publishYear > 0 && <p className="book-meta">Erstmals erschienen: {book.publishYear}</p>}
                        {book.totalPages > 0 && <p className="book-meta">Seiten: {book.totalPages}</p>}
                    </div>
                </div>

                <div className="card" style={{ marginTop: '1.5rem' }}>
                    <h2>Ins Regal legen</h2>
                    {error && <p className="error">Fehler: {error}</p>}
                    {added ? (
                        <p style={{ color: '#1e8449', marginTop: '0.5rem' }}>✓ Erfolgreich hinzugefügt!</p>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '0.75rem' }}>
                            <div>
                                <label style={{ fontSize: '0.85rem', color: '#666', display: 'block', marginBottom: '0.3rem' }}>
                                    Regal
                                </label>
                                <select value={selectedShelf} onChange={e => setSelectedShelf(Number(e.target.value))}>
                                    {shelves.map(shelf => (
                                        <option key={shelf.id} value={shelf.id}>{shelf.name}</option>
                                    ))}
                                </select>
                            </div>
                            <button onClick={addToShelf} style={{ alignSelf: 'flex-start' }}>
                                Ins Regal legen
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}