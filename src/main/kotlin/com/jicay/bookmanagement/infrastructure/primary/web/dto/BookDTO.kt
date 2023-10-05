package com.jicay.bookmanagement.infrastructure.primary.web.dto

import com.jicay.bookmanagement.domain.model.Book

data class BookDTO(val name: String, val author: String)

fun Book.toDto() = BookDTO(
    name = this.name,
    author = this.author
)