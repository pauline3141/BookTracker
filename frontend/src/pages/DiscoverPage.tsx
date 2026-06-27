import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { discoverBooks } from '../api/bookTrackerApi'
import type { Book } from '../types'

export default function DiscoverPage() {
    const [books, setBooks] = useState<Book[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [query, setQuery] = useState('')
    const navigate = useNavigate()

    const load = () => {
        setLoading(true)
        setError(null)
        discoverBooks()
            .then(setBooks)
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler beim Laden'))
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        load()
    }, [])

    const openDetail = (book: Book) => {
        navigate('/books/detail', { state: { book } })
    }

    const search = (e: React.FormEvent) => {
        e.preventDefault()
        if (query.trim()) {
            navigate(`/search?q=${encodeURIComponent(query)}`)
        }
    }

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1 style={{ marginBottom: '1.5rem' }}>Entdecke neue Bücher</h1>

                <form onSubmit={search} style={{ marginBottom: '2rem' }}>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <input
                            value={query}
                            onChange={e => setQuery(e.target.value)}
                            placeholder="Buchtitel, Autor oder ISBN suchen..."
                            style={{ fontSize: '1rem', padding: '0.7rem 1rem' }}
                        />
                        <button type="submit" style={{ padding: '0.7rem 1.4rem', flexShrink: 0 }}>
                            Suchen
                        </button>
                    </div>
                </form>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                    <h2 style={{ margin: 0, fontSize: '1.1rem', color: '#666' }}>Beliebt</h2>
                    <button className="secondary" onClick={load} disabled={loading}>
                        {loading ? 'Lädt...' : 'Neue Vorschläge'}
                    </button>
                </div>

                {error && <p className="error">Fehler: {error}</p>}
                {loading && books.length === 0 && <p>Lade Vorschläge ...</p>}

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))', gap: '1rem' }}>
                    {books.map((book, i) => (
                        <div
                            key={i}
                            className="card card-clickable"
                            onClick={() => openDetail(book)}
                            style={{ padding: '0.75rem', textAlign: 'center' }}
                        >
                            {book.coverUrl ? (
                                <img
                                    src={book.coverUrl}
                                    alt={book.title}
                                    style={{ width: '100%', height: '180px', objectFit: 'cover', borderRadius: '4px', marginBottom: '0.5rem' }}
                                />
                            ) : (
                                <div
                                    style={{
                                        width: '100%',
                                        height: '180px',
                                        background: '#e5e7eb',
                                        borderRadius: '4px',
                                        marginBottom: '0.5rem',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        color: '#9ca3af',
                                        fontSize: '0.75rem',
                                        padding: '0.5rem'
                                    }}
                                >
                                    Kein Cover
                                </div>
                            )}
                            <p style={{ fontSize: '0.85rem', fontWeight: 'bold', margin: 0 }}>{book.title}</p>
                            <p style={{ fontSize: '0.75rem', color: '#666', margin: '0.2rem 0 0' }}>{book.author}</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}