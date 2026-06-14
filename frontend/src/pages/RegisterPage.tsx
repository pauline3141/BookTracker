import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../api/bookTrackerApi'
import type { AuthRequest } from '../types'

const EMPTY: AuthRequest = { username: '', password: '' }

export default function RegisterPage() {
    const [form, setForm] = useState<AuthRequest>(EMPTY)
    const [error, setError] = useState<string | null>(null)
    const [submitting, setSubmitting] = useState(false)
    const navigate = useNavigate()

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        const { name, value } = event.target
        setForm(prev => ({ ...prev, [name]: value }))
    }

    async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setSubmitting(true)
        setError(null)
        try {
            const response = await register(form)
            localStorage.setItem('token', response.token)
            localStorage.setItem('username', response.username)
            navigate('/')
        } catch (err) {
            setError(err instanceof Error && err.message.includes('409')
                ? 'Benutzername bereits vergeben'
                : 'Registrierung fehlgeschlagen')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f0ebe3' }}>
            <div className="card" style={{ width: '100%', maxWidth: '400px' }}>
                <h1 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>BookTracker</h1>
                <h2 style={{ marginBottom: '1rem' }}>Registrieren</h2>
                {error && <p className="error" style={{ marginBottom: '1rem' }}>{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                        <input
                            name="username"
                            value={form.username}
                            onChange={handleChange}
                            placeholder="Benutzername (min. 3 Zeichen)"
                            required
                        />
                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            placeholder="Passwort (min. 6 Zeichen)"
                            required
                        />
                        <button type="submit" disabled={submitting}>
                            {submitting ? 'Registrieren...' : 'Registrieren'}
                        </button>
                    </div>
                </form>
                <p style={{ textAlign: 'center', marginTop: '1rem', color: '#666', fontSize: '0.9rem' }}>
                    Bereits ein Konto? <Link to="/login">Anmelden</Link>
                </p>
            </div>
        </div>
    )
}