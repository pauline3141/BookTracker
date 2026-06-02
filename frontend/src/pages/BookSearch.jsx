import { useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client'

export default function BookSearch() {
    const [query, setQuery] = useState('')
    const [results, setResults] = useState([])
    const [offset, setOffset] = useState(0)
    const [loading, setLoading] = useState(false)
    const [hasMore, setHasMore] = useState(false)

    const search = () => {
        if (!query.trim()) return
        setLoading(true)
        setOffset(0)
        client.get(`/books/search?q=${query}&offset=0`)
            .then(res => {
                setResults(res.data)
                setOffset(10)
                setHasMore(res.data.length === 10)
            })
            .finally(() => setLoading(false))
    }

    const loadMore = () => {
        setLoading(true)
        client.get(`/books/search?q=${query}&offset=${offset}`)
            .then(res => {
                setResults(prev => [...prev, ...res.data])
                setOffset(prev => prev + 10)
                setHasMore(res.data.length === 10)
            })
            .finally(() => setLoading(false))
    }

    return (
        <div>
            <nav>
                <Link to="/">Meine Regale</Link>
                <Link to="/search">Bücher suchen</Link>
            </nav>
            <div className="container">
                <h1>Bücher suchen</h1>
                <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                    <input
                        value={query}
                        onChange={e => setQuery(e.target.value)}
                        onKeyDown={e => e.key === 'Enter' && search()}
                        placeholder="Titel, Autor, ISBN..."
                    />
                    <button onClick={search}>Suchen</button>
                </div>

                {results.map((book, i) => (
                    <div key={i} className="card" style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
                        {book.coverUrl ? (
                            <img
                                src={book.coverUrl}
                                alt={book.title}
                                style={{ width: '60px', height: '90px', objectFit: 'cover', borderRadius: '4px' }}
                            />
                        ) : (
                            <div style={{
                                width: '60px', height: '90px', background: '#ddd',
                                borderRadius: '4px', display: 'flex',
                                alignItems: 'center', justifyContent: 'center',
                                fontSize: '0.7rem', color: '#999', textAlign: 'center'
                            }}>
                                Kein Cover
                            </div>
                        )}
                        <div>
                            <h2>{book.title}</h2>
                            <p>{book.author}</p>
                            {book.isbn && <p style={{ color: '#666', fontSize: '0.85rem' }}>ISBN: {book.isbn}</p>}
                            {book.publishYear && <p style={{ color: '#666', fontSize: '0.85rem' }}>Jahr: {book.publishYear}</p>}
                        </div>
                    </div>
                ))}

                {hasMore && (
                    <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                        <button onClick={loadMore} disabled={loading}>
                            {loading ? 'Lädt...' : 'Mehr laden'}
                        </button>
                    </div>
                )}
            </div>
        </div>
    )
}