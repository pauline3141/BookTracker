import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './index.css'
import ShelfList from './pages/ShelfList'
import ShelfDetail from './pages/ShelfDetail'
import BookSearch from './pages/BookSearch'
import BookDetail from './pages/BookDetail'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ShelfList />} />
                <Route path="/shelves/:id" element={<ShelfDetail />} />
                <Route path="/search" element={<BookSearch />} />
                <Route path="/books/detail" element={<BookDetail />} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)