import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { searchBooks } from '../api/bookTrackerApi'
import type { Book } from '../types'

export default function BookSearch() {
    const [searchParams] = useSearchParams()
    const [query, setQuery] = useState(searchParams.get('q') || '')
    const [results, setResults] = useState<Book[]>([])
    const [offset, setOffset] = useState(0)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [hasMore, setHasMore] = useState(false)
    const navigate = useNavigate()

    const doSearch = (q: string, off: number) => {
        setLoading(true)
        setError(null)
        searchBooks(q, off)
            .then(data => {
                if (off === 0) setResults(data)
                else setResults(prev => [...prev, ...data])
                setOffset(off + 10)
                setHasMore(data.length === 10)
            })
            .catch(err => setError(err instanceof Error ? err.message : 'Fehler'))
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        const q = searchParams.get('q')
        if (q) {
            setQuery(q)
            doSearch(q, 0)
        }
    }, [searchParams])

    const search = () => {
        if (!query.trim()) return
        doSearch(query, 0)
    }

    const openDetail = (book: Book) => {
        navigate('/books/detail', { state: { book } })
    }

    return (
        <div>
            <Navbar />
            <div className="container">
                <h1>Bücher suchen</h1>
                <div className="search-bar">
                    <input
                        value={query}
                        onChange={e => setQuery(e.target.value)}
                        onKeyDown={e => e.key === 'Enter' && search()}
                        placeholder="Titel, Autor, ISBN..."
                    />
                    <button onClick={search}>Suchen</button>
                </div>
                {error && <p className="error">Fehler: {error}</p>}
                {results.map((book, i) => (
                    <div
                        key={i}
                        className="card card-clickable book-card"
                        onClick={() => openDetail(book)}
                    >
                        {book.coverUrl ? (
                            <img src={book.coverUrl} alt={book.title} className="book-cover" />
                        ) : (
                            <div className="book-cover-placeholder">Kein Cover</div>
                        )}
                        <div className="book-info">
                            <h2>{book.title}</h2>
                            <p>{book.author}</p>
                            {book.isbn && <p className="book-meta">ISBN: {book.isbn}</p>}
                            {book.publishYear > 0 && <p className="book-meta">Jahr: {book.publishYear}</p>}
                            {book.totalPages > 0 && <p className="book-meta">Seiten: {book.totalPages}</p>}
                        </div>
                    </div>
                ))}
                {hasMore && (
                    <div className="load-more">
                        <button onClick={() => doSearch(query, offset)} disabled={loading}>
                            {loading ? 'Lädt...' : 'Mehr laden'}
                        </button>
                    </div>
                )}
            </div>
        </div>
    )
}