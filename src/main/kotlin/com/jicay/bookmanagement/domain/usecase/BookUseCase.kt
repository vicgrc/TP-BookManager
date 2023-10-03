package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book

class BookUseCase {
    fun getAllBooks(): List<Book> {
        return listOf(
            Book("Les Misérables", "Victor Hugo"),
            Book("Hamlet", "William Shakespeare")
        ).sortedBy {
            it.name.lowercase()
        }
    }
}