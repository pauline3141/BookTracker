import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'

export default function Navbar() {
    const [query, setQuery] = useState('')
    const navigate = useNavigate()

    const search = (e: React.FormEvent) => {
        e.preventDefault()
        if (query.trim()) {
            navigate(`/search?q=${encodeURIComponent(query)}`)
        }
    }

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">BookTracker</Link>
            </div>
            <div className="navbar-links">
                <Link to="/">Meine Regale</Link>
                <form onSubmit={search} className="navbar-search">
                    <input
                        value={query}
                        onChange={e => setQuery(e.target.value)}
                        placeholder="Bücher suchen..."
                        className="navbar-search-input"
                    />
                    <button type="submit" className="navbar-search-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="11" cy="11" r="8" />
                            <line x1="21" y1="21" x2="16.65" y2="16.65" />
                        </svg>
                    </button>
                </form>
            </div>
        </nav>
    )
}