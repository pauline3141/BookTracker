import type { Book, Shelf, ShelfEntry, BookNote, ShelfRequest, BookNoteRequest, AuthRequest, AuthResponse } from '../types'

const BASE = '/api'

function getToken(): string | null {
    return localStorage.getItem('token')
}

function authHeader(): Record<string, string> {
    const token = getToken()
    return token ? { 'Authorization': `Bearer ${token}` } : {}
}

async function handle<T>(response: Response): Promise<T> {
    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        window.location.href = '/login'
        throw new Error(`HTTP ${response.status}`)
    }
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }
    return (await response.json()) as T
}

function handleVoid(response: Response): void {
    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        window.location.href = '/login'
        throw new Error(`HTTP ${response.status}`)
    }
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }
}

// Auth
export function login(request: AuthRequest): Promise<AuthResponse> {
    return fetch(`${BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
    }).then(res => handle<AuthResponse>(res))
}

export function register(request: AuthRequest): Promise<AuthResponse> {
    return fetch(`${BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
    }).then(res => handle<AuthResponse>(res))
}

// Shelves
export function getShelves(): Promise<Shelf[]> {
    return fetch(`${BASE}/shelves`, {
        headers: authHeader()
    }).then(res => handle<Shelf[]>(res))
}

export function createShelf(request: ShelfRequest): Promise<Shelf> {
    return fetch(`${BASE}/shelves`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify(request)
    }).then(res => handle<Shelf>(res))
}

export function getShelf(id: number): Promise<Shelf> {
    return fetch(`${BASE}/shelves/${id}`, {
        headers: authHeader()
    }).then(res => handle<Shelf>(res))
}

export function deleteShelf(id: number): Promise<void> {
    return fetch(`${BASE}/shelves/${id}`, {
        method: 'DELETE',
        headers: authHeader()
    }).then(res => handleVoid(res))
}

// ShelfEntries
export function getEntries(shelfId: number): Promise<ShelfEntry[]> {
    return fetch(`${BASE}/shelves/${shelfId}/entries`, {
        headers: authHeader()
    }).then(res => handle<ShelfEntry[]>(res))
}

export function addEntry(shelfId: number, bookId: number, totalPages: number): Promise<ShelfEntry> {
    return fetch(`${BASE}/shelves/${shelfId}/entries`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify({ bookId, totalPages })
    }).then(res => handle<ShelfEntry>(res))
}

export function updateProgress(shelfId: number, entryId: number, currentPage: number, totalPages: number): Promise<ShelfEntry> {
    return fetch(`${BASE}/shelves/${shelfId}/entries/${entryId}/progress`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify({ currentPage, totalPages })
    }).then(res => handle<ShelfEntry>(res))
}

export function moveEntry(shelfId: number, entryId: number, targetShelfId: number): Promise<ShelfEntry> {
    return fetch(`${BASE}/shelves/${shelfId}/entries/${entryId}/move`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify({ targetShelfId })
    }).then(res => handle<ShelfEntry>(res))
}

export function removeEntry(shelfId: number, entryId: number): Promise<void> {
    return fetch(`${BASE}/shelves/${shelfId}/entries/${entryId}`, {
        method: 'DELETE',
        headers: authHeader()
    }).then(res => handleVoid(res))
}

// Books
export function createBook(book: Omit<Book, 'id'>): Promise<Book> {
    return fetch(`${BASE}/books`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify(book)
    }).then(res => handle<Book>(res))
}

export function searchBooks(query: string, offset: number): Promise<Book[]> {
    return fetch(`${BASE}/books/search?q=${encodeURIComponent(query)}&offset=${offset}`, {
        headers: authHeader()
    }).then(res => handle<Book[]>(res))
}

export function discoverBooks(): Promise<Book[]> {
    return fetch(`${BASE}/books/discover`, {
        headers: authHeader()
    }).then(res => handle<Book[]>(res))
}

// Notes
export function getNotes(entryId: number): Promise<BookNote[]> {
    return fetch(`${BASE}/entries/${entryId}/notes`, {
        headers: authHeader()
    }).then(res => handle<BookNote[]>(res))
}

export function addNote(entryId: number, request: BookNoteRequest): Promise<BookNote> {
    return fetch(`${BASE}/entries/${entryId}/notes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader() },
        body: JSON.stringify(request)
    }).then(res => handle<BookNote>(res))
}

export function deleteNote(noteId: number): Promise<void> {
    return fetch(`${BASE}/notes/${noteId}`, {
        method: 'DELETE',
        headers: authHeader()
    }).then(res => handleVoid(res))
}