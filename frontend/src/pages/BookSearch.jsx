import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import client from '../api/client'

export default function BookSearch() {
    const [searchParams] = useSearchParams()
    const [query, setQuery] = useState(searchParams.get('q') || '')
    const [results, setResults] = useState([])
    const [offset, setOffset] = useState(0)
    const [loading, setLoading] = useState(false)
    const [hasMore, setHasMore] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        const q = searchParams.get('q')
        if (q) {
            setQuery(q)
            doSearch(q, 0)
        }
    }, [searchParams])

    const doSearch = (q, off) => {
        setLoading(true)
        client.get(`/books/search?q=${q}&offset=${off}`)
            .then(res => {
                setResults(res.data)
                setOffset(10)
                setHasMore(res.data.length === 10)
            })
            .finally(() => setLoading(false))
    }

    const search = () => {
        if (!query.trim()) return
        doSearch(query, 0)
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

    const openDetail = (book) => {
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
                        <button onClick={loadMore} disabled={loading}>
                            {loading ? 'Lädt...' : 'Mehr laden'}
                        </button>
                    </div>
                )}
            </div>
        </div>
    )
}