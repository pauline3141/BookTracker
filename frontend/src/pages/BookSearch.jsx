import { useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client'

export default function BookSearch() {
    const [query, setQuery] = useState('')
    const [results, setResults] = useState([])

    const search = () => {
        client.get(`/books/search?q=${query}`).then(res => setResults(res.data))
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
            </div>
        </div>
    )
}