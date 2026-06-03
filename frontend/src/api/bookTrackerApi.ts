import type { Book, Shelf, ShelfEntry, BookNote, ShelfRequest, BookNoteRequest } from '../types'

const BASE = '/api'

async function handle<T>(response: Response): Promise<T> {
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }
    return (await response.json()) as T
}

// Shelves
export function getShelves(): Promise<Shelf[]> {
    return fetch(`${BASE}/shelves`).then(res => handle<Shelf[]>(res))
}

export function createShelf(request: ShelfRequest): Promise<Shelf> {
    return fetch(`${BASE}/shelves`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
    }).then(res => handle<Shelf>(res))
}

export function getShelf(id: number): Promise<Shelf> {
    return fetch(`${BASE}/shelves/${id}`).then(res => handle<Shelf>(res))
}

// ShelfEntries
export function getEntries(shelfId: number): Promise<ShelfEntry[]> {
    return fetch(`${BASE}/shelves/${shelfId}/entries`).then(res => handle<ShelfEntry[]>(res))
}

export function addEntry(shelfId: number, bookId: number, totalPages: number): Promise<ShelfEntry> {
    return fetch(`${BASE}/shelves/${shelfId}/entries`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ bookId, totalPages })
    }).then(res => handle<ShelfEntry>(res))
}

export function updateProgress(shelfId: number, entryId: number, currentPage: number, totalPages: number): Promise<ShelfEntry> {
    return fetch(`${BASE}/shelves/${shelfId}/entries/${entryId}/progress`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ currentPage, totalPages })
    }).then(res => handle<ShelfEntry>(res))
}

// Books
export function createBook(book: Omit<Book, 'id'>): Promise<Book> {
    return fetch(`${BASE}/books`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(book)
    }).then(res => handle<Book>(res))
}

export function searchBooks(query: string, offset: number): Promise<Book[]> {
    return fetch(`${BASE}/books/search?q=${encodeURIComponent(query)}&offset=${offset}`)
        .then(res => handle<Book[]>(res))
}

// Notes
export function getNotes(entryId: number): Promise<BookNote[]> {
    return fetch(`${BASE}/entries/${entryId}/notes`).then(res => handle<BookNote[]>(res))
}

export function addNote(entryId: number, request: BookNoteRequest): Promise<BookNote> {
    return fetch(`${BASE}/entries/${entryId}/notes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
    }).then(res => handle<BookNote>(res))
}