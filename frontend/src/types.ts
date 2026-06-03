export interface Book {
    id: number | null
    title: string
    author: string
    isbn: string | null
    coverUrl: string | null
    publishYear: number
    totalPages: number
}

export interface Shelf {
    id: number
    name: string
    description: string | null
    createdAt: string
}

export interface ShelfEntry {
    id: number
    shelfId: number
    book: Book
    addedAt: string
    currentPage: number
    totalPages: number
}

export interface BookNote {
    id: number
    content: string
    pageReference: number | null
    isPublic: boolean
}

export interface ShelfRequest {
    name: string
    description: string
}

export interface BookNoteRequest {
    content: string
    pageReference: number | null
    isPublic: boolean
}

export interface NoteFormInput {
    content: string
    pageReference: string
}
