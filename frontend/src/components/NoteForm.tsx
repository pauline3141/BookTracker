import { useState } from 'react'
import { addNote } from '../api/bookTrackerApi'
import type { BookNote, NoteFormInput } from '../types'

type NoteFormProps = {
    entryId: number
    onAdd: (note: BookNote) => void
}

const EMPTY: NoteFormInput = {
    content: '',
    pageReference: '',
}

export default function NoteForm({ entryId, onAdd }: NoteFormProps) {
    const [form, setForm] = useState<NoteFormInput>(EMPTY)
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState<string | null>(null)

    function handleChange(
        event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) {
        const { name, value } = event.target
        setForm(prev => ({ ...prev, [name]: value }))
    }

    function blockInvalidNumberKeys(event: React.KeyboardEvent<HTMLInputElement>) {
        if (['e', 'E', '+', '-'].includes(event.key)) {
            event.preventDefault()
        }
    }

    async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()
        if (!form.content.trim()) return
        setSubmitting(true)
        setError(null)
        try {
            const note = await addNote(entryId, {
                content: form.content,
                pageReference: form.pageReference ? Number(form.pageReference) : null
            })
            setForm(EMPTY)
            onAdd(note)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Fehler')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <form onSubmit={handleSubmit} style={{ marginTop: '1rem' }}>
            <h3>Notiz hinzufügen</h3>
            {error && <p className="error">Fehler: {error}</p>}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                <input
                    type="number"
                    name="pageReference"
                    value={form.pageReference}
                    onChange={handleChange}
                    onKeyDown={blockInvalidNumberKeys}
                    min={0}
                    placeholder="Seite (optional)"
                />
                <textarea
                    name="content"
                    value={form.content}
                    onChange={handleChange}
                    placeholder="Notiz..."
                    rows={3}
                    required
                />
                <button type="submit" disabled={submitting}>
                    {submitting ? 'Speichere...' : 'Hinzufügen'}
                </button>
            </div>
        </form>
    )
}