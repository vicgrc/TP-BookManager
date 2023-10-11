package com.jicay.bookmanagement.infrastructure.primary.web

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.primary.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.primary.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase
) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.toDomain())
    }

}