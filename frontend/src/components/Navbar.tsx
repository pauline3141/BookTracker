import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'

export default function Navbar() {
    const [query, setQuery] = useState('')
    const [showLogout, setShowLogout] = useState(false)
    const navigate = useNavigate()
    const username = localStorage.getItem('username')

    const search = (e: React.FormEvent) => {
        e.preventDefault()
        if (query.trim()) {
            navigate(`/search?q=${encodeURIComponent(query)}`)
        }
    }

    const logout = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        navigate('/login')
    }

    const wider = username && username.length > 'Logout'.length ? username : 'Logout'

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">BookTracker</Link>
            </div>
            <div className="navbar-links">
                <Link to="/">Entdecken</Link>
                {username && <Link to="/shelves">Meine Regale</Link>}
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
                {username ? (
                    <div
                        onClick={showLogout ? logout : undefined}
                        onMouseEnter={() => setShowLogout(true)}
                        onMouseLeave={() => setShowLogout(false)}
                        style={{
                            position: 'relative',
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '0.4rem',
                            cursor: showLogout ? 'pointer' : 'default',
                            userSelect: 'none' as const,
                            color: showLogout ? '#e74c3c' : '#d4b896',
                            fontSize: '0.85rem',
                            border: `1px solid ${showLogout ? '#e74c3c' : '#d4b896'}`,
                            borderRadius: '4px',
                            padding: '0.25rem 0.6rem',
                            transition: 'color 0.2s, border-color 0.2s',
                        }}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="1.8"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            style={{ flexShrink: 0 }}
                        >
                            <circle cx="12" cy="8" r="4" />
                            <path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7" />
                        </svg>
                        <span style={{ position: 'relative', display: 'inline-flex' }}>
                            <span style={{ visibility: 'hidden', pointerEvents: 'none' }}>
                              {wider}
                            </span>
                            <span style={{ position: 'absolute' }}>
                              {showLogout ? 'Logout' : username}
                            </span>
                        </span>
                    </div>
                ) : (
                    <Link
                        to="/login"
                        style={{
                            color: '#d4b896',
                            fontSize: '0.85rem',
                            border: '1px solid #d4b896',
                            borderRadius: '4px',
                            padding: '0.25rem 0.6rem',
                            textDecoration: 'none',
                        }}
                    >
                        Login
                    </Link>
                )}
            </div>
        </nav>
    )
}